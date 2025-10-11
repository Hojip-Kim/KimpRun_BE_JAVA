package kimp.news.dao.impl;

import kimp.news.dao.NewsSummaryDao;
import kimp.news.entity.News;
import kimp.news.entity.NewsSummary;
import kimp.news.repository.NewsSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NewsSummaryDaoImpl implements NewsSummaryDao {

    private final NewsSummaryRepository newsSummaryRepository;

    @Override
    public List<NewsSummary> findByNewsIdOrderByDisplayOrder(Long newsId) {
        return newsSummaryRepository.findByNewsIdOrderByDisplayOrder(newsId);
    }

    @Override
    public List<NewsSummary> saveAll(List<NewsSummary> summaries) {
        return newsSummaryRepository.saveAll(summaries);
    }

    @Override
    @Transactional
    public void updateSummaries(Long newsId, List<String> newSummaries) {
        // 기존 요약 조회
        List<NewsSummary> existingSummaries = newsSummaryRepository.findByNewsIdOrderByDisplayOrder(newsId);

        // 기존 데이터 업데이트 또는 새 데이터 추가
        int existingSize = existingSummaries.size();
        int newSize = newSummaries.size();

        News news = existingSummaries.isEmpty() ? null : existingSummaries.get(0).getNews();

        // 기존 데이터 업데이트
        int updateCount = Math.min(existingSize, newSize);
        for (int i = 0; i < updateCount; i++) {
            NewsSummary existing = existingSummaries.get(i);
            if (!existing.getSummary().equals(newSummaries.get(i))) {
                NewsSummary updated = NewsSummary.builder()
                        .id(existing.getId())
                        .news(existing.getNews())
                        .summary(newSummaries.get(i))
                        .displayOrder(i)
                        .build();
                newsSummaryRepository.save(updated);
            }
        }

        // 새 데이터가 더 많으면 추가
        if (newSize > existingSize && news != null) {
            List<NewsSummary> toAdd = newSummaries.subList(existingSize, newSize).stream()
                    .map(summary -> NewsSummary.builder()
                            .news(news)
                            .summary(summary)
                            .displayOrder(existingSize + newSummaries.subList(existingSize, newSize).indexOf(summary))
                            .build())
                    .collect(Collectors.toList());
            newsSummaryRepository.saveAll(toAdd);
        }

        // 기존 데이터가 더 많으면 삭제
        if (existingSize > newSize) {
            List<NewsSummary> toDelete = existingSummaries.subList(newSize, existingSize);
            newsSummaryRepository.deleteAll(toDelete);
        }
    }
}
