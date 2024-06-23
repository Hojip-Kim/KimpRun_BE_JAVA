package kimp.market.dto.response;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Ticker {

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

    public Ticker() {
    }


}
