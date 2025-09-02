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
    
    // 유저별 댓글 조회용 추가 필드들
    private Long boardId;
    private String boardTitle;
    private Long memberId; // comment 작성자 ID
    private Integer likes;

    // 기존 생성자 (게시글 내 댓글 목록용)
    public ResponseCommentDto(long id, long parentCommentId, String content, int depth, String email, String nickName, Long memberId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.depth = depth;
        this.email = email;
        this.nickName = nickName;
        this.memberId = memberId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // 유저별 댓글 조회용 생성자
    public ResponseCommentDto(Long id, Long boardId, String boardTitle, Long memberId, String nickName, String content, Integer likes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.boardId = boardId;
        this.boardTitle = boardTitle;
        this.memberId = memberId;
        this.nickName = nickName;
        this.content = content;
        this.likes = likes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
