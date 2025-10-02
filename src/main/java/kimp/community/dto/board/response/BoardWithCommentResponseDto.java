package kimp.community.dto.board.response;

import kimp.community.dto.comment.response.ResponseCommentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@NoArgsConstructor
@SuperBuilder
public class BoardWithCommentResponseDto extends BoardResponseDto{

    List<ResponseCommentDto> comments;
}
