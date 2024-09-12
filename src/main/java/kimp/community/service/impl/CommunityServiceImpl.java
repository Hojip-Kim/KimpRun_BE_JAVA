package kimp.community.service.impl;

import kimp.community.entity.Board;
import kimp.community.service.BoardService;
import kimp.community.service.CategoryService;
import kimp.community.service.CommentService;
import kimp.community.service.CommunityService;
import org.springframework.stereotype.Service;

import java.util.List;

// pacade 패턴 적용
@Service
public class CommunityServiceImpl implements CommunityService {

    private final BoardService boardService;
    private final CommentService commentService;
    private final CategoryService categoryService;

    public CommunityServiceImpl(BoardService boardService, CommentService commentService, CategoryService categoryService){
        this.boardService = boardService;
        this.commentService = commentService;
        this.categoryService = categoryService;
    }

    @Override
    public List<Board> getCategoryBoard(Long categoryId, int page, int size) {
        return List.of();
    }
}
