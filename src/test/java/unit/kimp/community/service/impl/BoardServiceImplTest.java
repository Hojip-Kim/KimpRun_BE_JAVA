package unit.kimp.community.service.impl;

import kimp.community.dao.BoardDao;
import kimp.community.dao.BoardLikeCountDao;
import kimp.community.dao.BoardViewDao;
import kimp.community.dto.board.request.CreateBoardRequestDto;
import kimp.community.service.BoardService;
import kimp.community.service.impl.BoardServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@SpringBootTest(classes = {BoardServiceImpl.class})
public class BoardServiceImplTest {

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private BoardDao boardDao;

    @MockBean
    private BoardViewDao boardViewDao;

    @MockBean
    private BoardLikeCountDao boardLikeCountDao;

    @Autowired
    private BoardService boardService;

    @MockBean
    private PlatformTransactionManager transactionManager;

    // 배치 단위(예: 10,000건씩 커밋)
    private static final int BATCH_SIZE = 10_000;
    // 총 데이터 건수
    private static final int TOTAL_RECORDS = 100_000;

    @Test
    public void testInsert100MillionRecords() {
        int count = 0;
        TransactionStatus txStatus = null;

        try {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setName("BatchInsertTx");
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

            txStatus = transactionManager.getTransaction(def);

            for (int i = 0; i < TOTAL_RECORDS; i++) {
                CreateBoardRequestDto dto = new CreateBoardRequestDto("Title" + i, "Content " + i, "Preview Image" + i);

                boardService.createBoard(dto);

                count++;

                if (count % BATCH_SIZE == 0) {
                    transactionManager.commit(txStatus);
                    System.out.println("Inserted " + count + " records so far.");

                    txStatus = transactionManager.getTransaction(def);
                }
            }
            transactionManager.commit(txStatus);
        } catch (Exception ex) {
            if (txStatus != null && !txStatus.isCompleted()) {
                transactionManager.rollback(txStatus);
            }
            throw ex;
        }
    }
}