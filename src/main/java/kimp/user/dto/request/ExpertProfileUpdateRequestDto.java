package kimp.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertProfileUpdateRequestDto {

    @Size(max = 200, message = "전문 분야는 200자를 초과할 수 없습니다")
    private String expertiseField;

    private String bio;

    @Size(max = 500, message = "포트폴리오 URL은 500자를 초과할 수 없습니다")
    private String portfolioUrl;
}
