package kimp.market.dto.coin.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoinMarketDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("symbol")
    private String symbol;
    
    public CoinMarketDto() {
    }
    
    public CoinMarketDto(Long id, String symbol) {
        this.id = id;
        this.symbol = symbol;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}