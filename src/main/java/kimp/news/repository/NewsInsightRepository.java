package kimp.news.repository;

import kimp.news.entity.NewsInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsInsightRepository extends JpaRepository<NewsInsight, Long> {

    List<NewsInsight> findByNewsIdOrderByDisplayOrder(Long newsId);

    void deleteByNewsId(Long newsId);
}
