package kimp.community.dto.board.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public class BoardResponseDto {

    private Long boardId;
    private Long memberId;
    private Long categoryId;
    private String categoryName;
    private String memberNickName;
    private String title;
    private String content;
    private Integer boardViewsCount;
    private Integer boardLikesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer commentsCount;
    private Boolean isPin;

}
