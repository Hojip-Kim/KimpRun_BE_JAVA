package kimp.community.repository;

import kimp.community.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    public Optional<Category> findByCategoryName(String name);

    /**
     * Category를 ID로 조회하면서 BoardCount를 fetch join으로 가져옴 (N+1 방지)
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.boardCount WHERE c.id = :id")
    Optional<Category> findByIdWithBoardCount(@Param("id") Long id);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.boardCount")
    List<Category> findAllWithBoardCount();

}
