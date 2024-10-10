package kimp.community.repository;

import kimp.community.entity.BoardLikeCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardLikeCountRepository extends JpaRepository<BoardLikeCount, Long> {

}
