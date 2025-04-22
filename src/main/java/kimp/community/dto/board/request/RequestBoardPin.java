package kimp.community.dto.board.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RequestBoardPin {

    @NotEmpty
    private List<Long> boardIds;

}
