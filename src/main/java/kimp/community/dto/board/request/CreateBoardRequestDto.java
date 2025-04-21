package kimp.community.dto.board.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateBoardRequestDto {
    private String title;
    private String content;
    private String previewImage;
}
