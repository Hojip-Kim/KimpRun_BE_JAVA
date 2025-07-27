package kimp.exchange.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.cmc.entity.exchange.CmcExchange;
import kimp.market.Enum.MarketType;
import kimp.market.entity.CoinExchange;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "exchange")
@NoArgsConstructor
public class Exchange extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MarketType market;

    @Column(nullable = false)
    private String link;

    @OneToMany(mappedBy = "exchange", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CoinExchange> coinExchanges = new ArrayList<>();

    @OneToOne(mappedBy = "exchange", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "cmc_exchange_id", referencedColumnName = "cmc_exchange_id", nullable = true)
    private CmcExchange cmcExchange;

    public Exchange(MarketType market, String link) {
        this.market = market;
        this.link = link;
    }

    public Exchange updateExchangeName(MarketType market){
        if(!isValidString(market.name())){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Exchange name cannot be null or empty", HttpStatus.BAD_REQUEST, "Exchange.updateExchangeName");
        }

        if (this.market == null || (this.market != null && this.market.name().isEmpty())) {
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "Cannot update exchange name - current name is invalid", HttpStatus.BAD_REQUEST, "Exchange.updateExchangeName");
        }

        this.market = market;

        return this;

    }

    public Exchange updateExchangeLink(String link){
        if(!isValidString(link)){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Exchange link cannot be null or empty", HttpStatus.BAD_REQUEST, "Exchange.updateExchangeLink");
        }

        if (this.link == null || (this.link != null && this.link.isBlank())) {
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "Cannot update exchange link - current link is invalid", HttpStatus.BAD_REQUEST, "Exchange.updateExchangeLink");
        }

        this.link = link;

        return this;
    }

    private boolean isValidString(String param){
        if(param == null || param.isEmpty()) {
            return false;
        }
        return true;
    }

    public void addCoinExchanges(CoinExchange coinExchange){
        this.coinExchanges.add(coinExchange);
        coinExchange.setExchange(this);
    }

    public void removeCoinExchanges(CoinExchange coinExchange){
        this.coinExchanges.remove(coinExchange);
        coinExchange.setExchange(null);
    }

    public Exchange setCmcExchange(CmcExchange cmcExchange){
        if(this.cmcExchange != null){
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_ALREADY_EXISTS_EXCEPTION, "CMC Exchange is already set for this exchange", HttpStatus.CONFLICT, "Exchange.setCmcExchange");
        }

        this.cmcExchange = cmcExchange;
        return this;
    }

    public Exchange updateCmcExchange(CmcExchange cmcExchange){
        if(this.cmcExchange == null){
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, "Cannot update CMC Exchange - no existing CMC Exchange found", HttpStatus.NOT_FOUND, "Exchange.updateCmcExchange");
        }
        this.cmcExchange = cmcExchange;
        return this;
    }

    public Exchange deleteCmcExchange(){
        if(this.cmcExchange == null){
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, "Cannot delete CMC Exchange - no existing CMC Exchange found", HttpStatus.NOT_FOUND, "Exchange.deleteCmcExchange");
        }
        this.cmcExchange = null;
        return this;
    }


}
