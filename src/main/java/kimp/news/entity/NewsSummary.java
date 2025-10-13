package kimp.news.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 뉴스 요약 엔티티
 * 뉴스 본문의 핵심 내용을 요약한 정보 (복수 개 가능)
 */
@Entity
@Table(name = "news_summary", indexes = {
    @Index(name = "idx_summary_news_id", columnList = "news_id")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 연관된 뉴스 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    /** 요약 내용 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    /** 표시 순서 (여러 요약이 있을 경우 순서 지정) */
    @Column(nullable = false)
    private Integer displayOrder;

    public void setNews(News news) {
        this.news = news;
    }
}
