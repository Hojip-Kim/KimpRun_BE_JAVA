package kimp.notice.controller;

import kimp.common.dto.PageRequestDto;
import kimp.exception.response.ApiResponse;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.notice.dto.notice.ExchangeNoticeDto;
import kimp.notice.service.NoticeService;
import kimp.exchange.service.ScrapService;
import kimp.exchange.service.impl.ExchangeNoticePacadeService;
import kimp.market.Enum.MarketType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/notice")
public class NoticeController {

    private final ScrapService scrapService;
    private final NoticeService noticeService;
    private final ExchangeNoticePacadeService exchangeNoticePacadeService;

    public NoticeController(ScrapService scrapService, NoticeService noticeService, ExchangeNoticePacadeService exchangeNoticePacadeService) {
        this.scrapService = scrapService;
        this.noticeService = noticeService;

        this.exchangeNoticePacadeService = exchangeNoticePacadeService;
    }

    @GetMapping("/{exchangeType}")
    public ApiResponse<ExchangeNoticeDto> getNoticeByExchangeId(@PathVariable("exchangeType") MarketType exchangeType, @ModelAttribute PageRequestDto pageRequestDto) {
        if(exchangeType == null || pageRequestDto == null) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Exchange type and page request parameters cannot be null", HttpStatus.BAD_REQUEST, "NoticeController.getNoticeByExchangeId");
        }

        ExchangeNoticeDto noticeDtos;

        if(exchangeType.equals(MarketType.ALL)){
            noticeDtos = noticeService.getAllNotices(pageRequestDto);
        }else{
            noticeDtos = exchangeNoticePacadeService.getNoticeByExchange(exchangeType, pageRequestDto);
        }
        return ApiResponse.success(noticeDtos);

    }


}