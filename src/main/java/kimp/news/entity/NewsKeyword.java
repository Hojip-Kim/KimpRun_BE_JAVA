package kimp.news.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 뉴스 키워드 엔티티
 * 뉴스에 태그된 키워드 정보 (예: 비트코인, 이더리움, 알트코인 등)
 */
@Entity
@Table(name = "news_keyword", indexes = {
    @Index(name = "idx_keyword_news_id", columnList = "news_id"),
    @Index(name = "idx_keyword_text", columnList = "keyword")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsKeyword {

    /** PK */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 연관된 뉴스 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    /** 키워드 텍스트 */
    @Column(nullable = false, length = 100)
    private String keyword;

    public void setNews(News news) {
        this.news = news;
    }
}
