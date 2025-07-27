package kimp.cmc.dao.jdbc;

import kimp.cmc.dto.common.coin.CmcApiDataDto;
import kimp.cmc.dto.common.coin.CmcCoinInfoDataDto;
import kimp.cmc.dto.common.coin.CmcCoinMapDataDto;
import kimp.cmc.dto.common.exchange.CmcExchangeDto;
import kimp.cmc.dto.common.exchange.CmcExchangeDetailDto;

import java.util.List;

public interface CmcBatchDao {
    
    // 코인 관련 배치 메서드들
    void upsertCmcCoinMap(List<CmcCoinMapDataDto> coinMapList);
    void updateCmcCoinLatestInfo(List<CmcApiDataDto> latestInfoList);
    void upsertCmcCoinInfo(List<CmcCoinInfoDataDto> coinInfoList);
    List<Long> getCmcCoinIds(int limit);
    
    // 거래소 관련 배치 메서드들
    void upsertCmcExchangeMap(List<CmcExchangeDto> exchangeMapList);
    void upsertCmcExchangeInfo(List<CmcExchangeDetailDto> exchangeInfoList);
    List<Integer> getCmcExchangeIds(int limit);
    
    // 유틸리티 메서드들
    boolean existsCmcCoin(Long cmcCoinId);
    boolean existsCmcExchange(Integer cmcExchangeId);
    
    // CMC Coin과 기존 Coin 테이블 매핑
    void linkCmcCoinWithExistingCoin();
    
    // 모든 CMC Coin ID 조회 (CmcCoinInfo 배치 처리용)
    List<Long> getAllCmcCoinIds();
    
    // CmcCoinInfo 일괄 처리 (getCmcCoinInfos API 활용)
    void upsertCmcCoinInfoBulk(List<CmcCoinInfoDataDto> coinInfoList);
    
    // CmcCoinMeta 데이터 처리 (getLatestCoinInfoFromCMC API 활용)
    void upsertCmcCoinMeta(List<CmcApiDataDto> latestInfoList);
    
    // CmcMainnet 데이터 처리 (getCmcCoinInfos에서 explorer URLs 활용)
    void upsertCmcMainnet(List<CmcCoinInfoDataDto> coinInfoList);
    
    // CmcPlatform 데이터 처리 (getCmcCoinInfos에서 platform 정보 활용)
    void upsertCmcPlatform(List<CmcCoinInfoDataDto> coinInfoList);
}
