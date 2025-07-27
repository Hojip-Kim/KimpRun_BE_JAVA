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
import kimp.cmc.entity.coin.CmcCoin;
import kimp.cmc.service.CmcCoinManageService;
import kimp.market.service.CoinService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CmcCoinManageServiceImpl implements CmcCoinManageService {

    private final CoinMarketCapComponent coinMarketCapComponent;
    private final CoinService coinService;

    private final CmcCoinDaoImpl cmcDao;
    private final CmcRankDaoImpl cmcRankDao;
    private final CmcMainnetDaoImpl cmcMainnetDao;
    private final CmcPlatformDaoImpl cmcPlatformDao;
    private final CmcCoinInfoDaoImpl cmcCoinInfoDao;
    private final CmcCoinMetaDaoImpl cmcCoinMetaDao;

    public CmcCoinManageServiceImpl(CmcCoinDaoImpl cmcDao, CmcRankDaoImpl cmcRankDao, CmcMainnetDaoImpl cmcMainnetDao, CmcPlatformDaoImpl cmcPlatformDao, CmcCoinInfoDaoImpl cmcCoinInfoDao, CmcCoinMetaDaoImpl cmcCoinMetaDao, CoinMarketCapComponent coinMarketCapComponent, CoinService coinService) {
        this.cmcDao = cmcDao;
        this.cmcRankDao = cmcRankDao;
        this.cmcMainnetDao = cmcMainnetDao;
        this.cmcPlatformDao = cmcPlatformDao;
        this.cmcCoinInfoDao = cmcCoinInfoDao;
        this.cmcCoinMetaDao = cmcCoinMetaDao;
        this.coinMarketCapComponent = coinMarketCapComponent;
        this.coinService = coinService;
    }

    private List<CmcCoinMapDataDto> cmcCoinMapDataDtoList = new ArrayList<>();
    private List<CmcApiDataDto> cmcApiDataDtoList = new ArrayList<>();
    private CmcCoinInfoDataMapDto cmcCoinInfoDataMapDto;

    public List<CmcCoinMapDataDto> getCmcCoinMapDataDtoList(int start, int limit) {
        List<CmcCoinMapDataDto> cmcCoinMapDataDtos = coinMarketCapComponent.getCoinMapFromCMC(start, limit);

        return cmcCoinMapDataDtos.size() != 0 ? cmcCoinMapDataDtos : null;
    }



    @Transactional
    public void cmcCoinDataSet() {
        List<CmcCoinMapDataDto> cmcCoinMapDataDtoList = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            List<CmcCoinMapDataDto> cmcCoinMapDataDtoPartialList = coinMarketCapComponent.getCoinMapFromCMC(i*5000 + 1, 5000);
            cmcCoinMapDataDtoList.addAll(cmcCoinMapDataDtoPartialList);
        }

        List<CmcCoin> cmcCoinList = cmcCoinMapDataDtoList.stream()
                .map(cmcCoinMapDataDto -> {
                    LocalDateTime parsedFirstHistoricalData = LocalDateTime.parse(cmcCoinMapDataDto.getFirst_historical_data());
                    LocalDateTime parsedLastHistoricalData = LocalDateTime.parse(cmcCoinMapDataDto.getLast_historical_data());
                    CmcCoin cmcCoin = new CmcCoin(cmcCoinMapDataDto.getId(),null , cmcCoinMapDataDto.getName(), cmcCoinMapDataDto.getSymbol(), cmcCoinMapDataDto.getSlug(), cmcCoinMapDataDto.getIsActive(), cmcCoinMapDataDto.getStatus(), false, parsedFirstHistoricalData, parsedLastHistoricalData);
                    return cmcCoin;
                })
                .toList();
        this.cmcDao.saveAllCmcCoin(cmcCoinList);
    }

}
