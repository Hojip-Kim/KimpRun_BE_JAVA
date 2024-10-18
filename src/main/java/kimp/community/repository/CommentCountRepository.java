package kimp.community.repository;

import kimp.community.entity.Board;
import kimp.community.entity.CommentCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentCountRepository extends JpaRepository<CommentCount, Long> {

    public CommentCount findByBoard(Board board);
}
