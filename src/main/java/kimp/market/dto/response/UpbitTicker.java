package kimp.market.dto.response;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UpbitTicker extends Ticker {
    private String market;
    private String trade_date;
    private String trade_time;
    private String trade_date_kst;
    private String trade_time_kst;
    private long trade_timestamp;
    private BigDecimal opening_price;
    private BigDecimal high_price;
    private BigDecimal low_price;
    private BigDecimal trade_price;
    private BigDecimal prev_closing_price;
    private String change;
    private BigDecimal change_price;
    private BigDecimal change_rate;
    private BigDecimal signed_change_price;
    private BigDecimal signed_change_rate;
    private BigDecimal trade_volume;
    private BigDecimal acc_trade_price;
    private BigDecimal acc_trade_price_24h;
    private BigDecimal acc_trade_volume;
    private BigDecimal acc_trade_volume_24h;
    private BigDecimal highest_52_week_price;
    private String highest_52_week_date;
    private BigDecimal lowest_52_week_price;
    private String lowest_52_week_date;
    private long timestamp;

    public UpbitTicker(){};

    public UpbitTicker(String market, String trade_date, String trade_time, String trade_date_kst, String trade_time_kst, long trade_timestamp, BigDecimal opening_price, BigDecimal high_price, BigDecimal low_price, BigDecimal trade_price, BigDecimal prev_closing_price, String change, BigDecimal change_price, BigDecimal change_rate, BigDecimal signed_change_price, BigDecimal signed_change_rate, BigDecimal trade_volume, BigDecimal acc_trade_price, BigDecimal acc_trade_price_24h, BigDecimal acc_trade_volume, BigDecimal acc_trade_volume_24h, BigDecimal highest_52_week_price, String highest_52_week_date, BigDecimal lowest_52_week_price, String lowest_52_week_date, long timestamp) {
        this.market = market;
        this.trade_date = trade_date;
        this.trade_time = trade_time;
        this.trade_date_kst = trade_date_kst;
        this.trade_time_kst = trade_time_kst;
        this.trade_timestamp = trade_timestamp;
        this.opening_price = opening_price;
        this.high_price = high_price;
        this.low_price = low_price;
        this.trade_price = trade_price;
        this.prev_closing_price = prev_closing_price;
        this.change = change;
        this.change_price = change_price;
        this.change_rate = change_rate;
        this.signed_change_price = signed_change_price;
        this.signed_change_rate = signed_change_rate;
        this.trade_volume = trade_volume;
        this.acc_trade_price = acc_trade_price;
        this.acc_trade_price_24h = acc_trade_price_24h;
        this.acc_trade_volume = acc_trade_volume;
        this.acc_trade_volume_24h = acc_trade_volume_24h;
        this.highest_52_week_price = highest_52_week_price;
        this.highest_52_week_date = highest_52_week_date;
        this.lowest_52_week_price = lowest_52_week_price;
        this.lowest_52_week_date = lowest_52_week_date;
        this.timestamp = timestamp;
    }
}
