package kimp.cmc.service;

import kimp.cmc.dto.response.CmcExchangeInfoResponseDto;
import kimp.common.dto.PageRequestDto;
import org.springframework.data.domain.Page;

public interface CmcExchangeManageService {
    
    /**
     * SpotVolume 순으로 정렬된 Exchange 목록을 페이지로 조회
     */
    Page<CmcExchangeInfoResponseDto> findAllExchangesOrderBySpotVolume(PageRequestDto pageRequestDto);
}
