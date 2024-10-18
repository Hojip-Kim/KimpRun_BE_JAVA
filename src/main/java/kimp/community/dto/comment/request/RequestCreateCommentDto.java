package kimp.community.dto.comment.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RequestCreateCommentDto {

    private String content;
    private int depth;
    private long parentCommentId;

    public RequestCreateCommentDto(String content, int depth, long parentCommentId) {
        if(content.isEmpty() || content.isBlank()) throw new IllegalArgumentException("Content cannot be empty");
        this.content = content;

        if(depth < 0) throw new IllegalArgumentException("Depth cannot be negative");
        this.depth = depth;

        if(parentCommentId < 0) throw new IllegalArgumentException("ParentCommentId cannot be negative");
        this.parentCommentId = parentCommentId;
    }
}
