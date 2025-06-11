package kimp.market.dto.market.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpbitTicker extends Ticker {
    @NotNull
    private String market;
    @NotNull
    private String trade_date;
    @NotNull
    private String trade_time;
    @NotNull
    private String trade_date_kst;
    @NotNull
    private String trade_time_kst;
    @NotNull
    private long trade_timestamp;
    @NotNull
    private BigDecimal opening_price;
    @NotNull
    private BigDecimal high_price;
    @NotNull
    private BigDecimal low_price;
    @NotNull
    private BigDecimal trade_price;
    @NotNull
    private BigDecimal prev_closing_price;
    @NotNull
    private String change;
    @NotNull
    private BigDecimal change_price;
    @NotNull
    private BigDecimal change_rate;
    @NotNull
    private BigDecimal signed_change_price;
    @NotNull
    private BigDecimal signed_change_rate;
    @NotNull
    private BigDecimal trade_volume;
    @NotNull
    private BigDecimal acc_trade_price;
    @NotNull
    private BigDecimal acc_trade_price_24h;
    @NotNull
    private BigDecimal acc_trade_volume;
    @NotNull
    private BigDecimal acc_trade_volume_24h;
    @NotNull
    private BigDecimal highest_52_week_price;
    @NotNull
    private String highest_52_week_date;
    @NotNull
    private BigDecimal lowest_52_week_price;
    @NotNull
    private String lowest_52_week_date;
    @NotNull
    private long timestamp;
}
