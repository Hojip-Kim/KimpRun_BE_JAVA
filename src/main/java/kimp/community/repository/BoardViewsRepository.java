package kimp.community.repository;

import kimp.community.entity.BoardViews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardViewsRepository extends JpaRepository<BoardViews, Long> {
}
