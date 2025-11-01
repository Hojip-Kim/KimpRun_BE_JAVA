package kimp.news.service.impl;

import kimp.news.dao.NewsInsightDao;
import kimp.news.dao.NewsKeywordDao;
import kimp.news.dao.NewsSummaryDao;
import kimp.news.dto.internal.bloomingbit.BloomingBitNewsDto;
import kimp.news.entity.News;
import kimp.news.entity.NewsInsight;
import kimp.news.entity.NewsKeyword;
import kimp.news.entity.NewsSummary;
import kimp.news.enums.NewsSource;
import kimp.news.service.NewsSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BloomingBitNewsSourceServiceImpl implements NewsSourceService<BloomingBitNewsDto> {

    private final NewsKeywordDao newsKeywordDao;
    private final NewsSummaryDao newsSummaryDao;
    private final NewsInsightDao newsInsightDao;

    public BloomingBitNewsSourceServiceImpl(NewsKeywordDao newsKeywordDao,
                                            NewsSummaryDao newsSummaryDao,
                                            NewsInsightDao newsInsightDao) {
        this.newsKeywordDao = newsKeywordDao;
        this.newsSummaryDao = newsSummaryDao;
        this.newsInsightDao = newsInsightDao;
    }

    @Override
    @Transactional
    public News createNewsFromSource(BloomingBitNewsDto newsSourceDto) {
        News news = News.builder()
                .newsSource(NewsSource.BLOOMING_BIT)
                .sourceSequenceId(newsSourceDto.getSeq())
                .newsType(newsSourceDto.getNewsType())
                .region(newsSourceDto.getRegion())
                .title(newsSourceDto.getTitle())
                .plainTextContent(newsSourceDto.getPlainTextContent())
                .markdownContent(newsSourceDto.getMarkdownContent())
                .thumbnail(newsSourceDto.getThumbnail())
                .sentiment(newsSourceDto.getSentiment())
                .sourceUrl(newsSourceDto.getSourceUrl())
                .createEpochMillis(newsSourceDto.getCreateEpoch())
                .updateEpochMillis(newsSourceDto.getUpdateEpoch())
                .changeValue(newsSourceDto.getChange())
                .isNew(newsSourceDto.getIsNew())
                .isHeadline(newsSourceDto.getHeadline())
                .build();

        return news;
    }

    @Transactional
    public void saveNewsCollections(News savedNews, BloomingBitNewsDto newsSourceDto) {
        // 키워드 저장
        if (newsSourceDto.getKeywordList() != null) {
            List<NewsKeyword> keywords = newsSourceDto.getKeywordList().stream()
                    .map(keyword -> NewsKeyword.builder()
                            .news(savedNews)
                            .keyword(keyword)
                            .build())
                    .collect(Collectors.toList());
            newsKeywordDao.saveAll(keywords);
        }

        // 요약 저장
        if (newsSourceDto.getSummaryList() != null) {
            List<NewsSummary> summaries = new ArrayList<>();
            for (int i = 0; i < newsSourceDto.getSummaryList().size(); i++) {
                summaries.add(NewsSummary.builder()
                        .news(savedNews)
                        .summary(newsSourceDto.getSummaryList().get(i))
                        .displayOrder(i)
                        .build());
            }
            newsSummaryDao.saveAll(summaries);
        }

        // 관점 저장
        if (newsSourceDto.getInsightList() != null) {
            List<NewsInsight> insights = new ArrayList<>();
            for (int i = 0; i < newsSourceDto.getInsightList().size(); i++) {
                insights.add(NewsInsight.builder()
                        .news(savedNews)
                        .insight(newsSourceDto.getInsightList().get(i))
                        .displayOrder(i)
                        .build());
            }
            newsInsightDao.saveAll(insights);
        }
    }

    @Override
    @Transactional
    public News updateNewsFromSource(News existingNews, BloomingBitNewsDto newsSourceDto) {
        // 엔티티의 update 메서드를 사용하여 Dirty Checking으로 UPDATE (SELECT 없이)
        existingNews.updateFromBloomingBit(
            newsSourceDto.getNewsType(),
            newsSourceDto.getRegion(),
            newsSourceDto.getTitle(),
            newsSourceDto.getPlainTextContent(),
            newsSourceDto.getMarkdownContent(),
            newsSourceDto.getThumbnail(),
            newsSourceDto.getSentiment(),
            newsSourceDto.getSourceUrl(),
            newsSourceDto.getUpdateEpoch(),
            newsSourceDto.getChange(),
            newsSourceDto.getIsNew(),
            newsSourceDto.getHeadline()
        );

        return existingNews;
    }

    @Transactional
    public void updateNewsCollections(News savedNews, BloomingBitNewsDto newsSourceDto) {
        // 키워드 업데이트
        if (newsSourceDto.getKeywordList() != null) {
            newsKeywordDao.updateKeywords(savedNews.getId(), newsSourceDto.getKeywordList());
        }

        // 요약 업데이트
        if (newsSourceDto.getSummaryList() != null) {
            newsSummaryDao.updateSummaries(savedNews.getId(), newsSourceDto.getSummaryList());
        }

        // 관점 업데이트
        if (newsSourceDto.getInsightList() != null) {
            newsInsightDao.updateInsights(savedNews.getId(), newsSourceDto.getInsightList());
        }
    }

    @Override
    public List<News> createNewsListFromSource(List<BloomingBitNewsDto> newsSourceDtos) {
        return newsSourceDtos.stream()
                .map(this::createNewsFromSource)
                .collect(Collectors.toList());
    }

    @Override
    public String getNewsSourceName() {
        return "BloomingBit";
    }
}
