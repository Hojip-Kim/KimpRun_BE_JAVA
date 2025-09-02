package kimp.user.repository;

import kimp.user.entity.Declaration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeclarationRepository extends JpaRepository<Declaration, Long> {

    public Page<Declaration> findAllByOrderByRegistedAtDesc(Pageable pageable);
    
    public long countByToMember(String toMember);
}
