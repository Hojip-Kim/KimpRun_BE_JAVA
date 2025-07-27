package kimp.cmc.dao.jdbc.impl;

import kimp.cmc.dao.jdbc.CmcBatchDao;
import kimp.cmc.dto.common.coin.CmcApiDataDto;
import kimp.cmc.dto.common.coin.CmcCoinInfoDataDto;
import kimp.cmc.dto.common.coin.CmcCoinMapDataDto;
import kimp.cmc.dto.common.coin.CmcDataPlatformDto;
import kimp.cmc.dto.common.exchange.CmcExchangeDto;
import kimp.cmc.dto.common.exchange.CmcExchangeDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class CmcBatchDaoImpl implements CmcBatchDao {

    private final JdbcTemplate jdbcTemplate;

    public CmcBatchDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void upsertCmcCoinMap(List<CmcCoinMapDataDto> coinMapList) {
        String sql = """
            INSERT INTO cmc_coin (cmc_coin_id, name, symbol, slug, is_active, status, is_mainnet, 
                                  first_historical_data, last_historical_data, registed_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (cmc_coin_id) 
            DO UPDATE SET
                name = EXCLUDED.name,
                symbol = EXCLUDED.symbol,
                slug = EXCLUDED.slug,
                is_active = EXCLUDED.is_active,
                status = EXCLUDED.status,
                is_mainnet = EXCLUDED.is_mainnet,
                first_historical_data = EXCLUDED.first_historical_data,
                last_historical_data = EXCLUDED.last_historical_data,
                updated_at = EXCLUDED.updated_at
            """;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        
        List<Object[]> batchArgs = coinMapList.stream()
            .map(coin -> new Object[]{
                coin.getId(),
                coin.getName(),
                coin.getSymbol(),
                coin.getSlug(),
                coin.getIsActive() != null ? coin.getIsActive() : false, // Boolean 타입이므로 getIsActive() 사용
                coin.getStatus() != null ? coin.getStatus() : true,
                coin.getPlatform() == null, // 플랫폼이 null이면 메인넷
                coin.getFirst_historical_data() != null ? LocalDateTime.parse(coin.getFirst_historical_data(), formatter) : now,
                coin.getLast_historical_data() != null ? LocalDateTime.parse(coin.getLast_historical_data(), formatter) : now,
                now,
                now
            })
            .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
        log.info("코인 맵 데이터 {} 건 업서트 완료", coinMapList.size());
    }

    @Override
    public void updateCmcCoinLatestInfo(List<CmcApiDataDto> latestInfoList) {
        LocalDateTime now = LocalDateTime.now();
        
        // 유효한 데이터만 필터링
        List<CmcApiDataDto> validItems = latestInfoList.stream()
            .filter(coin -> coin.getId() != null && coin.getCmcRank() != null && coin.getCmcRank() > 0)
            .toList();

        if (validItems.isEmpty()) {
            log.warn("업데이트할 유효한 코인 랭킹 데이터가 없습니다.");
            return;
        }

        // CmcRank 테이블 - UNIQUE 제약조건을 활용한 UPSERT
        String upsertRankSql = """
            INSERT INTO cmc_rank (cmc_coin_id, rank, registed_at, updated_at)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (cmc_coin_id) 
            DO UPDATE SET
                rank = EXCLUDED.rank,
                updated_at = EXCLUDED.updated_at
            RETURNING id
            """;

        // cmc_coin 테이블에 cmc_rank_id 업데이트
        String updateCoinRankSql = """
            UPDATE cmc_coin 
            SET cmc_rank_id = (
                SELECT id FROM cmc_rank WHERE cmc_coin_id = ?
            ),
            updated_at = ?
            WHERE cmc_coin_id = ?
            """;

        try {
            List<Object[]> batchArgs = validItems.stream()
                .map(coin -> new Object[]{
                    coin.getId(),
                    coin.getCmcRank(),
                    now,
                    now
                })
                .toList();

            jdbcTemplate.batchUpdate(upsertRankSql, batchArgs);
            log.info("코인 랭킹 데이터 {} 건 UPSERT 완료", validItems.size());
            
            // cmc_coin 테이블에 cmc_rank_id FK 업데이트
            List<Object[]> updateBatchArgs = validItems.stream()
                .map(coin -> new Object[]{
                    coin.getId(),
                    now,
                    coin.getId()
                })
                .toList();
            
            jdbcTemplate.batchUpdate(updateCoinRankSql, updateBatchArgs);
            log.info("코인 테이블 랭킹 FK {} 건 업데이트 완료", validItems.size());
            
        } catch (Exception e) {
            log.error("코인 랭킹 데이터 업데이트 중 오류 발생", e);
            throw e;
        }
    }

    @Override
    public void upsertCmcCoinInfo(List<CmcCoinInfoDataDto> coinInfoList) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        
        // CmcCoinInfo 테이블에 상세 정보 저장
        String coinInfoSql = """
            INSERT INTO cmc_coin_info (description, infinite_supply, is_fiat, last_updated, registed_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                description = EXCLUDED.description,
                infinite_supply = EXCLUDED.infinite_supply,
                is_fiat = EXCLUDED.is_fiat,
                last_updated = EXCLUDED.last_updated,
                updated_at = EXCLUDED.updated_at
            RETURNING id
            """;

        for (CmcCoinInfoDataDto info : coinInfoList) {
            if (info.getId() != null) {
                try {
                    // CmcCoinInfo 테이블에 상세 정보 저장
                    Long coinInfoId = jdbcTemplate.queryForObject(coinInfoSql, Long.class,
                        info.getDescription(),
                        info.getInfiniteSupply() != null ? info.getInfiniteSupply() : false,
                        false, // is_fiat - CoinInfoDataDto에 없으므로 기본값
                        now, // last_updated - CoinInfoDataDto에 없으므로 현재 시간 사용
                        now,
                        now
                    );
                    
                    // CmcCoin 테이블에 외래키와 logo 정보 업데이트
                    String updateCoinSql = """
                        UPDATE cmc_coin 
                        SET cmc_coin_info_id = ?,
                            logo = ?,
                            updated_at = ?
                        WHERE cmc_coin_id = ?
                        """;
                    
                    jdbcTemplate.update(updateCoinSql, 
                        coinInfoId,
                        info.getLogo(),
                        now,
                        info.getId());
                        
                } catch (Exception e) {
                    log.error("코인 상세 정보 처리 중 오류 발생 - coin_id: {}", info.getId(), e);
                }
            }
        }

        log.info("코인 상세 정보 {} 건 업서트 완료", coinInfoList.size());
    }

    @Override
    public List<Long> getCmcCoinIds(int limit) {
        String sql = "SELECT cmc_coin_id FROM cmc_coin ORDER BY cmc_coin_id LIMIT ?";
        return jdbcTemplate.queryForList(sql, Long.class, limit);
    }

    @Override
    public void upsertCmcExchangeMap(List<CmcExchangeDto> exchangeMapList) {
        String sql = """
            INSERT INTO cmc_exchange (cmc_exchange_id, name, slug, is_active, is_listed, 
                                     description, logo, date_launched, registed_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (cmc_exchange_id) 
            DO UPDATE SET
                name = EXCLUDED.name,
                slug = EXCLUDED.slug,
                is_active = EXCLUDED.is_active,
                is_listed = EXCLUDED.is_listed,
                description = EXCLUDED.description,
                logo = EXCLUDED.logo,
                date_launched = EXCLUDED.date_launched,
                updated_at = EXCLUDED.updated_at
            """;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        
        List<Object[]> batchArgs = exchangeMapList.stream()
            .map(exchange -> new Object[]{
                exchange.getId(),
                exchange.getName(),
                exchange.getSlug(),
                exchange.isActive(), // getIsActive() -> isActive()
                exchange.isListed(), // getIsListed() -> isListed()
                "", // description은 ExchangeDetail에서 업데이트
                "", // logo는 ExchangeDetail에서 업데이트
                now, // date_launched는 ExchangeDetail에서 업데이트
                now,
                now
            })
            .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
        log.info("거래소 맵 데이터 {} 건 업서트 완료", exchangeMapList.size());
    }

    @Override
    public void upsertCmcExchangeInfo(List<CmcExchangeDetailDto> exchangeInfoList) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        
        // CmcExchangeInfo 테이블에 상세 정보 저장 (fiats 필드 사용)
        String exchangeInfoSql = """
            INSERT INTO cmc_exchange_info (fiats, registed_at, updated_at)
            VALUES (?, ?, ?)
            RETURNING id
            """;
            
        // CmcExchangeMeta 테이블에 메타 정보 저장
        String exchangeMetaSql = """
            INSERT INTO cmc_exchange_meta (market_fee, taker_fee, spot_volume_usd, spot_volume_last_updated, weekly_visits, registed_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;
            
        // CmcExchangeUrl 테이블에 URL 정보 저장 (필요시)
        String exchangeUrlSql = """
            INSERT INTO cmc_exchange_url (website, twitter, register, registed_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;

        for (CmcExchangeDetailDto info : exchangeInfoList) {
            if (info.getId() != null) {
                try {
                    Long exchangeInfoId = null;
                    Long exchangeMetaId = null;
                    Long exchangeUrlId = null;
                    
                    // ExchangeInfo 저장
                    String fiatsData = (info.getFiats() != null) ? info.getFiats().toString() : "";
                    exchangeInfoId = jdbcTemplate.queryForObject(exchangeInfoSql, Long.class,
                        fiatsData, now, now);
                    
                    // ExchangeMeta 저장
                    exchangeMetaId = jdbcTemplate.queryForObject(exchangeMetaSql, Long.class,
                        info.getMarketFee() != null ? info.getMarketFee() : BigDecimal.ZERO,
                        info.getTakerFee() != null ? info.getTakerFee() : BigDecimal.ZERO,
                        info.getSpotVolumeUsd() != null ? info.getSpotVolumeUsd() : BigDecimal.ZERO,
                        now, // spot_volume_last_updated
                        info.getWeeklyVisited() != null ? info.getWeeklyVisited() : 0L,
                        now,
                        now);
                    
                    // ExchangeUrl 저장 (웹사이트 정보가 있을 경우)
                    if (info.getUrls() != null && info.getUrls().getWebsite() != null && !info.getUrls().getWebsite().isEmpty()) {
                        // URLs 객체에서 웹사이트 정보 추출
                        String website = info.getUrls().getWebsite().get(0); // 첫 번째 웹사이트 URL 사용
                        if (website != null && !website.trim().isEmpty()) {
                            exchangeUrlId = jdbcTemplate.queryForObject(exchangeUrlSql, Long.class,
                                website,
                                "", // twitter
                                "", // register
                                now, now);
                        }
                    }
                    
                    // CmcExchange 테이블에 외래키들과 기본 정보 업데이트
                    String updateExchangeSql = """
                        UPDATE cmc_exchange 
                        SET cmc_exchange_info_id = ?,
                            cmc_exchange_meta_id = ?,
                            cmc_exchange_url_id = ?,
                            description = ?, 
                            logo = ?,
                            date_launched = ?,
                            updated_at = ?
                        WHERE cmc_exchange_id = ?
                        """;
                    
                    jdbcTemplate.update(updateExchangeSql, 
                        exchangeInfoId,
                        exchangeMetaId,
                        exchangeUrlId,
                        info.getDescription() != null ? info.getDescription() : "",
                        info.getLogo() != null ? info.getLogo() : "",
                        info.getDateLaunched() != null ? LocalDateTime.parse(info.getDateLaunched(), formatter) : now,
                        now,
                        info.getId());
                        
                } catch (Exception e) {
                    log.error("거래소 상세 정보 처리 중 오류 발생 - exchange_id: {}", info.getId(), e);
                }
            }
        }

        log.info("거래소 상세 정보 {} 건 업서트 완료", exchangeInfoList.size());
    }

    @Override
    public List<Integer> getCmcExchangeIds(int limit) {
        String sql = "SELECT cmc_exchange_id FROM cmc_exchange ORDER BY cmc_exchange_id LIMIT ?";
        return jdbcTemplate.queryForList(sql, Integer.class, limit);
    }

    @Override
    public boolean existsCmcCoin(Long cmcCoinId) {
        String sql = "SELECT COUNT(*) FROM cmc_coin WHERE cmc_coin_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, cmcCoinId);
        return count != null && count > 0;
    }

    @Override
    public boolean existsCmcExchange(Integer cmcExchangeId) {
        String sql = "SELECT COUNT(*) FROM cmc_exchange WHERE cmc_exchange_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, cmcExchangeId);
        return count != null && count > 0;
    }
    
    @Override
    public List<Long> getAllCmcCoinIds() {
        String sql = "SELECT cmc_coin_id FROM cmc_coin ORDER BY cmc_coin_id";
        return jdbcTemplate.queryForList(sql, Long.class);
    }
    
    @Override
    public void upsertCmcCoinInfoBulk(List<CmcCoinInfoDataDto> coinInfoList) {
        LocalDateTime now = LocalDateTime.now();
        
        // CmcCoinInfo 테이블에 상세 정보 저장
        String coinInfoSql = """
            INSERT INTO cmc_coin_info (description, infinite_supply, is_fiat, last_updated, registed_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

        for (CmcCoinInfoDataDto info : coinInfoList) {
            if (info.getId() != null) {
                try {
                    // CmcCoinInfo 테이블에 상세 정보 저장
                    Long coinInfoId = jdbcTemplate.queryForObject(coinInfoSql, Long.class,
                        info.getDescription() != null ? info.getDescription() : "",
                        info.getInfiniteSupply() != null ? info.getInfiniteSupply() : false,
                        0, // is_fiat - 기본값 0 (암호화폐)
                        now, // last_updated
                        now,
                        now
                    );
                    
                    // CmcCoin 테이블에 외래키와 logo 정보 업데이트
                    String updateCoinSql = """
                        UPDATE cmc_coin 
                        SET cmc_coin_info_id = ?,
                            logo = ?,
                            updated_at = ?
                        WHERE cmc_coin_id = ?
                        """;
                    
                    jdbcTemplate.update(updateCoinSql, 
                        coinInfoId,
                        info.getLogo() != null ? info.getLogo() : "",
                        now,
                        info.getId());
                        
                } catch (Exception e) {
                    log.error("코인 상세 정보 일괄 처리 중 오류 발생 - coin_id: {}", info.getId(), e);
                }
            }
        }

        log.info("코인 상세 정보 일괄 처리 {} 건 완료", coinInfoList.size());
    }
    
    @Override
    public void upsertCmcCoinMeta(List<CmcApiDataDto> latestInfoList) {
        LocalDateTime now = LocalDateTime.now();
        
        // 유효한 데이터만 필터링 (USD quote 데이터가 있는 것만)
        List<CmcApiDataDto> validItems = latestInfoList.stream()
            .filter(coin -> coin.getId() != null && coin.getQuote() != null && coin.getQuote().getUSD() != null)
            .toList();

        if (validItems.isEmpty()) {
            log.warn("업데이트할 유효한 코인 메타 데이터가 없습니다.");
            return;
        }

        // CmcCoinMeta 테이블에 메타 정보 저장
        String coinMetaSql = """
            INSERT INTO cmc_coin_meta (market_cap, market_cap_dominance, fully_diluted_market_cap,
                                      circulating_supply, total_supply, max_supply, 
                                      self_reported_circulating_supply, self_reported_market_cap,
                                      registed_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

        for (CmcApiDataDto coin : validItems) {
            try {
                // USD quote 데이터 추출
                var usdQuote = coin.getQuote().getUSD();
                
                // CmcCoinMeta 테이블에 메타 정보 저장
                Long coinMetaId = jdbcTemplate.queryForObject(coinMetaSql, Long.class,
                    String.valueOf(usdQuote.getMarketCap()),
                    usdQuote.getMarketCapDominance(),
                    String.valueOf(usdQuote.getFullyDilutedMarketCap()),
                    coin.getCirculatingSupply() != null ? coin.getCirculatingSupply() : "0",
                    coin.getTotalSupply() != null ? coin.getTotalSupply() : "0",
                    coin.getMaxSupply() != null ? coin.getMaxSupply() : "0",
                    coin.getSelfReportedCirculatingSupply() != null ? coin.getSelfReportedCirculatingSupply() : "0",
                    coin.getSelfReportedMarketCap() != null ? coin.getSelfReportedMarketCap() : "0",
                    now,
                    now
                );
                
                // CmcCoinInfo 테이블에 CmcCoinMeta FK 업데이트
                String updateCoinInfoSql = """
                    UPDATE cmc_coin_info 
                    SET cmc_coin_meta_id = ?,
                        updated_at = ?
                    WHERE id = (
                        SELECT cmc_coin_info_id FROM cmc_coin WHERE cmc_coin_id = ?
                    )
                    """;
                
                jdbcTemplate.update(updateCoinInfoSql, 
                    coinMetaId,
                    now,
                    coin.getId());
                    
            } catch (Exception e) {
                log.error("코인 메타 데이터 처리 중 오류 발생 - coin_id: {}", coin.getId(), e);
            }
        }

        log.info("코인 메타 데이터 {} 건 업서트 완료", validItems.size());
    }
    
    @Override
    public void upsertCmcMainnet(List<CmcCoinInfoDataDto> coinInfoList) {
        LocalDateTime now = LocalDateTime.now();
        
        // CmcMainnet 테이블에 explorer URL 정보 저장 (UPSERT 방식으로 중복 방지)
        String mainnetSql = """
            INSERT INTO cmc_mainnet (explorer_url, cmc_coin_id, registed_at, updated_at)
            VALUES (?, (SELECT cmc_coin_id FROM cmc_coin WHERE cmc_coin_id = ?), ?, ?)
            ON CONFLICT (cmc_coin_id) 
            DO UPDATE SET
                explorer_url = EXCLUDED.explorer_url,
                updated_at = EXCLUDED.updated_at
            """;

        int processedCount = 0;
        for (CmcCoinInfoDataDto info : coinInfoList) {
            if (info.getId() != null && info.getUrls() != null && info.getUrls().getExplorer() != null && !info.getUrls().getExplorer().isEmpty()) {
                try {
                    // 여러 explorer URL이 있는 경우 첫 번째 URL만 저장 (OneToOne 관계)
                    String firstExplorerUrl = info.getUrls().getExplorer().get(0);
                    if (firstExplorerUrl != null && !firstExplorerUrl.trim().isEmpty()) {
                        jdbcTemplate.update(mainnetSql,
                            firstExplorerUrl.trim(),
                            info.getId(),
                            now,
                            now);
                        processedCount++;
                    }
                } catch (Exception e) {
                    log.error("CmcMainnet 처리 중 오류 발생 - coin_id: {}", info.getId(), e);
                }
            }
        }

        log.info("CmcMainnet 데이터 {} 건 업서트 완료", processedCount);
    }
    
    @Override
    public void upsertCmcPlatform(List<CmcCoinInfoDataDto> coinInfoList) {
        LocalDateTime now = LocalDateTime.now();
        
        // CmcPlatform 테이블에 플랫폼 정보 저장 (중복 체크 후 INSERT)
        String checkExistsSql = """
            SELECT COUNT(*) FROM cmc_platform 
            WHERE cmc_coin_id = (SELECT cmc_coin_id FROM cmc_coin WHERE cmc_coin_id = ?)
            """;
        
        String platformSql = """
            INSERT INTO cmc_platform (name, symbol, cmc_coin_id, registed_at, updated_at)
            VALUES (?, ?, (SELECT cmc_coin_id FROM cmc_coin WHERE cmc_coin_id = ?), ?, ?)
            """;

        int insertedCount = 0;
        for (CmcCoinInfoDataDto info : coinInfoList) {
            if (info.getId() != null && info.getPlatform() != null) {
                try {
                    // Platform 정보가 있는 경우에만 저장
                    CmcDataPlatformDto platform = info.getPlatform();
                    if (platform.getName() != null && !platform.getName().trim().isEmpty()) {
                        
                        // 기존 데이터 존재 여부 확인
                        Integer existingCount = jdbcTemplate.queryForObject(checkExistsSql, Integer.class, info.getId());
                        
                        if (existingCount == null || existingCount == 0) {
                            jdbcTemplate.update(platformSql,
                                platform.getName().trim(),
                                platform.getSymbol() != null ? platform.getSymbol().trim() : "",
                                info.getId(), // CmcCoinInfoDataDto의 id가 cmc_coin_id와 매핑됨
                                now,
                                now);
                            insertedCount++;
                        } else {
                            log.debug("CmcPlatform 이미 존재함 - coin_id: {}", info.getId());
                        }
                    }
                } catch (Exception e) {
                    log.error("CmcPlatform 처리 중 오류 발생 - coin_id: {}", info.getId(), e);
                }
            }
        }

        log.info("CmcPlatform 데이터 {} 건 업서트 완료", insertedCount);
    }
    
    @Override
    public void linkCmcCoinWithExistingCoin() {
        LocalDateTime now = LocalDateTime.now();
        
        // 1:1 매핑을 위해 각 coin에 대해 개별적으로 처리
        // 먼저 매핑 가능한 coin들을 조회
        String selectSql = """
            SELECT DISTINCT c.id as coin_id, c.symbol
            FROM coin c
            WHERE EXISTS (
                SELECT 1 FROM cmc_coin cc 
                WHERE UPPER(cc.symbol) = UPPER(c.symbol) 
                AND cc.coin_id IS NULL
            )
            AND NOT EXISTS (
                SELECT 1 FROM cmc_coin cc2 
                WHERE cc2.coin_id = c.id
            )
            """;
        
        try {
            List<Map<String, Object>> mappableCoinIds = jdbcTemplate.queryForList(selectSql);
            
            if (mappableCoinIds.isEmpty()) {
                log.debug("매핑할 CMC Coin과 기존 Coin이 없습니다.");
                return;
            }
            
            int linkedCount = 0;
            
            // 각 기존 coin에 대해 하나의 CMC coin만 매핑
            for (Map<String, Object> coinInfo : mappableCoinIds) {
                Long coinId = ((Number) coinInfo.get("coin_id")).longValue();
                String symbol = (String) coinInfo.get("symbol");
                
                // 해당 symbol을 가진 CMC coin 중에서 첫 번째 하나만 매핑 (rank 우선순위)
                String updateSql = """
                    UPDATE cmc_coin 
                    SET coin_id = ?, 
                        updated_at = ?
                    WHERE id = (
                        SELECT cc.id
                        FROM cmc_coin cc
                        LEFT JOIN cmc_rank cr ON cc.cmc_coin_id = cr.cmc_coin_id
                        WHERE UPPER(cc.symbol) = UPPER(?)
                        AND cc.coin_id IS NULL
                        ORDER BY COALESCE(cr.rank, 999999), cc.cmc_coin_id
                        LIMIT 1
                    )
                    AND coin_id IS NULL
                    """;
                
                int updated = jdbcTemplate.update(updateSql, coinId, now, symbol);
                if (updated > 0) {
                    linkedCount++;
                    log.debug("CMC Coin 매핑: symbol={}, coin_id={}", symbol, coinId);
                }
            }
            
            if (linkedCount > 0) {
                log.info("CMC Coin과 기존 Coin 매핑 완료: {} 건", linkedCount);
            } else {
                log.debug("매핑할 CMC Coin과 기존 Coin이 없습니다.");
            }
            
        } catch (Exception e) {
            log.error("CMC Coin 매핑 중 오류 발생", e);
            throw e;
        }
    }
}
