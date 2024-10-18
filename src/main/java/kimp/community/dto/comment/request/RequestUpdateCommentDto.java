package kimp.community.dto.comment.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RequestUpdateCommentDto {

    public long commentId;

    public String content;


    public RequestUpdateCommentDto(long commentId, String content) {
        this.commentId = commentId;

        if(!isContentValid(content)){
            throw new IllegalArgumentException("Invalid content");
        }
        this.content = content;
    }

    public boolean isContentValid(String content){
        if(content == null || content.isEmpty()){
            return false;
        }
        return true;
    }
}
