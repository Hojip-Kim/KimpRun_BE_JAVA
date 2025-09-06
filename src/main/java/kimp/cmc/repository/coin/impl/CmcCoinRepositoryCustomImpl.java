package kimp.cmc.repository.coin.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kimp.cmc.dto.response.CmcCoinInfoResponseDto;
import kimp.cmc.entity.coin.CmcCoin;
import kimp.cmc.entity.coin.CmcMainnet;
import kimp.cmc.entity.coin.CmcPlatform;
import kimp.cmc.entity.coin.QCmcCoin;
import kimp.cmc.entity.coin.QCmcCoinInfo;
import kimp.cmc.entity.coin.QCmcCoinMeta;
import kimp.cmc.entity.coin.QCmcMainnet;
import kimp.cmc.entity.coin.QCmcPlatform;
import kimp.cmc.entity.coin.QCmcRank;
import kimp.market.entity.QCoin;
import kimp.cmc.repository.coin.CmcCoinRepositoryCustom;
import kimp.common.dto.PageRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CmcCoinRepositoryCustomImpl implements CmcCoinRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CmcCoinRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // QueryDSL Q클래스 인스턴스
    QCmcCoin cmcCoin = QCmcCoin.cmcCoin;
    QCmcRank cmcRank = QCmcRank.cmcRank;
    QCmcCoinInfo cmcCoinInfo = QCmcCoinInfo.cmcCoinInfo;
    QCmcCoinMeta cmcCoinMeta = QCmcCoinMeta.cmcCoinMeta;
    QCmcMainnet cmcMainnet = QCmcMainnet.cmcMainnet;
    QCmcPlatform cmcPlatform = QCmcPlatform.cmcPlatform;
    QCoin coin = QCoin.coin;

    @Override
    @Transactional(readOnly = true)
    public Optional<CmcCoin> findByIdWithOneToOneRelations(Long coinId) {
        // CmcCoin과 OneToOne 관계의 모든 엔티티를 fetch join으로 가져옴 (1번째 쿼리)
        // coinId는 Coin 테이블의 ID이므로 cmcCoin.coin.id로 조회
        CmcCoin result = queryFactory
                .selectFrom(cmcCoin)
                .leftJoin(cmcCoin.coin, coin).fetchJoin()
                .leftJoin(cmcCoin.cmcRank, cmcRank).fetchJoin()
                .leftJoin(cmcCoin.cmcCoinInfo, cmcCoinInfo).fetchJoin()
                .leftJoin(cmcCoinInfo.cmcCoinMeta, cmcCoinMeta).fetchJoin()
                .where(cmcCoin.coin.id.eq(coinId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CmcMainnet> findMainnetsByCmcCoinId(Long cmcCoinId) {
        // cmcCoinId 기준으로 CmcMainnet 목록 조회
        return queryFactory
                .selectFrom(cmcMainnet)
                .where(cmcMainnet.cmcCoin.cmcCoinId.eq(cmcCoinId))
                .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CmcPlatform> findPlatformsByCmcCoinId(Long cmcCoinId) {
        // cmcCoinId 기준으로 CmcPlatform 목록 조회
        return queryFactory
                .selectFrom(cmcPlatform)
                .where(cmcPlatform.cmcCoin.cmcCoinId.eq(cmcCoinId))
                .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CmcCoin> findAllOrderByRankWithFetchJoin(PageRequestDto pageRequestDto) {
        // 페이지 정보 설정 (0-based 인덱스)
        int page = (pageRequestDto.getPage() != null) ? Math.max(0, pageRequestDto.getPage() - 1) : 0;
        int size = (pageRequestDto.getSize() != null) ? pageRequestDto.getSize() : 15;
        Pageable pageable = PageRequest.of(page, size);

        // Rank 순으로 정렬하여 CmcCoin 목록 조회 (N+1 방지를 위한 fetch join)
        List<CmcCoin> results = queryFactory
                .selectFrom(cmcCoin)
                .leftJoin(cmcCoin.cmcRank, cmcRank).fetchJoin()
                .leftJoin(cmcCoin.cmcCoinInfo, cmcCoinInfo).fetchJoin()
                .leftJoin(cmcCoinInfo.cmcCoinMeta, cmcCoinMeta).fetchJoin()
                .where(cmcCoin.cmcRank.isNotNull()) // rank가 있는 코인만 조회
                .orderBy(cmcRank.rank.asc()) // rank 오름차순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회 (카운트 쿼리)
        Long totalCount = queryFactory
                .select(cmcCoin.count())
                .from(cmcCoin)
                .where(cmcCoin.cmcRank.isNotNull())
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount != null ? totalCount : 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CmcMainnet> findMainnetsByCmcCoinIds(List<Long> cmcCoinIds) {
        return queryFactory
                .selectFrom(cmcMainnet)
                .leftJoin(cmcMainnet.cmcCoin, cmcCoin).fetchJoin() // CmcCoin fetch join으로 lazy loading 방지
                .where(cmcMainnet.cmcCoin.cmcCoinId.in(cmcCoinIds))
                .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CmcPlatform> findPlatformsByCmcCoinIds(List<Long> cmcCoinIds) {
        return queryFactory
                .selectFrom(cmcPlatform)
                .leftJoin(cmcPlatform.cmcCoin, cmcCoin).fetchJoin() // CmcCoin fetch join으로 lazy loading 방지
                .where(cmcPlatform.cmcCoin.cmcCoinId.in(cmcCoinIds))
                .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CmcCoinInfoResponseDto> findAllCoinInfoDtosOrderByRank(PageRequestDto pageRequestDto) {
        // 페이지 정보 설정 (0-based 인덱스)
        int page = (pageRequestDto.getPage() != null) ? Math.max(0, pageRequestDto.getPage() - 1) : 0;
        int size = (pageRequestDto.getSize() != null) ? pageRequestDto.getSize() : 15;
        Pageable pageable = PageRequest.of(page, size);

        // 1. CmcCoinInfoResponseDto 기본 필드와 cmcCoinId를 Tuple로 조회 (DTO와 cmcCoinId를 함께 조회)
        List<com.querydsl.core.Tuple> tupleResults = queryFactory
                .select(
                        cmcCoin.cmcCoinId,
                        cmcCoin.symbol,
                        cmcCoin.name,
                        cmcCoin.slug,
                        cmcCoin.logo,
                        cmcRank.rank.stringValue(),
                        cmcCoinInfo.description,
                        cmcCoinMeta.marketCapDominance,
                        cmcCoinMeta.maxSupply,
                        cmcCoinMeta.totalSupply,
                        cmcCoinMeta.circulatingSupply,
                        cmcCoinMeta.marketCap,
                        cmcCoinMeta.fullyDilutedMarketCap,
                        cmcCoinMeta.selfReportedCirculatingSupply,
                        cmcCoinMeta.selfReportedMarketCap,
                        cmcCoinInfo.lastUpdated,
                        cmcCoin.firstHistoricalData
                )
                .from(cmcCoin)
                .leftJoin(cmcCoin.cmcRank, cmcRank)
                .leftJoin(cmcCoin.cmcCoinInfo, cmcCoinInfo)
                .leftJoin(cmcCoinInfo.cmcCoinMeta, cmcCoinMeta)
                .where(cmcCoin.cmcRank.isNotNull()) // rank가 있는 코인만 조회
                .orderBy(cmcRank.rank.asc()) // rank 오름차순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2. Tuple에서 DTO 생성과 cmcCoinId 추출을 동시에 수행
        List<CmcCoinInfoResponseDto> results = new ArrayList<>();
        List<Long> cmcCoinIds = new ArrayList<>();
        
        for (com.querydsl.core.Tuple tuple : tupleResults) {
            CmcCoinInfoResponseDto dto = new CmcCoinInfoResponseDto(
                    tuple.get(cmcCoin.symbol),
                    tuple.get(cmcCoin.name),
                    tuple.get(cmcCoin.slug),
                    tuple.get(cmcCoin.logo),
                    tuple.get(cmcRank.rank.stringValue()),
                    tuple.get(cmcCoinInfo.description),
                    tuple.get(cmcCoinMeta.marketCapDominance),
                    tuple.get(cmcCoinMeta.maxSupply),
                    tuple.get(cmcCoinMeta.totalSupply),
                    tuple.get(cmcCoinMeta.circulatingSupply),
                    tuple.get(cmcCoinMeta.marketCap),
                    tuple.get(cmcCoinMeta.fullyDilutedMarketCap),
                    tuple.get(cmcCoinMeta.selfReportedCirculatingSupply),
                    tuple.get(cmcCoinMeta.selfReportedMarketCap),
                    tuple.get(cmcCoinInfo.lastUpdated),
                    tuple.get(cmcCoin.firstHistoricalData)
            );
            results.add(dto);
            cmcCoinIds.add(tuple.get(cmcCoin.cmcCoinId));
        }

        // 3. Platform 정보를 batch 조회하여 그룹핑
        Map<Long, List<String>> platformByCoinId = cmcCoinIds.isEmpty() ? 
                new java.util.HashMap<>() :
                queryFactory
                    .select(cmcPlatform.cmcCoin.cmcCoinId, 
                           cmcPlatform.name.concat(" (").concat(cmcPlatform.symbol).concat(")"))
                    .from(cmcPlatform)
                    .where(cmcPlatform.cmcCoin.cmcCoinId.in(cmcCoinIds))
                    .fetch()
                    .stream()
                    .collect(Collectors.groupingBy(
                        tuple -> tuple.get(cmcPlatform.cmcCoin.cmcCoinId),
                        Collectors.mapping(
                            tuple -> tuple.get(cmcPlatform.name.concat(" (").concat(cmcPlatform.symbol).concat(")")), 
                            Collectors.toList())
                    ));

        // 4. Mainnet(ExplorerUrl) 정보를 batch 조회하여 그룹핑
        Map<Long, List<String>> explorerUrlByCoinId = cmcCoinIds.isEmpty() ? 
                new java.util.HashMap<>() :
                queryFactory
                    .select(cmcMainnet.cmcCoin.cmcCoinId, cmcMainnet.explorerUrl)
                    .from(cmcMainnet)
                    .where(cmcMainnet.cmcCoin.cmcCoinId.in(cmcCoinIds))
                    .fetch()
                    .stream()
                    .collect(Collectors.groupingBy(
                        tuple -> tuple.get(cmcMainnet.cmcCoin.cmcCoinId),
                        Collectors.mapping(
                            tuple -> tuple.get(cmcMainnet.explorerUrl), 
                            Collectors.toList())
                    ));

        // 5. 각 DTO에 platform과 explorerUrl 설정
        for (int i = 0; i < results.size() && i < cmcCoinIds.size(); i++) {
            CmcCoinInfoResponseDto dto = results.get(i);
            Long coinId = cmcCoinIds.get(i);
            
            dto.setPlatform(platformByCoinId.getOrDefault(coinId, List.of()));
            dto.setExplorerUrl(explorerUrlByCoinId.getOrDefault(coinId, List.of()));
        }

        // 전체 개수 조회 (카운트 쿼리)
        Long totalCount = queryFactory
                .select(cmcCoin.count())
                .from(cmcCoin)
                .where(cmcCoin.cmcRank.isNotNull())
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount != null ? totalCount : 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CmcCoinInfoResponseDto> findCoinInfoDtosBySymbolContaining(String symbol, PageRequestDto pageRequestDto) {
        // 페이지 정보 설정 (0-based 인덱스)
        int page = (pageRequestDto.getPage() != null) ? Math.max(0, pageRequestDto.getPage() - 1) : 0;
        int size = (pageRequestDto.getSize() != null) ? pageRequestDto.getSize() : 15;
        Pageable pageable = PageRequest.of(page, size);

        // 1. Symbol을 포함하는 CmcCoin과 연관 데이터를 Tuple로 조회 (DTO와 cmcCoinId를 함께 조회)
        List<com.querydsl.core.Tuple> tupleResults = queryFactory
                .select(
                        cmcCoin.cmcCoinId,
                        cmcCoin.symbol,
                        cmcCoin.name,
                        cmcCoin.slug,
                        cmcCoin.logo,
                        cmcRank.rank.stringValue(),
                        cmcCoinInfo.description,
                        cmcCoinMeta.marketCapDominance,
                        cmcCoinMeta.maxSupply,
                        cmcCoinMeta.totalSupply,
                        cmcCoinMeta.circulatingSupply,
                        cmcCoinMeta.marketCap,
                        cmcCoinMeta.fullyDilutedMarketCap,
                        cmcCoinMeta.selfReportedCirculatingSupply,
                        cmcCoinMeta.selfReportedMarketCap,
                        cmcCoinInfo.lastUpdated,
                        cmcCoin.firstHistoricalData
                )
                .from(cmcCoin)
                .leftJoin(cmcCoin.cmcRank, cmcRank)
                .leftJoin(cmcCoin.cmcCoinInfo, cmcCoinInfo)
                .leftJoin(cmcCoinInfo.cmcCoinMeta, cmcCoinMeta)
                .where(cmcCoin.symbol.containsIgnoreCase(symbol)) // Symbol을 포함하는 검색 (대소문자 구분 없음)
                .orderBy(cmcRank.rank.asc().nullsLast()) // rank 오름차순 정렬, null은 마지막에
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2. Tuple에서 DTO 생성과 cmcCoinId 추출을 동시에 수행
        List<CmcCoinInfoResponseDto> results = new ArrayList<>();
        List<Long> cmcCoinIds = new ArrayList<>();
        
        for (com.querydsl.core.Tuple tuple : tupleResults) {
            CmcCoinInfoResponseDto dto = new CmcCoinInfoResponseDto(
                    tuple.get(cmcCoin.symbol),
                    tuple.get(cmcCoin.name),
                    tuple.get(cmcCoin.slug),
                    tuple.get(cmcCoin.logo),
                    tuple.get(cmcRank.rank.stringValue()),
                    tuple.get(cmcCoinInfo.description),
                    tuple.get(cmcCoinMeta.marketCapDominance),
                    tuple.get(cmcCoinMeta.maxSupply),
                    tuple.get(cmcCoinMeta.totalSupply),
                    tuple.get(cmcCoinMeta.circulatingSupply),
                    tuple.get(cmcCoinMeta.marketCap),
                    tuple.get(cmcCoinMeta.fullyDilutedMarketCap),
                    tuple.get(cmcCoinMeta.selfReportedCirculatingSupply),
                    tuple.get(cmcCoinMeta.selfReportedMarketCap),
                    tuple.get(cmcCoinInfo.lastUpdated),
                    tuple.get(cmcCoin.firstHistoricalData)
            );
            results.add(dto);
            cmcCoinIds.add(tuple.get(cmcCoin.cmcCoinId));
        }

        // 3. Platform 정보를 batch 조회하여 그룹핑
        Map<Long, List<String>> platformByCoinId = cmcCoinIds.isEmpty() ? 
                new java.util.HashMap<>() :
                queryFactory
                    .select(cmcPlatform.cmcCoin.cmcCoinId, 
                           cmcPlatform.name.concat(" (").concat(cmcPlatform.symbol).concat(")"))
                    .from(cmcPlatform)
                    .where(cmcPlatform.cmcCoin.cmcCoinId.in(cmcCoinIds))
                    .fetch()
                    .stream()
                    .collect(Collectors.groupingBy(
                        tuple -> tuple.get(cmcPlatform.cmcCoin.cmcCoinId),
                        Collectors.mapping(
                            tuple -> tuple.get(cmcPlatform.name.concat(" (").concat(cmcPlatform.symbol).concat(")")), 
                            Collectors.toList())
                    ));

        // 4. Mainnet(ExplorerUrl) 정보를 batch 조회하여 그룹핑
        Map<Long, List<String>> explorerUrlByCoinId = cmcCoinIds.isEmpty() ? 
                new java.util.HashMap<>() :
                queryFactory
                    .select(cmcMainnet.cmcCoin.cmcCoinId, cmcMainnet.explorerUrl)
                    .from(cmcMainnet)
                    .where(cmcMainnet.cmcCoin.cmcCoinId.in(cmcCoinIds))
                    .fetch()
                    .stream()
                    .collect(Collectors.groupingBy(
                        tuple -> tuple.get(cmcMainnet.cmcCoin.cmcCoinId),
                        Collectors.mapping(
                            tuple -> tuple.get(cmcMainnet.explorerUrl), 
                            Collectors.toList())
                    ));

        // 5. 각 DTO에 platform과 explorerUrl 설정
        for (int i = 0; i < results.size() && i < cmcCoinIds.size(); i++) {
            CmcCoinInfoResponseDto dto = results.get(i);
            Long coinId = cmcCoinIds.get(i);
            
            dto.setPlatform(platformByCoinId.getOrDefault(coinId, List.of()));
            dto.setExplorerUrl(explorerUrlByCoinId.getOrDefault(coinId, List.of()));
        }

        // 전체 개수 조회 (카운트 쿼리)
        Long totalCount = queryFactory
                .select(cmcCoin.count())
                .from(cmcCoin)
                .where(cmcCoin.symbol.containsIgnoreCase(symbol))
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount != null ? totalCount : 0L);
    }
}