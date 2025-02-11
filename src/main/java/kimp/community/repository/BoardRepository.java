package kimp.community.repository;

import kimp.community.entity.Board;
import kimp.community.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {


    public Page<Board> findByCategoryOrderByRegistedAtDesc(Category category, Pageable pageable);
    public Page<Board> findAllByOrderByRegistedAtDesc(Pageable pageable);

}
