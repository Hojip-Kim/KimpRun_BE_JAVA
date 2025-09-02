package kimp.community.dto.board.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardWithCountResponseDto {
    List<BoardResponseDto> boardResponseDtos;
    Long count;
}
