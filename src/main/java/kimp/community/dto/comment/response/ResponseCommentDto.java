package kimp.community.dto.comment.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ResponseCommentDto {
    private long id;
    private long parentCommentId;
    private String content;
    private int depth;
    private String userLoginId;
    private String nickName;

    public ResponseCommentDto(long id, long parentCommentId, String content, int depth, String userLoginId, String nickName) {
        this.id = id;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.depth = depth;
        this.userLoginId = userLoginId;
        this.nickName = nickName;
    }
}
