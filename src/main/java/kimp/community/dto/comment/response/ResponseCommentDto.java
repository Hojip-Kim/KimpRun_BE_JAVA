package kimp.community.dto.comment.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ResponseCommentDto {
    private long id;
    private long parentCommentId;
    private String content;
    private int depth;
    private String email;
    private String nickName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ResponseCommentDto(long id, long parentCommentId, String content, int depth, String email, String nickName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.depth = depth;
        this.email = email;
        this.nickName = nickName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
