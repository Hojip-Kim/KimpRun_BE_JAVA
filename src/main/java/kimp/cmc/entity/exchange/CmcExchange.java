package kimp.cmc.entity.exchange;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.exchange.entity.Exchange;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cmc_exchange",
        indexes = {
                @Index(name = "cmc_exchange_id_idx", columnList = "cmc_exchange_id")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = "cmc_exchange_id")
)
@Getter
@NoArgsConstructor
public class CmcExchange extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "cmc_exchange_id")
    private Long cmcExchangeId;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "slug")
    private String slug;

    @Column(nullable = false, name = "is_active")
    private Boolean isActive;

    @Column(nullable = false, name = "is_listed")
    private Boolean isListed;

    @Column(nullable = false, name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, name = "logo")
    private String logo;

    @Column(nullable = false, name = "date_launched")
    private LocalDateTime dateLaunched;

    @OneToOne(fetch = FetchType.LAZY)
    private Exchange exchange;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private CmcExchangeInfo cmcExchangeInfo;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private CmcExchangeMeta cmcExchangeMeta;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private CmcExchangeUrl cmcExchangeUrl;


    public CmcExchange(String name, Long cmcExchangeId, String slug, Boolean isActive, Boolean isListed, String description, String logo, LocalDateTime dateLaunched) {
        this.name = name;
        this.cmcExchangeId = cmcExchangeId;
        this.slug = slug;
        this.isActive = isActive;
        this.isListed = isListed;
        this.description = description;
        this.logo = logo;
        this.dateLaunched = dateLaunched;
    }

    public CmcExchange setExchange(Exchange exchange) {
        if( exchange == null){
            throw new IllegalArgumentException("exchange is null");
        }
        this.exchange = exchange;
        return this;
    }

    public CmcExchange updateExchange(Exchange exchange) {
        if( exchange == null){
            throw new IllegalArgumentException("exchange is null");
        }
        this.exchange = exchange;
        return this;
    }

    public CmcExchange deleteExchange() {
        this.exchange = null;
        return this;
    }

    public CmcExchange setCmcExchangeInfo(CmcExchangeInfo cmcExchangeInfo) {
        if( cmcExchangeInfo == null){
            throw new IllegalArgumentException("cmcExchangeInfo is null");
        }
        this.cmcExchangeInfo = cmcExchangeInfo;
        return this;
    }
    public CmcExchange updateCmcExchangeInfo(CmcExchangeInfo cmcExchangeInfo) {
        if( cmcExchangeInfo == null){
            throw new IllegalArgumentException("cmcExchangeInfo is null");
        }
        this.cmcExchangeInfo = cmcExchangeInfo;
        return this;
    }
    public CmcExchange deleteCmcExchangeInfo() {
        this.cmcExchangeInfo = null;
        return this;
    }

    public CmcExchange setCmcExchangeMeta(CmcExchangeMeta cmcExchangeMeta) {
        if( cmcExchangeMeta == null){
            throw new IllegalArgumentException("cmcExchangeMeta is null");
        }
        this.cmcExchangeMeta = cmcExchangeMeta;
        return this;
    }

    public CmcExchange updateCmcExchangeMeta(CmcExchangeMeta cmcExchangeMeta) {
        if( cmcExchangeMeta == null){
            throw new IllegalArgumentException("cmcExchangeMeta is null");
        }
        this.cmcExchangeMeta = cmcExchangeMeta;
        return this;
    }

    public CmcExchange deleteCmcExchangeMeta() {
        this.cmcExchangeMeta = null;
        return this;
    }

    public CmcExchange setCmcExchangeUrl(CmcExchangeUrl cmcExchangeUrl) {
        if( cmcExchangeUrl == null){
            throw new IllegalArgumentException("cmcExchangeUrl is null");
        }
        this.cmcExchangeUrl = cmcExchangeUrl;
        return this;
    }

    public CmcExchange updateCmcExchangeUrl(CmcExchangeUrl cmcExchangeUrl) {
        if( cmcExchangeUrl == null){
            throw new IllegalArgumentException("cmcExchangeUrl is null");
        }
        this.cmcExchangeUrl = cmcExchangeUrl;
        return this;
    }


}
