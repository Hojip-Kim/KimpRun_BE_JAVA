package kimp.user.service.impl;

import kimp.user.dao.DeclarationDao;
import kimp.user.dto.request.DeclarationMemberRequest;
import kimp.user.dto.response.DeclarationResponse;
import kimp.user.entity.Declaration;
import kimp.user.service.DeclarationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DeclarationServiceImpl implements DeclarationService {

    private final DeclarationDao declarationDao;

    public DeclarationServiceImpl(DeclarationDao declarationDao) {
        this.declarationDao = declarationDao;
    }

    @Override
    public Boolean declaration(DeclarationMemberRequest request, String fromIp) {
        Declaration declaration = new Declaration(request.getFromMember(), fromIp, request.getToMember(), request.getReason());

        Declaration createdDeclaration = declarationDao.createDeclaration(declaration);

        if(createdDeclaration == null) {
            return false;
        }

        return true;
    }

    @Override
    public Page<DeclarationResponse> getDeclarations(Pageable pageable) {
        Page<Declaration> declarations = declarationDao.getDeclarationsOrderByRegistedAt(pageable);

        return declarations.map(DeclarationResponse::from);
    }
}
