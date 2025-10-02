package kimp.community.dto.board.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class AllBoardResponseDto {
    @JsonProperty("boardResponseDtos")
    List<BoardResponseDto> boards;
    @JsonProperty("boardCount")
    Long boardCount;
}
