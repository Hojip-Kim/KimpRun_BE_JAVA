package kimp.notice.entity;


import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.exchange.entity.Exchange;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Entity
@Table(name= "notice")
@Getter
public class Notice extends TimeStamp {

    public Notice(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id")
    private Exchange exchange;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String link;

    @Column(nullable = false)
    private LocalDateTime date;

    public Notice(String title, String link, LocalDateTime date) {
        this.title = title;
        this.link = link;
        this.date = date;
    }

    public Notice setExchange(Exchange exchange) {
        if(this.exchange == null){
            this.exchange = exchange;
        }else{
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Exchange is already set for this notice", HttpStatus.BAD_REQUEST, "Notice.setExchange");
        }
        return this;
    }

    public Notice updateTitle(String title) {
        if(this.title.equals(title)){
            return this;
        }
        if(title.isEmpty()){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Notice title cannot be empty", HttpStatus.BAD_REQUEST, "Notice.updateTitle");
        }
        this.title = title;
        return this;
    }

    public Notice updateLink(String link) {
        if(this.link.equals(link)){
            return this;
        }
        if(link.isEmpty()){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Notice link cannot be empty", HttpStatus.BAD_REQUEST, "Notice.updateLink");
        }
        this.link = link;
        return this;
    }

    public Notice updateDate(LocalDateTime date) {
        if(date == null){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Notice date cannot be null", HttpStatus.BAD_REQUEST, "Notice.updateDate");
        }
        this.date = date;
        return this;
    }

}
