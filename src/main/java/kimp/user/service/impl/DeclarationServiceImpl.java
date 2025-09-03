package kimp.user.service.impl;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.DeclarationDao;
import kimp.user.dto.request.DeclarationMemberRequest;
import kimp.user.dto.response.DeclarationResponse;
import kimp.user.entity.Declaration;
import kimp.user.service.DeclarationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class DeclarationServiceImpl implements DeclarationService {

    private final DeclarationDao declarationDao;

    public DeclarationServiceImpl(DeclarationDao declarationDao) {
        this.declarationDao = declarationDao;
    }

    @Override
    public Boolean declaration(DeclarationMemberRequest request, String fromIp) {
        // 24시간 이내 중복 신고 확인
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        Optional<Declaration> recentDeclaration = declarationDao.findRecentDeclarationByFromAndToMember(
            request.getFromMember(), request.getToMember(), twentyFourHoursAgo);
        
        if (recentDeclaration.isPresent()) {
            log.warn("Duplicate declaration attempt within 24 hours - fromMember: {}, toMember: {}", 
                    request.getFromMember(), request.getToMember());
            throw new KimprunException(KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION, 
                "24시간 이내에 같은 사용자를 중복으로 신고할 수 없습니다.", 
                HttpStatus.BAD_REQUEST, "DeclarationServiceImpl.declaration");
        }

        Declaration declaration = new Declaration(request.getFromMember(), fromIp, request.getToMember(), request.getReason());

        Declaration createdDeclaration = declarationDao.createDeclaration(declaration);

        if(createdDeclaration == null) {
            return false;
        }

        log.info("Declaration created - fromMember: {}, toMember: {}", request.getFromMember(), request.getToMember());
        return true;
    }

    @Override
    public Page<DeclarationResponse> getDeclarations(Pageable pageable) {
        Page<Declaration> declarations = declarationDao.getDeclarationsOrderByRegistedAt(pageable);

        return declarations.map(DeclarationResponse::from);
    }
}
