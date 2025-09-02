package kimp.cmc.service;

import kimp.cmc.dto.response.CmcCoinInfoResponseDto;
import kimp.cmc.dto.response.CmcCoinResponseDto;
import kimp.common.dto.PageRequestDto;
import org.springframework.data.domain.Page;

public interface CmcCoinManageService {

    public CmcCoinResponseDto findCmcCoinDataByCoinId(Long coinId);
    
    /**
     * Rank 순으로 정렬된 Coin 목록을 페이지로 조회
     */
    public Page<CmcCoinResponseDto> findAllCoinsOrderByRank(PageRequestDto pageRequestDto);
    
    /**
     * CmcCoinInfoResponseDto DTO 직접 조회 (완전한 N+1 방지)
     * Rank 순으로 정렬된 CoinInfo 목록을 페이지로 조회
     */
    public Page<CmcCoinInfoResponseDto> findAllCoinInfoDtosOrderByRank(PageRequestDto pageRequestDto);
}
