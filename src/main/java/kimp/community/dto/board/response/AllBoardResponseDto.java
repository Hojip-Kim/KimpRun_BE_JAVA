package kimp.community.dto.board.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AllBoardResponseDto {
    List<BoardResponseDto> boards;
    Long boardCount;
}
