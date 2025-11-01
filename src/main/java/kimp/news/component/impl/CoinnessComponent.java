package kimp.news.component.impl;

import kimp.news.dto.internal.coinness.CoinnessArticleDto;
import kimp.news.dto.internal.coinness.CoinnessBreakingNewsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class CoinnessComponent {

    private final RestClient restClient;

    @Value("${coinness.api.breaking-news.url:https://api.coinness.com/feed/v1/breaking-news}")
    private String breakingNewsUrl;

    @Value("${coinness.api.articles.url:https://api.coinness.com/feed/v1/articles}")
    private String articlesUrl;

    @Value("${coinness.api.language:ko}")
    private String languageCode;

    @Value("${coinness.api.articles.limit:25}")
    private Integer articlesLimit;

    @Value("${coinness.api.articles.section:latest}")
    private String articlesSection;

    @Value("${coinness.api.articles.categoryId:0}")
    private Integer articlesCategoryId;

    public CoinnessComponent(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    /**
     * Breaking News 가져오기
     */
    public List<CoinnessBreakingNewsDto> fetchBreakingNews() {
        try {
            String url = String.format("%s?languageCode=%s", breakingNewsUrl, languageCode);

            List<CoinnessBreakingNewsDto> newsList = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CoinnessBreakingNewsDto>>() {});

            if (newsList == null || newsList.isEmpty()) {
                log.warn("코인니스 속보 API 응답이 비어있음");
                return Collections.emptyList();
            }

            return newsList;

        } catch (Exception e) {
            log.error("코인니스 API 속보 조회 중 오류 발생", e);
            return Collections.emptyList();
        }
    }

    /**
     * Articles 가져오기
     */
    public List<CoinnessArticleDto> fetchArticles() {
        try {
            String url = String.format("%s?limit=%d&section=%s&categoryId=%d&languageCode=%s",
                    articlesUrl, articlesLimit, articlesSection, articlesCategoryId, languageCode);

            List<CoinnessArticleDto> articles = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CoinnessArticleDto>>() {});

            if (articles == null || articles.isEmpty()) {
                log.warn("코인니스 기사 API 응답이 비어있음");
                return Collections.emptyList();
            }

            return articles;

        } catch (Exception e) {
            log.error("코인니스 API 기사 조회 중 오류 발생", e);
            return Collections.emptyList();
        }
    }

    public List<CoinnessArticleDto> fetchArticlesWithParams(int limit, String section, int categoryId) {
        try {
            String url = String.format("%s?limit=%d&section=%s&categoryId=%d&languageCode=%s",
                    articlesUrl, limit, section, categoryId, languageCode);

            List<CoinnessArticleDto> articles = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CoinnessArticleDto>>() {});

            if (articles == null || articles.isEmpty()) {
                log.warn("코인니스 기사 API 응답이 비어있음");
                return Collections.emptyList();
            }

            return articles;

        } catch (Exception e) {
            log.error("코인니스 API 커스텀 파라미터 기사 조회 중 오류 발생", e);
            return Collections.emptyList();
        }
    }

    public String getNewsSource() {
        return "Coinness";
    }

    public String getBreakingNewsApiUrl() {
        return this.breakingNewsUrl;
    }

    public String getArticlesApiUrl() {
        return this.articlesUrl;
    }
}
