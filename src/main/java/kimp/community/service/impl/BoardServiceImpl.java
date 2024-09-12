package kimp.community.service.impl;

import kimp.community.entity.Board;
import kimp.community.service.BoardService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {
    @Override
    public List<Board> getBoardsByPage(int page, int size) {
        return List.of();
    }

    @Override
    public Board getBoardById(int id) {
        return null;
    }

    @Override
    public Integer getBoardsCount() {
        return 0;
    }
}
