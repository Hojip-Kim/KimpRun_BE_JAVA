package kimp.community.service;

import kimp.community.entity.Board;

import java.util.List;

public interface BoardService {

    public List<Board> getBoardsByPage(int page, int size);

    public Board getBoardById(int id);

    public Integer getBoardsCount();

}
