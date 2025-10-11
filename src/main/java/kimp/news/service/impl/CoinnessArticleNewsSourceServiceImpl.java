package kimp.news.service.impl;

import kimp.news.dao.NewsSummaryDao;
import kimp.news.dto.internal.coinness.CoinnessArticleDto;
import kimp.news.entity.News;
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
@Service("coinnessArticleNewsSourceService")
public class CoinnessArticleNewsSourceServiceImpl implements NewsSourceService<CoinnessArticleDto> {

    private final NewsSummaryDao newsSummaryDao;

    public CoinnessArticleNewsSourceServiceImpl(NewsSummaryDao newsSummaryDao) {
        this.newsSummaryDao = newsSummaryDao;
    }

    @Override
    @Transactional
    public News createNewsFromSource(CoinnessArticleDto newsSourceDto) {
        News news = News.builder()
                .newsSource(NewsSource.COINNESS)
                .sourceSequenceId(newsSourceDto.getId())
                .newsType("article")
                .region("KR")
                .title(newsSourceDto.getTitle())
                .plainTextContent(newsSourceDto.getDescription())
                .markdownContent(newsSourceDto.getDescription())
                .thumbnail(newsSourceDto.getThumbnailImage())
                .sentiment("NEUTRAL")
                .sourceUrl(newsSourceDto.getSourceUrl())
                .createEpochMillis(newsSourceDto.getCreateEpochMillis())
                .updateEpochMillis(newsSourceDto.getCreateEpochMillis())
                .changeValue(null)
                .isNew(true)
                .isHeadline(false)
                .build();

        return news;
    }

    @Transactional
    public void saveNewsCollections(News savedNews, CoinnessArticleDto newsSourceDto) {
        if (newsSourceDto.getDescription() != null && !newsSourceDto.getDescription().isEmpty()) {
            NewsSummary summary = NewsSummary.builder()
                    .news(savedNews)
                    .summary(newsSourceDto.getDescription())
                    .displayOrder(0)
                    .build();
            List<NewsSummary> summaries = new ArrayList<>();
            summaries.add(summary);
            newsSummaryDao.saveAll(summaries);
        }
    }

    @Override
    @Transactional
    public News updateNewsFromSource(News existingNews, CoinnessArticleDto newsSourceDto) {
        News updatedNews = News.builder()
                .id(existingNews.getId())
                .newsSource(NewsSource.COINNESS)
                .sourceSequenceId(newsSourceDto.getId())
                .newsType("article")
                .region("KR")
                .title(newsSourceDto.getTitle())
                .plainTextContent(newsSourceDto.getDescription())
                .markdownContent(newsSourceDto.getDescription())
                .thumbnail(newsSourceDto.getThumbnailImage())
                .sentiment("NEUTRAL")
                .sourceUrl(newsSourceDto.getSourceUrl())
                .createEpochMillis(newsSourceDto.getCreateEpochMillis())
                .updateEpochMillis(newsSourceDto.getCreateEpochMillis())
                .changeValue(null)
                .isNew(true)
                .isHeadline(false)
                .createdAt(existingNews.getCreatedAt())
                .build();

        return updatedNews;
    }

    @Transactional
    public void updateNewsCollections(News savedNews, CoinnessArticleDto newsSourceDto) {
        if (newsSourceDto.getDescription() != null && !newsSourceDto.getDescription().isEmpty()) {
            List<String> summaryList = new ArrayList<>();
            summaryList.add(newsSourceDto.getDescription());
            newsSummaryDao.updateSummaries(savedNews.getId(), summaryList);
        }
    }

    @Override
    public List<News> createNewsListFromSource(List<CoinnessArticleDto> newsSourceDtos) {
        return newsSourceDtos.stream()
                .map(this::createNewsFromSource)
                .collect(Collectors.toList());
    }

    @Override
    public String getNewsSourceName() {
        return "Coinness-Article";
    }
}
