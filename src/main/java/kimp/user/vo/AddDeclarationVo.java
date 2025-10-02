package kimp.user.vo;

import kimp.user.dto.request.DeclarationMemberRequest;

public class AddDeclarationVo {

    private final DeclarationMemberRequest declarationMemberRequest;
    private final String clientIp;

    public AddDeclarationVo(DeclarationMemberRequest declarationMemberRequest, String clientIp) {
        this.declarationMemberRequest = declarationMemberRequest;
        this.clientIp = clientIp;
    }

    public DeclarationMemberRequest getDeclarationMemberRequest() {
        return declarationMemberRequest;
    }

    public String getClientIp() {
        return clientIp;
    }
}
