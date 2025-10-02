package kimp.cmc.service;

import kimp.cmc.dto.response.CmcCoinInfoResponseDto;
import kimp.cmc.dto.response.CmcCoinResponseDto;
import kimp.cmc.vo.GetAllCoinInfoPageDataVo;
import kimp.cmc.vo.GetCoinDataByCoinIdVo;
import kimp.cmc.vo.GetCoinsBySymbolContainingVo;
import org.springframework.data.domain.Page;

public interface CmcCoinManageService {

    public CmcCoinResponseDto findCmcCoinDataByCoinId(GetCoinDataByCoinIdVo vo);

    /**
     * CmcCoinInfoResponseDto DTO 직접 조회 (완전한 N+1 방지)
     * Rank 순으로 정렬된 CoinInfo 목록을 페이지로 조회
     */
    public Page<CmcCoinInfoResponseDto> findAllCoinInfoDtosOrderByRank(GetAllCoinInfoPageDataVo vo);

    /**
     * Symbol을 포함하는 CmcCoin 검색 (대소문자 구분 없음)
     * QueryDSL을 사용하여 N+1 문제를 방지하고 fetch join으로 필요한 데이터를 한 번에 조회
     */
    public Page<CmcCoinInfoResponseDto> findCoinsBySymbolContaining(GetCoinsBySymbolContainingVo vo);
}
