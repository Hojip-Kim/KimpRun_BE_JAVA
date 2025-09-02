package kimp.user.service;

import kimp.user.dto.request.DeclarationMemberRequest;
import kimp.user.dto.response.DeclarationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeclarationService {

    // 유저 신고 (이유 포함)
    public Boolean declaration(DeclarationMemberRequest request, String fromIp);

    public Page<DeclarationResponse> getDeclarations(Pageable pageable);


}
