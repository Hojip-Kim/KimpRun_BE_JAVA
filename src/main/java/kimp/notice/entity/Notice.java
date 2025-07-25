package kimp.notice.entity;


import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.exchange.entity.Exchange;
import lombok.Getter;

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

    @Column(nullable = false)
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
            throw new IllegalArgumentException("exchange is already set");
        }
        return this;
    }

    public Notice updateTitle(String title) {
        if(this.title.equals(title)){
            return this;
        }
        if(title.isEmpty()){
            throw new IllegalArgumentException("title is empty");
        }
        this.title = title;
        return this;
    }

    public Notice updateLink(String link) {
        if(this.link.equals(link)){
            return this;
        }
        if(link.isEmpty()){
            throw new IllegalArgumentException("link is empty");
        }
        this.link = link;
        return this;
    }

    public Notice updateDate(LocalDateTime date) {
        if(date == null){
            throw new IllegalArgumentException("date is null");
        }
        this.date = date;
        return this;
    }

}
