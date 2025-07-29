package kimp.exchange.service.impl;

import kimp.common.dto.PageRequestDto;
import kimp.common.method.DtoConverter;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.exchange.dao.ExchangeDao;
import kimp.notice.dao.NoticeDao;
import kimp.notice.dto.notice.ExchangeNoticeDto;
import kimp.notice.dto.notice.NoticeDto;
import kimp.notice.dto.notice.NoticeParsedData;
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
import java.util.List;

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

    @Transactional
    public ExchangeNoticeDto<NoticeDto> createNotice(MarketType marketType, String title, String link, LocalDateTime date){

        Exchange exchange = exchangeDao.getExchangeByMarketType(marketType);
        Notice notice = new Notice(title, link, date);

        Notice savedNotice = this.noticeDao.createNotice(notice);

        NoticeDto noticeDto = dtoConverter.convertNoticeToDto(savedNotice.setExchange(exchange));

        return dtoConverter.wrappingDtoToExchangeNoticeDto(noticeDto);
    }

    @Transactional
    public ExchangeNoticeDto<Page<NoticeDto>> getNoticeByExchange(MarketType marketType, PageRequestDto pageRequestDto) {
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
