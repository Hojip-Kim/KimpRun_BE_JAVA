package kimp.batch.writer;

import kimp.cmc.dao.jdbc.CmcBatchDao;
import kimp.cmc.dto.common.coin.CmcApiDataDto;
import kimp.cmc.dto.common.coin.CmcCoinInfoDataDto;
import kimp.cmc.dto.common.coin.CmcCoinMapDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class CmcCoinBatchWriter {

    private final CmcBatchDao cmcBatchDao;

    public CmcCoinBatchWriter(CmcBatchDao cmcBatchDao) {
        this.cmcBatchDao = cmcBatchDao;
    }

    /**
     * 코인 맵 데이터 Writer
     */
    @Transactional("batchTransactionManager")
    public ItemWriter<CmcCoinMapDataDto> getCoinMapWriter() {
        return new ItemWriter<CmcCoinMapDataDto>() {
            @Override
            public void write(Chunk<? extends CmcCoinMapDataDto> chunk) throws Exception {
                List<? extends CmcCoinMapDataDto> items = chunk.getItems();
                if (!items.isEmpty()) {
                    log.info("코인 맵 데이터 Writer 시작: {} 건", items.size());
                    // 안전한 타입 변환
                    @SuppressWarnings("unchecked")
                    List<CmcCoinMapDataDto> coinMapItems = (List<CmcCoinMapDataDto>) items;
                    cmcBatchDao.upsertCmcCoinMap(coinMapItems);
                    log.info("코인 맵 데이터 Writer 완료: {} 건", items.size());
                }
            }
        };
    }

    /**
     * 코인 최신 정보 Writer
     */
    @Transactional("batchTransactionManager")
    public ItemWriter<CmcApiDataDto> getLatestCoinInfoWriter() {
        return new ItemWriter<CmcApiDataDto>() {
            @Override
            public void write(Chunk<? extends CmcApiDataDto> chunk) throws Exception {
                List<? extends CmcApiDataDto> items = chunk.getItems();
                if (!items.isEmpty()) {
                    log.info("코인 최신 정보 Writer 시작: {} 건", items.size());
                    // 안전한 타입 변환
                    @SuppressWarnings("unchecked")
                    List<CmcApiDataDto> latestInfoItems = (List<CmcApiDataDto>) items;
                    cmcBatchDao.updateCmcCoinLatestInfo(latestInfoItems);
                    log.info("코인 최신 정보 Writer 완료: {} 건", items.size());
                }
            }
        };
    }

    /**
     * 코인 상세 정보 Writer
     */
    @Transactional("batchTransactionManager")
    public ItemWriter<List<CmcCoinInfoDataDto>> getCoinInfoWriter() {
        return new ItemWriter<List<CmcCoinInfoDataDto>>() {
            @Override
            public void write(Chunk<? extends List<CmcCoinInfoDataDto>> chunk) throws Exception {
                for (List<CmcCoinInfoDataDto> batchItems : chunk.getItems()) {
                    if (batchItems != null && !batchItems.isEmpty()) {
                        log.info("코인 상세 정보 배치 Writer 시작: {} 건", batchItems.size());
                        cmcBatchDao.upsertCmcCoinInfo(batchItems);
                        log.info("코인 상세 정보 배치 Writer 완료: {} 건", batchItems.size());
                    }
                }
            }
        };
    }
} 