package kimp.exchange.service.impl;

import kimp.common.dto.request.PageRequestDto;
import kimp.common.method.DtoConverter;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.exchange.dao.ExchangeDao;
import kimp.notice.dao.NoticeDao;
import kimp.notice.dto.response.ExchangeNoticeDto;
import kimp.notice.dto.response.NoticeDto;
import kimp.notice.dto.response.NoticeParsedData;
import kimp.exchange.entity.Exchange;
import kimp.notice.entity.Notice;
import kimp.market.Enum.MarketType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExchangeNoticePacadeService {

    private final NoticeDao noticeDao;
    private final ExchangeDao exchangeDao;
    private final DtoConverter dtoConverter;

    public ExchangeNoticePacadeService(NoticeDao noticeDao, ExchangeDao exchangeDao, DtoConverter dtoConverter) {
        this.noticeDao = noticeDao;
        this.exchangeDao = exchangeDao;
        this.dtoConverter = dtoConverter;
    }

    @Transactional
    public boolean createNoticesBulk(MarketType marketType, List<NoticeParsedData> noticeParsedDataList){
        List<Notice> noticeList = new ArrayList<>();

        Exchange exchange = exchangeDao.getExchangeByMarketType(marketType);

        for(NoticeParsedData noticeParsedData : noticeParsedDataList){

            Notice exsitingNotice = noticeDao.getNoticeByLink(noticeParsedData.getAlink());

            if(exsitingNotice == null){
                Notice notice = new Notice(noticeParsedData.getTitle(), noticeParsedData.getAlink(), noticeParsedData.getDate());

                Notice setNotice = notice.setExchange(exchange);

                noticeList.add(setNotice);
            }

        }
        if(noticeList.isEmpty()){
            return true;
        }

        return this.noticeDao.createBulkNotice(noticeList);
    }


    /**
     * 최적화된 JPA 배치를 사용한 공지사항 대량 생성 메서드
     * - N+1 문제 해결 (한번의 SELECT로 기존 링크 확인)
     * - Hibernate batch_size 설정 활용한 JPA 배치 INSERT
     */
    @Transactional
    public boolean createNoticesBulkOptimized(MarketType marketType, List<NoticeParsedData> noticeParsedDataList){
        if (noticeParsedDataList == null || noticeParsedDataList.isEmpty()) {
            return true;
        }

        // 1. Exchange 정보 미리 조회 (1 query, CmcExchange 관계 포함하여 N+1 문제 방지)
        Exchange exchange = exchangeDao.getExchangeByMarketTypeWithCmcExchange(marketType);

        // 2. 모든 새로운 링크들을 Set으로 추출 (중복 제거 및 빠른 조회)
        Set<String> newNoticeLinks = noticeParsedDataList.stream()
                .map(NoticeParsedData::getAlink)
                .filter(link -> link != null && !link.trim().isEmpty())
                .collect(Collectors.toSet());

        if (newNoticeLinks.isEmpty()) {
            return true;
        }

        // 3. 기존 공지사항 링크들을 한번에 조회 (1 query로 N+1 문제 해결)
        List<String> existingLinks = noticeDao.findExistingNoticeLinks(new ArrayList<>(newNoticeLinks));
        Set<String> existingLinkSet = new HashSet<>(existingLinks);

        // 4. 메모리에서 중복되지 않은 공지사항만 필터링하여 Notice 객체 생성
        List<Notice> noticeList = noticeParsedDataList.stream()
                .filter(noticeParsedData -> {
                    String link = noticeParsedData.getAlink();
                    return link != null && !link.trim().isEmpty() && !existingLinkSet.contains(link);
                })
                .map(noticeParsedData -> {
                    Notice notice = new Notice(
                            noticeParsedData.getTitle(), 
                            noticeParsedData.getAlink(), 
                            noticeParsedData.getDate()
                    );
                    return notice.setExchange(exchange);
                })
                .collect(Collectors.toList());

        // 5. 생성할 공지사항이 없으면 성공 반환
        if (noticeList.isEmpty()) {
            return true;
        }

        // 6. JPA 배치 INSERT (Hibernate batch_size 설정 활용)
        return this.noticeDao.createBulkNoticeOptimized(noticeList);
    }

    @Transactional
    public ExchangeNoticeDto<NoticeDto> createNotice(MarketType marketType, String title, String link, LocalDateTime date){

        Exchange exchange = exchangeDao.getExchangeByMarketType(marketType);
        Notice notice = new Notice(title, link, date);

        Notice savedNotice = this.noticeDao.createNotice(notice);

        NoticeDto noticeDto = dtoConverter.convertNoticeToDto(savedNotice.setExchange(exchange));

        return dtoConverter.wrappingDtoToExchangeNoticeDto(noticeDto);
    }

    @Transactional
    public ExchangeNoticeDto<Page<NoticeDto>> getNoticeByExchange(kimp.notice.vo.GetNoticeByExchangeVo vo) {
        MarketType marketType = vo.getExchangeType();
        PageRequestDto pageRequestDto = vo.getPageRequestDto();
        Pageable pageable = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize());

        Exchange exchange = exchangeDao.getExchangeByMarketType(marketType);

        Page<Notice> noticePage = noticeDao.findByExchangeIdOrderByRegistedAtAsc(exchange.getId(), pageable);

        if(noticePage.isEmpty()){
            throw new KimprunException(KimprunExceptionEnum.REQUEST_ACCEPTED, "Not have data", HttpStatus.ACCEPTED, "hello");
        }
        Page<NoticeDto> pageNoticeDto = dtoConverter.convertNoticePageToDtoPage(noticePage);

        return dtoConverter.wrappingDtosToExchangeNoticeDto(marketType,pageNoticeDto);
    }

}
