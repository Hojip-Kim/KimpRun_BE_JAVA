package kimp.community.dto.comment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    
    // 유저별 댓글 조회용 추가 필드들
    private Long boardId;
    private String boardTitle;
    private Long memberId; // comment 작성자 ID
    private Integer likes;

    // 삭제된 댓글용 정적 팩토리 메서드
    public static ResponseCommentDto createDeletedComment(long id, long parentCommentId, int depth, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return ResponseCommentDto.builder()
                .id(id)
                .parentCommentId(parentCommentId)
                .content(null)
                .depth(depth)
                .email(null)
                .nickName(null)
                .memberId(null)
                .likes(null)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
