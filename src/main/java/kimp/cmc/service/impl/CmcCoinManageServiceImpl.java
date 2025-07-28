package kimp.cmc.service.impl;

import kimp.cmc.component.CoinMarketCapComponent;
import kimp.cmc.dao.coin.impl.CmcCoinDaoImpl;
import kimp.cmc.dao.coin.impl.CmcCoinInfoDaoImpl;
import kimp.cmc.dao.coin.impl.CmcCoinMetaDaoImpl;
import kimp.cmc.dao.coin.impl.CmcMainnetDaoImpl;
import kimp.cmc.dao.coin.impl.CmcPlatformDaoImpl;
import kimp.cmc.dao.coin.impl.CmcRankDaoImpl;
import kimp.cmc.dto.common.coin.CmcApiDataDto;
import kimp.cmc.dto.common.coin.CmcCoinInfoDataMapDto;
import kimp.cmc.dto.common.coin.CmcCoinMapDataDto;
import kimp.cmc.dto.response.CmcCoinResponseDto;
import kimp.cmc.entity.coin.CmcCoin;
import kimp.cmc.entity.coin.CmcMainnet;
import kimp.cmc.entity.coin.CmcPlatform;
import kimp.cmc.service.CmcCoinManageService;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.market.service.CoinService;
import kimp.market.service.serviceImpl.MarketInfoServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CmcCoinManageServiceImpl implements CmcCoinManageService {

    private final CoinMarketCapComponent coinMarketCapComponent;
    private final CoinService coinService;
    private final MarketInfoServiceImpl marketInfoServiceImpl;

    private final CmcCoinDaoImpl cmcDao;
    private final CmcRankDaoImpl cmcRankDao;
    private final CmcMainnetDaoImpl cmcMainnetDao;
    private final CmcPlatformDaoImpl cmcPlatformDao;
    private final CmcCoinInfoDaoImpl cmcCoinInfoDao;
    private final CmcCoinMetaDaoImpl cmcCoinMetaDao;

    public CmcCoinManageServiceImpl(CmcCoinDaoImpl cmcDao, CmcRankDaoImpl cmcRankDao, CmcMainnetDaoImpl cmcMainnetDao, CmcPlatformDaoImpl cmcPlatformDao, CmcCoinInfoDaoImpl cmcCoinInfoDao, CmcCoinMetaDaoImpl cmcCoinMetaDao, CoinMarketCapComponent coinMarketCapComponent, CoinService coinService, MarketInfoServiceImpl marketInfoServiceImpl) {
        this.cmcDao = cmcDao;
        this.cmcRankDao = cmcRankDao;
        this.cmcMainnetDao = cmcMainnetDao;
        this.cmcPlatformDao = cmcPlatformDao;
        this.cmcCoinInfoDao = cmcCoinInfoDao;
        this.cmcCoinMetaDao = cmcCoinMetaDao;
        this.coinMarketCapComponent = coinMarketCapComponent;
        this.coinService = coinService;
        this.marketInfoServiceImpl = marketInfoServiceImpl;
    }

    private List<CmcCoinMapDataDto> cmcCoinMapDataDtoList = new ArrayList<>();
    private List<CmcApiDataDto> cmcApiDataDtoList = new ArrayList<>();
    private CmcCoinInfoDataMapDto cmcCoinInfoDataMapDto;

    public List<CmcCoinMapDataDto> getCmcCoinMapDataDtoList(int start, int limit) {
        List<CmcCoinMapDataDto> cmcCoinMapDataDtos = coinMarketCapComponent.getCoinMapFromCMC(start, limit);

        return cmcCoinMapDataDtos.size() != 0 ? cmcCoinMapDataDtos : null;
    }

    @Override
    @Transactional(readOnly = true)
    public CmcCoinResponseDto findCmcCoinDataByCoinId(Long coinId) {
        // 1번째 쿼리: CmcCoin과 OneToOne 관계 엔티티들을 fetch join으로 조회
        CmcCoin cmcCoin = this.cmcDao.findCmcCoinByIdWithOneToOneRelations(coinId);
        if(cmcCoin == null){
             throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "코인 Id에 해당하는 cmc Coin이 없음.", HttpStatus.BAD_REQUEST, "CmcCoinManageService.findCmcCoinDataByCoinId");
        }

        // 2번째와 3번째 쿼리: OneToMany 컬렉션들을 cmcCoinId로 조회 (총 3번의 SELECT)
        // MultipleBagFetchException을 피하기 위해 개별 조회 후 애플리케이션에서 조립
        Long cmcCoinId = cmcCoin.getCmcCoinId();
        List<CmcMainnet> mainnets = this.cmcDao.findMainnetsByCmcCoinId(cmcCoinId);
        List<CmcPlatform> platforms = this.cmcDao.findPlatformsByCmcCoinId(cmcCoinId);

        return buildCmcCoinResponseDto(cmcCoin, mainnets, platforms);
    }
    
    /**
     * CmcCoin 엔티티와 별도 조회한 컬렉션들로부터 CmcCoinResponseDto를 구성
     * 총 3번의 SELECT 쿼리로 모든 데이터를 조회하여 애플리케이션에서 조립
     * (MultipleBagFetchException을 피하면서 최소한의 쿼리로 데이터 조회)
     */
    private CmcCoinResponseDto buildCmcCoinResponseDto(CmcCoin cmcCoin, List<CmcMainnet> mainnets, List<CmcPlatform> platforms) {
        // 기본 정보
        String symbol = cmcCoin.getSymbol();
        String name = cmcCoin.getName();
        String logo = cmcCoin.getLogo();
        
        // CmcCoinMeta에서 maxSupply, totalSupply 가져오기
        String maxSupply = null;
        String totalSupply = null;
        String circulatingSupply = null;
        String marketCap = null;
        if (cmcCoin.getCmcCoinInfo() != null && cmcCoin.getCmcCoinInfo().getCmcCoinMeta() != null) {
            maxSupply = cmcCoin.getCmcCoinInfo().getCmcCoinMeta().getMaxSupply();
            totalSupply = cmcCoin.getCmcCoinInfo().getCmcCoinMeta().getTotalSupply();
            circulatingSupply = cmcCoin.getCmcCoinInfo().getCmcCoinMeta().getCirculatingSupply();
            String marketCapStr = cmcCoin.getCmcCoinInfo().getCmcCoinMeta().getMarketCap();
            double marketCapValue = Double.parseDouble(marketCapStr) * marketInfoServiceImpl.getTetherKRW();
            marketCap = String.valueOf(Math.round(marketCapValue));
        }
        
        // 별도 조회한 CmcMainnet에서 explorer URLs 가져오기
        List<String> explorerUrls = mainnets.stream()
                .map(mainnet -> mainnet.getExplorerUrl())
                .toList();
        
        // 별도 조회한 CmcPlatform에서 platform 정보 가져오기
        List<String> platformStrings = platforms.stream()
                .map(platform -> platform.getName() + " (" + platform.getSymbol() + ")")
                .toList();
        
        // CmcRank에서 순위 가져오기
        Integer rank = null;
        if (cmcCoin.getCmcRank() != null) {
            rank = cmcCoin.getCmcRank().getRank().intValue();
        }
        
        // lastUpdated는 CmcCoinInfo에서 가져오기
        LocalDateTime lastUpdated = null;
        if (cmcCoin.getCmcCoinInfo() != null) {
            lastUpdated = cmcCoin.getCmcCoinInfo().getLastUpdated();
        }
        
        return new CmcCoinResponseDto(
                symbol,
                name,
                logo,
                maxSupply,
                totalSupply,
                circulatingSupply,
                marketCap,
                explorerUrls,
                platformStrings,
                rank,
                lastUpdated
        );
    }
}
