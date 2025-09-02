package kimp.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Getter
public class DeclarationMemberRequest {

    // 로그인 한 유저가 아니면 String값, 로그인 한 유저면 Long값인데 String으로 매핑
    @JsonProperty("fromMember")
    private String fromMember;
    // 로그인 한 유저가 아니면 String값, 로그인 한 유저면 Long값인데 String으로 매핑
    @JsonProperty("toMember")
    private String toMember;

    @JsonProperty("reason")
    private String reason;

    public DeclarationMemberRequest(Object fromMember, Object toMember, String reason) {
        this.fromMember = String.valueOf(fromMember);
        this.toMember = String.valueOf(toMember);
        reasonSizeValidCheck(reason);
        this.reason = reason;
    }

    private void reasonSizeValidCheck(String reason) {
        if(reason.length() > 150) {
            throw new KimprunException(KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION, "can't exceed reasonSize more than 150", HttpStatus.BAD_REQUEST, "DeclarationMemberRequestDto");
        }
    }
}
