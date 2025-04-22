package kimp.exchange.controller;

import kimp.common.dto.PageRequestDto;
import kimp.exchange.dto.notice.ExchangeNoticeDto;
import kimp.exchange.dto.notice.NoticeParsedData;
import kimp.exchange.service.NoticeService;
import kimp.exchange.service.ScrapService;
import kimp.exchange.service.impl.ExchangeNoticePacadeService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{exchangeId}")
    public ResponseEntity<?> getNoticeByExchangeId(@PathVariable("exchangeId") long exchangeId, @ModelAttribute PageRequestDto pageRequestDto) throws BadRequestException {

        if(pageRequestDto == null){
            throw new BadRequestException();
        }

        ExchangeNoticeDto noticeDtos = exchangeNoticePacadeService.getNoticeByExchange(exchangeId, pageRequestDto);
        return ResponseEntity.ok(noticeDtos);
    }


}