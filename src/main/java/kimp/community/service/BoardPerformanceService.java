package kimp.community.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kimp.community.dto.board.request.BoardInsertDto;
import kimp.community.entity.Board;
import kimp.community.entity.Category;
import kimp.community.repository.BoardRepository;
import kimp.user.entity.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Qualifier("performance")
public class BoardPerformanceService {

    private final ConcurrentLinkedQueue<BoardInsertDto> boardQueue = new ConcurrentLinkedQueue<>();

    private static final int BATCH_SIZE = 100;

    private final BoardRepository boardRepo;
    private final BoardPacadeService boardPacadeService;

    @PersistenceContext
    private EntityManager entityManager;

    public BoardPerformanceService(BoardRepository boardRepo, BoardPacadeService boardPacadeService) {
        this.boardRepo = boardRepo;
        this.boardPacadeService = boardPacadeService;
    }

//    @Scheduled(fixedRate = 100)
    @Transactional
    public void flushQueue(){

        List<Board> batch = new ArrayList<>();
        BoardInsertDto request;

        while ((request = boardQueue.poll()) != null) {
            Category categoryProxy = entityManager.getReference(Category.class, request.getCategoryId());
            Member memberProxy = entityManager.getReference(Member.class, request.getMemberId());

            Board board = new Board(request.getTitle(), request.getContent());
            board.setCategory(categoryProxy);
            board.setMember(memberProxy);

            batch.add(board);

            if (batch.size() >= BATCH_SIZE) {
                persistBatch(batch);
                batch.clear();
            }
        }
        // 남은 요청 처리
        if (!batch.isEmpty()) {
            persistBatch(batch);
        }
    }


    public void enqueueBoardQueue(BoardInsertDto boardInsertDto) {
        this.boardQueue.add(boardInsertDto);
    }

    public void persistBatch(List<Board> batch) {
        for (Board b : batch) {
            entityManager.persist(b);
        }
        entityManager.flush();
        entityManager.clear();
    }


}
