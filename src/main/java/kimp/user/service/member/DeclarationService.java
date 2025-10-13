package kimp.user.service.member;

import kimp.user.dto.response.DeclarationResponse;
import kimp.user.vo.AddDeclarationVo;
import kimp.user.vo.GetDeclarationsVo;
import org.springframework.data.domain.Page;

public interface DeclarationService {

    // 유저 신고 (이유 포함)
    public Boolean declaration(AddDeclarationVo vo);

    public Page<DeclarationResponse> getDeclarations(GetDeclarationsVo vo);


}
