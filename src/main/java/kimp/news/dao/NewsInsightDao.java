package kimp.news.dao;

import kimp.news.entity.NewsInsight;

import java.util.List;

public interface NewsInsightDao {

    List<NewsInsight> findByNewsIdOrderByDisplayOrder(Long newsId);

    List<NewsInsight> saveAll(List<NewsInsight> insights);

    void updateInsights(Long newsId, List<String> newInsights);
}
