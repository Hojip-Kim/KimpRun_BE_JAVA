package kimp.community.service.impl;

import kimp.community.dao.CommentCountDao;
import kimp.community.dao.CommentDao;
import kimp.community.dao.CommentLikeCountDao;
import kimp.community.dto.comment.request.RequestCreateCommentDto;
import kimp.community.dto.comment.request.RequestUpdateCommentDto;
import kimp.community.dto.comment.response.ResponseCommentDto;
import kimp.community.entity.Board;
import kimp.community.entity.Comment;
import kimp.community.entity.CommentCount;
import kimp.community.entity.CommentLikeCount;
import kimp.community.service.CommentService;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final CommentLikeCountDao commentLikeCountDao;
    private final CommentCountDao commentCountDao;

    public CommentServiceImpl(CommentDao commentDao, CommentLikeCountDao commentLikeCountDao, CommentCountDao commentCountDao) {
        this.commentDao = commentDao;
        this.commentLikeCountDao = commentLikeCountDao;
        this.commentCountDao = commentCountDao;
    }

    @Override
    public Page<Comment> getCommentByBoard(Board board, int page) {

        PageRequest pageRequest = PageRequest.of(page, 30);



        Page<Comment> comments = commentDao.getComments(board, pageRequest);


        return comments;
    }
    
    @Override
    public Page<Comment> getCommentByBoardWithDeleted(Board board, int page) {
        PageRequest pageRequest = PageRequest.of(page, 30);
        Page<Comment> comments = commentDao.getCommentsWithBoard(board, pageRequest);
        return comments;
    }

    @Override
    public Comment getCommentById(long commentId) {
        return commentDao.getComment(commentId);
    }

    @Override
    public Comment createComment(Member member, Board board, RequestCreateCommentDto createCommentDto) {
        if(member == null){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Member cannot be null", HttpStatus.BAD_REQUEST, "CommentServiceImpl.createComment");
        }
        if(board == null){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Board cannot be null", HttpStatus.BAD_REQUEST, "CommentServiceImpl.createComment");
        }

        return commentDao.createComment(member, board, createCommentDto.getContent(), createCommentDto.getParentCommentId(), createCommentDto.getDepth());
    }

    @Override
    public CommentCount createCommentCount(Board board){
        return commentCountDao.createCommentCount(board);
    }

    @Override
    @Transactional
    public Comment updateComment(long memberId, RequestUpdateCommentDto updateCommentDto) {

        Comment comment = commentDao.getComment(updateCommentDto.getCommentId());
        if(!comment.member.getId().equals(memberId)){
            throw new KimprunException(KimprunExceptionEnum.AUTHENTICATION_REQUIRED_EXCEPTION, "User not authorized to update this comment", HttpStatus.UNAUTHORIZED, "CommentServiceImpl.updateComment");
        }

        return comment.updateCommentContent(updateCommentDto.getContent());
    }

    @Override
    public Boolean deleteComment(long memberId, long commentId) {
        Comment comment = commentDao.getComment(commentId);
        if(!comment.member.getId().equals(memberId)){
            throw new KimprunException(KimprunExceptionEnum.AUTHENTICATION_REQUIRED_EXCEPTION, "User not authorized to delete this comment", HttpStatus.UNAUTHORIZED, "CommentServiceImpl.deleteComment");
        }

        return commentDao.deleteComment(commentId);
    }

    @Override
    public CommentLikeCount createCommentLikeCount(Comment comment) {

        return commentLikeCountDao.createCommentLikeCount(comment);
    }

    @Override
    public ResponseCommentDto convertCommentToResponseDto(Comment comment) {
        if (comment.isDeleted()) {
            // 삭제된 댓글의 경우 민감한 정보를 null로 처리
            return ResponseCommentDto.createDeletedComment(
                    comment.getId(),
                    comment.getParentCommentId(),
                    comment.getDepth(),
                    comment.getRegistedAt(),
                    comment.getUpdatedAt()
            );
        } else {
            // 일반 댓글의 경우 모든 정보 포함
            long commentId = comment.getId();
            long parentCommentId = comment.getParentCommentId();
            String content = comment.getContent();
            int depth = comment.getDepth();
            String memberEmail = comment.getMember().getEmail();
            String memberNickName = comment.getMember().getNickname();
            Long memberId = comment.getMember().getId();
            LocalDateTime createdAt = comment.getRegistedAt();
            LocalDateTime updatedAt = comment.getUpdatedAt();
            return ResponseCommentDto.builder()
                    .id(commentId)
                    .parentCommentId(parentCommentId)
                    .content(content)
                    .depth(depth)
                    .email(memberEmail)
                    .nickName(memberNickName)
                    .memberId(memberId)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        }
    }

    @Override
    public List<ResponseCommentDto> converCommentsToResponseDtoList(List<Comment> comments){
        return comments.stream().map(comment->convertCommentToResponseDto(comment)).collect(Collectors.toList());
    }

    @Override
    public Page<Comment> getCommentsByMember(Member member, Pageable pageable) {
        return commentDao.getCommentsByMember(member, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByMemberIdWithAllFetch(Long memberId, Pageable pageable) {
        return commentDao.getCommentsByMemberIdWithAllFetch(memberId, pageable);
    }

    @Override
    @Transactional
    public void softDeleteComment(long memberId, long commentId) {
        Comment comment = getCommentById(commentId);
        if (!comment.getMember().getId().equals(memberId)) {
            throw new KimprunException(KimprunExceptionEnum.AUTHENTICATION_REQUIRED_EXCEPTION, 
                "User not authorized to delete this comment: " + commentId, HttpStatus.UNAUTHORIZED, "CommentServiceImpl.softDeleteComment");
        }
        
        comment.softDelete();
        commentDao.saveComment(comment);
    }

    @Override
    public ResponseCommentDto updateCommentDto(long memberId, RequestUpdateCommentDto updateCommentDto) {
        Comment comment = updateComment(memberId, updateCommentDto);
        return convertCommentToResponseDto(comment);
    }

}
