package kimp.news.dao;

import kimp.news.entity.NewsSummary;

import java.util.List;

public interface NewsSummaryDao {

    List<NewsSummary> findByNewsIdOrderByDisplayOrder(Long newsId);

    List<NewsSummary> saveAll(List<NewsSummary> summaries);

    void updateSummaries(Long newsId, List<String> newSummaries);
}
