package kimp.news.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 뉴스 인사이트 엔티티
 * 뉴스에 대한 분석이나 인사이트 정보 (예: 시장 영향 분석, 전문가 의견 등, 복수 개 가능)
 */
@Entity
@Table(name = "news_insight", indexes = {
    @Index(name = "idx_insight_news_id", columnList = "news_id")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsInsight {

    /** PK */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 연관된 뉴스 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    /** 인사이트 내용 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String insight;

    /** 표시 순서 (여러 인사이트가 있을 경우 순서 지정) */
    @Column(nullable = false)
    private Integer displayOrder;

    public void setNews(News news) {
        this.news = news;
    }
}
