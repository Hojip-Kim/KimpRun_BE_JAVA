package kimp.websocket;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TradeSubscribe {
    @JsonProperty("type")
    private String type;

    @JsonProperty("codes")
    private List<String> codes;

    @JsonProperty("isOnlySnapshot")
    private Boolean isOnlySnapshot;

    @JsonProperty("isOnlyRealtime")
    private Boolean isOnlyRealtime;

    public TradeSubscribe(String type, List<String> codes, Boolean isOnlySnapshot, Boolean isOnlyRealtime) {
        this.type = type;
        this.codes = codes;
        this.isOnlySnapshot = isOnlySnapshot;
        this.isOnlyRealtime = isOnlyRealtime;
    }


}
