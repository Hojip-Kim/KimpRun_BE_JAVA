package kimp.community.dao.impl;

import kimp.community.dao.BoardCountDao;
import kimp.community.entity.BoardCount;
import kimp.community.entity.Category;
import kimp.community.repository.BoardCountRepository;
import org.springframework.stereotype.Repository;

@Repository
public class BoardCountDaoImpl implements BoardCountDao {

    private final BoardCountRepository boardCountRepository;

    public BoardCountDaoImpl(BoardCountRepository boardCountRepository) {
        this.boardCountRepository = boardCountRepository;
    }


    @Override
    public BoardCount createBoardCount(Category category) {

        BoardCount boardCount = boardCountRepository.findBoardCountByCategory(category);

        if(boardCount != null){
            return boardCount;
        }

        BoardCount newBoardCount = new BoardCount(category);

        return boardCountRepository.save(newBoardCount);
    }
}
