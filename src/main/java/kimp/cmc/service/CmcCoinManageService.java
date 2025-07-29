package kimp.cmc.service;

import kimp.cmc.dto.response.CmcCoinResponseDto;

public interface CmcCoinManageService {

    public CmcCoinResponseDto findCmcCoinDataByCoinId(Long coinId);
}
