package kimp.news.repository;

import kimp.news.entity.NewsKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsKeywordRepository extends JpaRepository<NewsKeyword, Long> {

    List<NewsKeyword> findByNewsId(Long newsId);

    void deleteByNewsId(Long newsId);
}
