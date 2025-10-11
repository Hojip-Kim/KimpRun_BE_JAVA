package kimp.news.dao.impl;

import kimp.news.dao.NewsInsightDao;
import kimp.news.entity.News;
import kimp.news.entity.NewsInsight;
import kimp.news.repository.NewsInsightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NewsInsightDaoImpl implements NewsInsightDao {

    private final NewsInsightRepository newsInsightRepository;

    @Override
    public List<NewsInsight> findByNewsIdOrderByDisplayOrder(Long newsId) {
        return newsInsightRepository.findByNewsIdOrderByDisplayOrder(newsId);
    }

    @Override
    public List<NewsInsight> saveAll(List<NewsInsight> insights) {
        return newsInsightRepository.saveAll(insights);
    }

    @Override
    @Transactional
    public void updateInsights(Long newsId, List<String> newInsights) {
        // 기존 인사이트 조회
        List<NewsInsight> existingInsights = newsInsightRepository.findByNewsIdOrderByDisplayOrder(newsId);

        // 기존 데이터 업데이트 또는 새 데이터 추가
        int existingSize = existingInsights.size();
        int newSize = newInsights.size();

        News news = existingInsights.isEmpty() ? null : existingInsights.get(0).getNews();

        // 기존 데이터 업데이트
        int updateCount = Math.min(existingSize, newSize);
        for (int i = 0; i < updateCount; i++) {
            NewsInsight existing = existingInsights.get(i);
            if (!existing.getInsight().equals(newInsights.get(i))) {
                // 새로 빌드해서 업데이트
                NewsInsight updated = NewsInsight.builder()
                        .id(existing.getId())
                        .news(existing.getNews())
                        .insight(newInsights.get(i))
                        .displayOrder(i)
                        .build();
                newsInsightRepository.save(updated);
            }
        }

        // 새 데이터가 더 많으면 추가
        if (newSize > existingSize && news != null) {
            List<NewsInsight> toAdd = newInsights.subList(existingSize, newSize).stream()
                    .map(insight -> NewsInsight.builder()
                            .news(news)
                            .insight(insight)
                            .displayOrder(existingSize + newInsights.subList(existingSize, newSize).indexOf(insight))
                            .build())
                    .collect(Collectors.toList());
            newsInsightRepository.saveAll(toAdd);
        }

        // 기존 데이터가 더 많으면 삭제
        if (existingSize > newSize) {
            List<NewsInsight> toDelete = existingInsights.subList(newSize, existingSize);
            newsInsightRepository.deleteAll(toDelete);
        }
    }
}
