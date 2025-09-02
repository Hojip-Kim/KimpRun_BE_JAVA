package kimp.user.dao.impl;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.DeclarationDao;
import kimp.user.entity.Declaration;
import kimp.user.repository.DeclarationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DeclarationDaoImpl implements DeclarationDao {
    private final DeclarationRepository declarationRepository;

    public DeclarationDaoImpl(DeclarationRepository declarationRepository) {
        this.declarationRepository = declarationRepository;
    }


    @Override
    public Declaration createDeclaration(Declaration declaration) {

        return declarationRepository.save(declaration);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Declaration> getDeclarationsOrderByRegistedAt(Pageable pageable) {
        Page<Declaration> declarations = declarationRepository.findAllByOrderByRegistedAtDesc(pageable);
        if(declarations.getTotalElements() == 0) {
            throw new KimprunException(KimprunExceptionEnum.REQUEST_ACCEPTED, "not have declaration", HttpStatus.ACCEPTED, "DeclarationDaoImpl.getDeclarationsOrderByRegistedAt");
        }

        return declarations;
    }

    @Override
    @Transactional(readOnly = true)
    public long getDeclarationCountByToMember(String toMember) {
        return declarationRepository.countByToMember(toMember);
    }
}
