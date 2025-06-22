package kimp.exchange.controller;

import kimp.common.dto.PageRequestDto;
import kimp.exchange.dto.notice.ExchangeNoticeDto;
import kimp.exchange.service.NoticeService;
import kimp.exchange.service.ScrapService;
import kimp.exchange.service.impl.ExchangeNoticePacadeService;
import kimp.market.Enum.MarketType;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
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
    public ResponseEntity<?> getNoticeByExchangeId(@PathVariable("exchangeType") MarketType exchangeType, @ModelAttribute PageRequestDto pageRequestDto) throws BadRequestException {
        if(exchangeType == null || pageRequestDto == null) {
            throw new BadRequestException();
        }

        ExchangeNoticeDto noticeDtos;

        if(exchangeType.equals(MarketType.ALL)){
            noticeDtos = noticeService.getAllNotices(pageRequestDto);
        }else{
            noticeDtos = exchangeNoticePacadeService.getNoticeByExchange(exchangeType, pageRequestDto);
        }
        return ResponseEntity.ok(noticeDtos);

    }


}