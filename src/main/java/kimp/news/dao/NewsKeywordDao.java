package kimp.news.dao;

import kimp.news.entity.NewsKeyword;

import java.util.List;

public interface NewsKeywordDao {

    List<NewsKeyword> findByNewsId(Long newsId);

    List<NewsKeyword> saveAll(List<NewsKeyword> keywords);

    void updateKeywords(Long newsId, List<String> newKeywords);
}
