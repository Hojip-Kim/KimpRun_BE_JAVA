package kimp.news.entity;

import jakarta.persistence.*;
import kimp.news.enums.NewsSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 뉴스 엔티티
 * 다중 소스(블루밍비트, 코인니스 등)의 암호화폐 뉴스를 통합 관리
 */
@Entity
@Table(name = "news",
    indexes = {
        @Index(name = "idx_news_source", columnList = "newsSource"),
        @Index(name = "idx_news_create_epoch", columnList = "createEpochMillis"),
        @Index(name = "idx_news_type", columnList = "newsType"),
        @Index(name = "idx_news_headline", columnList = "isHeadline"),
        @Index(name = "idx_news_source_create_epoch", columnList = "newsSource,createEpochMillis")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_news_source_seq", columnNames = {"newsSource", "sourceSequenceId"})
    }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class News {

    /** PK */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 뉴스 소스 (예: BLOOMING_BIT, COINNESS) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NewsSource newsSource;

    /** 원본 뉴스 소스에서 제공하는 고유 시퀀스 ID (newsSource와 조합하여 unique) */
    @Column(nullable = false)
    private Long sourceSequenceId;

    /** 뉴스 타입 (예: 속보, 일반기사 등) */
    @Column(length = 50)
    private String newsType;

    /** 뉴스 지역 (예: 국내, 해외 등) */
    @Column(length = 50)
    private String region;

    /** 뉴스 제목 */
    @Column(nullable = false, length = 500)
    private String title;

    /** 플레인 텍스트 형식의 뉴스 본문 */
    @Column(columnDefinition = "TEXT")
    private String plainTextContent;

    /** 마크다운 형식의 뉴스 본문 */
    @Column(columnDefinition = "TEXT")
    private String markdownContent;

    /** 썸네일 이미지 URL */
    @Column(length = 500)
    private String thumbnail;

    /** 감정 분석 결과 (예: POSITIVE, NEGATIVE, NEUTRAL) */
    @Column(length = 50)
    private String sentiment;

    /** 원본 뉴스 URL */
    @Column(nullable = false, length = 1000)
    private String sourceUrl;

    /** 뉴스 생성 시각 (epoch 밀리초) */
    @Column(nullable = false)
    private Long createEpochMillis;

    /** 뉴스 수정 시각 (epoch 밀리초) */
    @Column(nullable = false)
    private Long updateEpochMillis;

    /** 변화값 (시세 관련 변화량 등) */
    @Column(name = "change_value")
    private Integer changeValue;

    /** 신규 뉴스 여부 */
    @Column
    private Boolean isNew;

    /** 헤드라인 뉴스 여부 */
    @Column
    private Boolean isHeadline;

    /** 레코드 생성 시각 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 레코드 수정 시각 */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 코인니스 속보 뉴스 업데이트
     */
    public void updateFromCoinnessBreakingNews(String title, String content, String thumbnailImage,
                                                String sentiment, String sourceUrl, Long updateEpochMillis,
                                                Boolean isImportant) {
        this.title = title;
        this.plainTextContent = content;
        this.markdownContent = content;
        this.thumbnail = thumbnailImage;
        this.sentiment = sentiment;
        this.sourceUrl = sourceUrl;
        this.updateEpochMillis = updateEpochMillis;
        this.isNew = true;
        this.isHeadline = isImportant != null && isImportant;
    }

    /**
     * 코인니스 기사 뉴스 업데이트
     */
    public void updateFromCoinnessArticle(String title, String description, String thumbnailImage,
                                          String sourceUrl, Long updateEpochMillis) {
        this.title = title;
        this.plainTextContent = description;
        this.markdownContent = description;
        this.thumbnail = thumbnailImage;
        this.sentiment = "NEUTRAL";
        this.sourceUrl = sourceUrl;
        this.updateEpochMillis = updateEpochMillis;
        this.isNew = true;
        this.isHeadline = false;
    }

    /**
     * 블루밍비트 뉴스 업데이트
     */
    public void updateFromBloomingBit(String newsType, String region, String title,
                                      String plainTextContent, String markdownContent,
                                      String thumbnail, String sentiment, String sourceUrl,
                                      Long updateEpochMillis, Integer changeValue,
                                      Boolean isNew, Boolean isHeadline) {
        this.newsType = newsType;
        this.region = region;
        this.title = title;
        this.plainTextContent = plainTextContent;
        this.markdownContent = markdownContent;
        this.thumbnail = thumbnail;
        this.sentiment = sentiment;
        this.sourceUrl = sourceUrl;
        this.updateEpochMillis = updateEpochMillis;
        this.changeValue = changeValue;
        this.isNew = isNew;
        this.isHeadline = isHeadline;
    }
}
