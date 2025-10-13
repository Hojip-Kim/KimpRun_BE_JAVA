package kimp.news.repository;

import kimp.news.entity.NewsSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsSummaryRepository extends JpaRepository<NewsSummary, Long> {

    List<NewsSummary> findByNewsIdOrderByDisplayOrder(Long newsId);

    void deleteByNewsId(Long newsId);
}
