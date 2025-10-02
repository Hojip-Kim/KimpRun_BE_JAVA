package kimp.user.service;

import kimp.user.dto.request.DeclarationMemberRequest;
import kimp.user.dto.response.DeclarationResponse;
import kimp.user.vo.AddDeclarationVo;
import kimp.user.vo.GetDeclarationsVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeclarationService {

    // 유저 신고 (이유 포함)
    public Boolean declaration(AddDeclarationVo vo);

    public Page<DeclarationResponse> getDeclarations(GetDeclarationsVo vo);


}
