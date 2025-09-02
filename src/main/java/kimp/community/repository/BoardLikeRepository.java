package kimp.community.repository;

import kimp.community.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long>, BoardLikeRepositoryCustom {
}