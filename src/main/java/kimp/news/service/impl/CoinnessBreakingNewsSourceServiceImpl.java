package kimp.news.service.impl;

import kimp.news.dto.internal.coinness.CoinnessBreakingNewsDto;
import kimp.news.entity.News;
import kimp.news.entity.NewsKeyword;
import kimp.news.enums.NewsSource;
import kimp.news.service.NewsSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("coinnessBreakingNewsSourceService")
public class CoinnessBreakingNewsSourceServiceImpl implements NewsSourceService<CoinnessBreakingNewsDto> {

    private final kimp.news.dao.NewsKeywordDao newsKeywordDao;

    public CoinnessBreakingNewsSourceServiceImpl(kimp.news.dao.NewsKeywordDao newsKeywordDao) {
        this.newsKeywordDao = newsKeywordDao;
    }

    @Override
    public News createNewsFromSource(CoinnessBreakingNewsDto newsSourceDto) {
        News news = News.builder()
                .newsSource(NewsSource.COINNESS)
                .sourceSequenceId(newsSourceDto.getId())
                .newsType("breaking-news")
                .region("KR")
                .title(newsSourceDto.getTitle())
                .plainTextContent(newsSourceDto.getContent())
                .markdownContent(newsSourceDto.getContent())
                .thumbnail(newsSourceDto.getThumbnailImage())
                .sentiment(determineSentiment(newsSourceDto))
                .sourceUrl(newsSourceDto.getSourceUrl())
                .createEpochMillis(newsSourceDto.getCreateEpochMillis())
                .updateEpochMillis(newsSourceDto.getCreateEpochMillis())
                .changeValue(null)
                .isNew(true)
                .isHeadline(newsSourceDto.getIsImportant() != null && newsSourceDto.getIsImportant())
                .build();

        return news;
    }

    public void saveNewsCollections(News savedNews, CoinnessBreakingNewsDto newsSourceDto) {
        List<NewsKeyword> keywords = new ArrayList<>();

        if (newsSourceDto.getOriginCodes() != null && !newsSourceDto.getOriginCodes().isEmpty()) {
            newsSourceDto.getOriginCodes().forEach(code -> {
                keywords.add(NewsKeyword.builder()
                        .news(savedNews)
                        .keyword(code)
                        .build());
            });
        }

        if (newsSourceDto.getQuickOrderCode() != null && !newsSourceDto.getQuickOrderCode().isEmpty()) {
            keywords.add(NewsKeyword.builder()
                    .news(savedNews)
                    .keyword(newsSourceDto.getQuickOrderCode())
                    .build());
        }

        if (!keywords.isEmpty()) {
            newsKeywordDao.saveAll(keywords);
        }
    }

    @Override
    public News updateNewsFromSource(News existingNews, CoinnessBreakingNewsDto newsSourceDto) {
        News updatedNews = News.builder()
                .id(existingNews.getId())
                .newsSource(NewsSource.COINNESS)
                .sourceSequenceId(newsSourceDto.getId())
                .newsType("breaking-news")
                .region("KR")
                .title(newsSourceDto.getTitle())
                .plainTextContent(newsSourceDto.getContent())
                .markdownContent(newsSourceDto.getContent())
                .thumbnail(newsSourceDto.getThumbnailImage())
                .sentiment(determineSentiment(newsSourceDto))
                .sourceUrl(newsSourceDto.getSourceUrl())
                .createEpochMillis(newsSourceDto.getCreateEpochMillis())
                .updateEpochMillis(newsSourceDto.getCreateEpochMillis())
                .changeValue(null)
                .isNew(true)
                .isHeadline(newsSourceDto.getIsImportant() != null && newsSourceDto.getIsImportant())
                .createdAt(existingNews.getCreatedAt())
                .build();

        return updatedNews;
    }

    public void updateNewsCollections(News savedNews, CoinnessBreakingNewsDto newsSourceDto) {
        List<String> keywordList = new ArrayList<>();

        if (newsSourceDto.getOriginCodes() != null) {
            keywordList.addAll(newsSourceDto.getOriginCodes());
        }

        if (newsSourceDto.getQuickOrderCode() != null && !newsSourceDto.getQuickOrderCode().isEmpty()) {
            keywordList.add(newsSourceDto.getQuickOrderCode());
        }

        if (!keywordList.isEmpty()) {
            newsKeywordDao.updateKeywords(savedNews.getId(), keywordList);
        }
    }

    @Override
    public List<News> createNewsListFromSource(List<CoinnessBreakingNewsDto> newsSourceDtos) {
        return newsSourceDtos.stream()
                .map(this::createNewsFromSource)
                .collect(Collectors.toList());
    }

    @Override
    public String getNewsSourceName() {
        return "Coinness-BreakingNews";
    }

    private String determineSentiment(CoinnessBreakingNewsDto newsDto) {
        // isBull 값 기반 sentiment 결정
        if (newsDto.getIsBull() != null && newsDto.getIsBull()) {
            return "POSITIVE";
        }

        String title = newsDto.getTitle() != null ? newsDto.getTitle().toLowerCase() : "";
        String content = newsDto.getContent() != null ? newsDto.getContent().toLowerCase() : "";

        if (title.contains("상승") || title.contains("증가") || content.contains("긍정")) {
            return "POSITIVE";
        } else if (title.contains("하락") || title.contains("감소") || content.contains("부정")) {
            return "NEGATIVE";
        }

        return "NEUTRAL";
    }
}
