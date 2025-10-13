package kimp.user.repository.user;

import kimp.user.entity.Declaration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DeclarationRepository extends JpaRepository<Declaration, Long> {

    public Page<Declaration> findAllByOrderByRegistedAtDesc(Pageable pageable);
    
    public long countByToMember(String toMember);
    
    @Query("SELECT d FROM Declaration d WHERE d.fromMember = :fromMember AND d.toMember = :toMember AND d.registedAt > :timeLimit ORDER BY d.registedAt DESC LIMIT 1")
    Optional<Declaration> findRecentDeclarationByFromAndToMember(@Param("fromMember") String fromMember, 
                                                               @Param("toMember") String toMember, 
                                                               @Param("timeLimit") LocalDateTime timeLimit);
}
