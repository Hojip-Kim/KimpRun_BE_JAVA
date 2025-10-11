package kimp.community.factory;

import kimp.community.service.BoardService;
import kimp.community.service.CategoryService;
import kimp.community.service.CommentService;
import kimp.user.service.member.MemberService;
import org.springframework.stereotype.Component;

@Component
public class CommunityServiceFactory {

    private final BoardService boardService;
    private final CommentService commentService;
    private final CategoryService categoryService;
    private final MemberService memberService;

    public CommunityServiceFactory(BoardService boardService, CommentService commentService, CategoryService categoryService, MemberService memberService) {
        this.boardService = boardService;
        this.commentService = commentService;
        this.categoryService = categoryService;
        this.memberService = memberService;
    }

    public BoardService getBoardService() {
        return boardService;
    }

    public CommentService getCommentService() {
        return commentService;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public MemberService getMemberService() {
        return memberService;
    }
}
