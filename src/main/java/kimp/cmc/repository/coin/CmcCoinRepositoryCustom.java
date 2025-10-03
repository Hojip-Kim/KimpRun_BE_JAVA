package kimp.cmc.repository.coin;

import kimp.cmc.dto.response.CmcCoinInfoResponseDto;
import kimp.cmc.entity.coin.CmcCoin;
import kimp.cmc.entity.coin.CmcMainnet;
import kimp.cmc.entity.coin.CmcPlatform;
import kimp.common.dto.request.PageRequestDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CmcCoinRepositoryCustom {
    
    /**
     * CmcCoin을 ID로 조회하면서 OneToOne 관계의 연관 엔티티를 fetch join으로 가져옴
     * CmcRank, CmcCoinInfo, CmcCoinMeta를 한번에 fetch join
     * 
     */
    Optional<CmcCoin> findByIdWithOneToOneRelations(Long coinId);
    
    /**
     * cmcCoinId로 CmcMainnet 목록을 조회
     * 
     */
    List<CmcMainnet> findMainnetsByCmcCoinId(Long cmcCoinId);
    
    /**
     * cmcCoinId로 CmcPlatform 목록을 조회
     * 
     */
    List<CmcPlatform> findPlatformsByCmcCoinId(Long cmcCoinId);
    
    /**
     * 여러 cmcCoinId들로 CmcMainnet 목록을 batch 조회 (N+1 방지)
     * 
     */
    List<CmcMainnet> findMainnetsByCmcCoinIds(List<Long> cmcCoinIds);
    
    /**
     * 여러 cmcCoinId들로 CmcPlatform 목록을 batch 조회 (N+1 방지)
     * 
     */
    List<CmcPlatform> findPlatformsByCmcCoinIds(List<Long> cmcCoinIds);
    
    /**
     * Rank 순으로 정렬된 CmcCoin 페이지 조회 (N+1 방지용 fetch join)
     * CmcRank, CmcCoinInfo, CmcCoinMeta를 모두 fetch join으로 한번에 조회
     */
    Page<CmcCoin> findAllOrderByRankWithFetchJoin(PageRequestDto pageRequestDto);
    
    /**
     * CmcCoinInfoResponseDto 직접 조회 (완전한 N+1 방지)
     * Rank 순으로 정렬된 CmcCoinInfo 페이지 조회
     */
    Page<CmcCoinInfoResponseDto> findAllCoinInfoDtosOrderByRank(PageRequestDto pageRequestDto);
    
    /**
     * Symbol을 포함하는 CmcCoin 검색 (대소문자 구분 없음)
     * QueryDSL을 사용하여 N+1 문제를 방지하고 fetch join으로 필요한 데이터를 한 번에 조회
     */
    Page<CmcCoinInfoResponseDto> findCoinInfoDtosBySymbolContaining(String symbol, PageRequestDto pageRequestDto);
}