package kimp.community.dto.board.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BoardInsertDto {
    private Long memberId;
    private Long categoryId;
    private String title;
    private String content;
}
