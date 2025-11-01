package kimp.cmc.service.impl;

import kimp.cmc.dao.exchange.CmcExchangeDao;
import kimp.cmc.dao.exchange.CmcExchangeInfoDao;
import kimp.cmc.dao.exchange.CmcExchangeMetaDao;
import kimp.cmc.dao.exchange.CmcExchangeUrlDao;
import kimp.cmc.dto.response.CmcExchangeInfoResponseDto;
import kimp.cmc.entity.exchange.CmcExchange;
import kimp.cmc.repository.exchange.CmcExchangeRepository;
import kimp.cmc.service.CmcExchangeManageService;
import kimp.cmc.vo.GetAllExchangeInfoPageDataVo;
import kimp.exchange.service.ExchangeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CmcExchangeManageServiceImpl implements CmcExchangeManageService {

    private final ExchangeService exchangeService;

    private final CmcExchangeDao cmcExchangeDao;
    private final CmcExchangeInfoDao cmcExchangeInfoDao;
    private final CmcExchangeMetaDao cmcExchangeMetaDao;
    private final CmcExchangeUrlDao cmcExchangeUrlDao;
    private final CmcExchangeRepository cmcExchangeRepository;

    public CmcExchangeManageServiceImpl(ExchangeService exchangeService, CmcExchangeDao cmcExchangeDao, CmcExchangeInfoDao cmcExchangeInfoDao, CmcExchangeMetaDao cmcExchangeMetaDao, CmcExchangeUrlDao cmcExchangeUrlDao, CmcExchangeRepository cmcExchangeRepository) {
        this.exchangeService = exchangeService;
        this.cmcExchangeDao = cmcExchangeDao;
        this.cmcExchangeInfoDao = cmcExchangeInfoDao;
        this.cmcExchangeMetaDao = cmcExchangeMetaDao;
        this.cmcExchangeUrlDao = cmcExchangeUrlDao;
        this.cmcExchangeRepository = cmcExchangeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CmcExchangeInfoResponseDto> findAllExchangesOrderBySpotVolume(GetAllExchangeInfoPageDataVo vo) {
        // 페이지 정보 설정 (0-based 인덱스)
        int page = Math.max(0, vo.getPage() - 1);
        int size = vo.getSize();
        Pageable pageable = PageRequest.of(page, size);

        // SpotVolume 기준으로 정렬된 Exchange 페이지 조회 (fetch join으로 N+1 방지)
        Page<CmcExchange> exchangePage = cmcExchangeRepository.findAllOrderBySpotVolumeWithFetchJoin(pageable);
        
        // CmcExchange를 CmcExchangeInfoResponseDto로 변환 (이미 fetch join으로 모든 연관관계 로딩됨)
        List<CmcExchangeInfoResponseDto> responseDtos = exchangePage.getContent().stream()
                .map(this::buildCmcExchangeInfoResponseDto)
                .toList();
        
        return new PageImpl<>(responseDtos, pageable, exchangePage.getTotalElements());
    }
    
    /**
     * CmcExchange 엔티티를 CmcExchangeInfoResponseDto로 변환
     */
    private CmcExchangeInfoResponseDto buildCmcExchangeInfoResponseDto(CmcExchange cmcExchange) {
        // CmcExchange에서 필요한 데이터들을 추출
        String name = cmcExchange.getName();
        String slug = cmcExchange.getSlug();
        String description = cmcExchange.getDescription();
        String logo = cmcExchange.getLogo();
        Boolean isSupported = cmcExchange.getExchange() == null ? false : true;
        LocalDateTime dateLaunched = cmcExchange.getDateLaunched();
        LocalDateTime updatedAt = cmcExchange.getUpdatedAt();
        
        // CmcExchangeInfo에서 fiats 정보 추출
        List<String> fiats = new ArrayList<>();
        if (cmcExchange.getCmcExchangeInfo() != null && cmcExchange.getCmcExchangeInfo().getFiats() != null) {
            // fiats는 콤마로 구분된 문자열로 저장되어 있다고 가정
            String[] fiatArray = cmcExchange.getCmcExchangeInfo().getFiats().split(",");
            fiats = List.of(fiatArray);
        }
        
        // CmcExchangeMeta에서 fee와 spotVolumeUsd 정보 추출
        BigDecimal fee = null;
        BigDecimal spotVolumeUsd = null;
        if (cmcExchange.getCmcExchangeMeta() != null) {
            fee = cmcExchange.getCmcExchangeMeta().getMarketFee();
            spotVolumeUsd = cmcExchange.getCmcExchangeMeta().getSpotVolumeUsd();
        }
        
        // CmcExchangeUrl에서 URL 정보 추출
        String url = null;
        if (cmcExchange.getCmcExchangeUrl() != null) {
            url = cmcExchange.getCmcExchangeUrl().getWebsite();
        }
        
        return CmcExchangeInfoResponseDto.builder()
                .name(name)
                .slug(slug)
                .fiats(fiats)
                .description(description)
                .logo(logo)
                .fee(fee)
                .spotVolumeUsd(spotVolumeUsd)
                .url(url)
                .isSupported(isSupported)
                .dateLaunched(dateLaunched)
                .updatedAt(updatedAt)
                .build();
    }

}
