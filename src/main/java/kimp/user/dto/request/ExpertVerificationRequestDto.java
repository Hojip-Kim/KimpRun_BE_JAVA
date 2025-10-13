package kimp.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertVerificationRequestDto {

    @NotBlank(message = "전문 분야는 필수입니다")
    @Size(max = 200, message = "전문 분야는 200자를 초과할 수 없습니다")
    private String expertiseField;

    @NotBlank(message = "설명은 필수입니다")
    private String description;

    private String credentials;

    @Size(max = 500, message = "포트폴리오 URL은 500자를 초과할 수 없습니다")
    private String portfolioUrl;
}
