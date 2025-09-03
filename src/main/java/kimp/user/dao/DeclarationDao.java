package kimp.user.dao;

import kimp.user.entity.Declaration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DeclarationDao {

    public Declaration createDeclaration(Declaration declaration);

    public Page<Declaration> getDeclarationsOrderByRegistedAt(Pageable pageable);
    
    public long getDeclarationCountByToMember(String toMember);
    
    public Optional<Declaration> findRecentDeclarationByFromAndToMember(String fromMember, String toMember, LocalDateTime timeLimit);
}
