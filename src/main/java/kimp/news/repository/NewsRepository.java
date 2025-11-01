package kimp.news.repository;

import kimp.news.entity.News;
import kimp.news.enums.NewsSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, NewsRepositoryCustom {

    Optional<News> findByNewsSourceAndSourceSequenceId(NewsSource newsSource, Long sourceSequenceId);

    boolean existsByNewsSourceAndSourceSequenceId(NewsSource newsSource, Long sourceSequenceId);
}
