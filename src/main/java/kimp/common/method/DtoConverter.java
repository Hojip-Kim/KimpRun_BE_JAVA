package kimp.common.method;

import kimp.exchange.dto.exchange.response.ExchangeDto;
import kimp.exchange.dto.notice.ExchangeNoticeDto;
import kimp.exchange.dto.notice.NoticeDto;
import kimp.exchange.entity.Exchange;
import kimp.exchange.entity.Notice;
import kimp.market.Enum.MarketType;
import kimp.market.dto.coin.response.CoinResponseDto;
import kimp.market.dto.coin.response.CoinResponseWithMarketTypeDto;
import kimp.market.entity.Coin;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DtoConverter {

    private final MarketMethod marketMethod;

    public DtoConverter(MarketMethod marketMethod) {
        this.marketMethod = marketMethod;
    }
    // Exchange

    public ExchangeDto convertExchangeToExchangeDto(Exchange exchange) {
        return new ExchangeDto(exchange.getId(), exchange.getMarket(), exchange.getLink());
    }

    public List<ExchangeDto> convertExchangeListToExchangeDtoList(List<Exchange> exchanges) {

        List<ExchangeDto> result = new ArrayList<>(exchanges.size());

        for(Exchange exchange : exchanges) {
            result.add(convertExchangeToExchangeDto(exchange));
        }

        return result;
    }

    // Coin

    public CoinResponseWithMarketTypeDto convertCoinToCoinResponseWithMarketTypeDto(Coin coin) {
        return new CoinResponseWithMarketTypeDto(coin.getId(), coin.getSymbol(), coin.getName(), coin.getEnglishName(), coin.getMarketTypes());
    }

    public CoinResponseDto convertCoinToCoinResponseDto(Coin coin) {
        return new CoinResponseDto(coin.getId(), coin.getSymbol(), coin.getName(), coin.getEnglishName());
    }

    public List<CoinResponseDto> convertCoinListToCoinResponseDtoList(List<Coin> coins) {
        return coins.stream().map(coin -> convertCoinToCoinResponseDto(coin)).toList();
    }

    // Notice

    public NoticeDto convertNoticeToDto(Notice notice) {
        if(notice == null){
            throw new IllegalArgumentException("not have notice");
        }

        NoticeDto noticeDto =  new NoticeDto(notice.getId() ,notice.getExchange().getMarket(),notice.getTitle(),notice.getLink(), notice.getDate());
        noticeDto.setExchangeUrl(noticeDto.getExchangeType().getNoticeUrl());

        return noticeDto;
    }

    public Page<NoticeDto> convertNoticePageToDtoPage(Page<Notice> notices) {

        return notices.map(this::convertNoticeToDto);
    }

    public ExchangeNoticeDto<NoticeDto> wrappingDtoToExchangeNoticeDto(NoticeDto noticeDto){
        MarketType marketType = noticeDto.getExchangeType();
        String absoluteUrl = marketMethod.getMarketAbsoluteUrlByMarketType(marketType);

        ExchangeNoticeDto<NoticeDto> dto = new ExchangeNoticeDto<>(absoluteUrl, marketType);
        return dto.setData(noticeDto);

    }

    // notice의 market type 종류가 하나만 있는 list를 dto로 변환
    public ExchangeNoticeDto<Page<NoticeDto>> wrappingDtosToExchangeNoticeDto(MarketType marketType, Page<NoticeDto> noticeDtos){
        String absoluteUrl;
         absoluteUrl = marketMethod.getMarketAbsoluteUrlByMarketType(marketType);

        ExchangeNoticeDto<Page<NoticeDto>> dto = new ExchangeNoticeDto<>(absoluteUrl, marketType);

        return dto.setData(noticeDtos);
    }

}
