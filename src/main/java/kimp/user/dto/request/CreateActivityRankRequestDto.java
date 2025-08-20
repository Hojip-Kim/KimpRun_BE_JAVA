package kimp.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreateActivityRankRequestDto {
    // 새싹, 일반회원, 우수회원, 마스터, 운영자
    private String grade;

    public CreateActivityRankRequestDto(String grade) {
        this.grade = grade;
    }
}
