package kimp.news.dao.impl;

import kimp.news.dao.NewsKeywordDao;
import kimp.news.entity.News;
import kimp.news.entity.NewsKeyword;
import kimp.news.repository.NewsKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NewsKeywordDaoImpl implements NewsKeywordDao {

    private final NewsKeywordRepository newsKeywordRepository;

    @Override
    public List<NewsKeyword> findByNewsId(Long newsId) {
        return newsKeywordRepository.findByNewsId(newsId);
    }

    @Override
    public List<NewsKeyword> saveAll(List<NewsKeyword> keywords) {
        return newsKeywordRepository.saveAll(keywords);
    }

    @Override
    @Transactional
    public void updateKeywords(Long newsId, List<String> newKeywords) {
        // 기존 키워드 조회
        List<NewsKeyword> existingKeywords = newsKeywordRepository.findByNewsId(newsId);

        List<String> existingKeywordTexts = existingKeywords.stream()
                .map(NewsKeyword::getKeyword)
                .collect(Collectors.toList());

        // 삭제할 키워드: 기존에 있었지만 새 목록에 없는 것
        List<NewsKeyword> toDelete = existingKeywords.stream()
                .filter(keyword -> !newKeywords.contains(keyword.getKeyword()))
                .collect(Collectors.toList());

        // 추가할 키워드: 새 목록에 있지만 기존에 없는 것
        List<String> toAdd = newKeywords.stream()
                .filter(keyword -> !existingKeywordTexts.contains(keyword))
                .collect(Collectors.toList());

        // 삭제 실행
        if (!toDelete.isEmpty()) {
            newsKeywordRepository.deleteAll(toDelete);
        }

        // 추가 실행
        if (!toAdd.isEmpty()) {
            News news = existingKeywords.isEmpty() ? null : existingKeywords.get(0).getNews();
            if (news != null) {
                List<NewsKeyword> newKeywordEntities = toAdd.stream()
                        .map(keyword -> NewsKeyword.builder()
                                .news(news)
                                .keyword(keyword)
                                .build())
                        .collect(Collectors.toList());
                newsKeywordRepository.saveAll(newKeywordEntities);
            }
        }
    }
}
