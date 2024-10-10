package kimp.community.dto.board.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BoardResponseDto {

    private Long boardId;
    private Long userId;
    private String userNickName;
    private String title;
    private String content;
    private Integer boardViewsCount;
    private Integer boardLikesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
