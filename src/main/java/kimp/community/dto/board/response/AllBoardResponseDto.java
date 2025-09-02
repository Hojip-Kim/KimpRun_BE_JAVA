package kimp.community.dto.board.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AllBoardResponseDto {
    @JsonProperty("boardResponseDtos")
    List<BoardResponseDto> boards;
    @JsonProperty("boardCount")
    Long boardCount;
}
