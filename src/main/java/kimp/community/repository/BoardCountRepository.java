package kimp.community.repository;

import kimp.community.entity.BoardCount;
import kimp.community.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCountRepository extends JpaRepository<BoardCount, Long> {

    public BoardCount findBoardCountByCategory(Category category);

}
