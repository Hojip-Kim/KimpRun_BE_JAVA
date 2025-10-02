package kimp.community.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SoftDeleteCommentVo {
    private long memberId;
    private long commentId;
}
