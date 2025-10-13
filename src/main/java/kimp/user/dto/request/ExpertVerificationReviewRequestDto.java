package kimp.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertVerificationReviewRequestDto {

    @NotNull(message = "승인 여부는 필수입니다")
    private Boolean approved;

    private String rejectionReason;
}
