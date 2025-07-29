package kimp.batch.step;

import kimp.batch.processor.CmcCoinBatchProcessor;
import kimp.batch.processor.CmcExchangeBatchProcessor;
import kimp.batch.reader.CmcCoinBatchReader;
import kimp.batch.reader.CmcExchangeBatchReader;
import kimp.batch.writer.CmcCoinBatchWriter;
import kimp.batch.writer.CmcExchangeBatchWriter;
import kimp.cmc.dao.jdbc.CmcBatchDao;
import kimp.cmc.dto.common.coin.CmcApiDataDto;
import kimp.cmc.dto.common.coin.CmcCoinInfoDataDto;
import kimp.cmc.dto.common.coin.CmcCoinInfoDataMapDto;
import kimp.cmc.dto.common.coin.CmcCoinMapDataDto;
import kimp.cmc.dto.common.exchange.CmcExchangeDetailDto;
import kimp.cmc.dto.common.exchange.CmcExchangeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CmcBatchStep {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager batchTransactionManager;
    
    private final CmcCoinBatchReader cmcCoinBatchReader;
    private final CmcExchangeBatchReader cmcExchangeBatchReader;
    private final CmcCoinBatchProcessor cmcCoinBatchProcessor;
    private final CmcExchangeBatchProcessor cmcExchangeBatchProcessor;
    private final CmcCoinBatchWriter cmcCoinBatchWriter;
    private final CmcExchangeBatchWriter cmcExchangeBatchWriter;
    private final CmcBatchDao cmcBatchDao;

    /**
     * 코인 맵 데이터 수집 및 저장
     */
    @Bean
    public Step coinMapSyncStep() {
        return new StepBuilder("coinMapSyncStep", jobRepository)
                .<CmcCoinMapDataDto, CmcCoinMapDataDto>chunk(1000, batchTransactionManager)
                .reader(cmcCoinBatchReader.getCoinMapReader())
                .processor(cmcCoinBatchProcessor.getCoinMapProcessor())
                .writer(cmcCoinBatchWriter.getCoinMapWriter())
                .build();
    }

    /**
     * 코인 최신 정보 업데이트 (랭킹 등)
     */
    @Bean
    public Step coinLatestInfoSyncStep() {
        return new StepBuilder("coinLatestInfoSyncStep", jobRepository)
                .<CmcApiDataDto, CmcApiDataDto>chunk(1000, batchTransactionManager)
                .reader(cmcCoinBatchReader.getLatestCoinInfoReader())
                .processor(cmcCoinBatchProcessor.getLatestCoinInfoProcessor())
                .writer(cmcCoinBatchWriter.getLatestCoinInfoWriter())
                .build();
    }

    /**
     * 코인 상세 정보 수집 및 저장
     */
    @Bean
    public Step coinDetailInfoSyncStep() {
        return new StepBuilder("coinDetailInfoSyncStep", jobRepository)
                .<List<CmcCoinInfoDataDto>, List<CmcCoinInfoDataDto>>chunk(10, batchTransactionManager)
                .reader(cmcCoinBatchReader.getCmcCoinInfoReader())
                .processor(cmcCoinBatchProcessor.getCoinInfoProcessor())
                .writer(cmcCoinBatchWriter.getCoinInfoWriter())
                .build();
    }

    /**
     * 거래소 맵 데이터 수집 및 저장
     */
    @Bean
    public Step exchangeMapSyncStep() {
        return new StepBuilder("exchangeMapSyncStep", jobRepository)
                .<CmcExchangeDto, CmcExchangeDto>chunk(1000, batchTransactionManager)
                .reader(cmcExchangeBatchReader.getExchangeMapReader())
                .processor(cmcExchangeBatchProcessor.getExchangeMapProcessor())
                .writer(cmcExchangeBatchWriter.getExchangeMapWriter())
                .build();
    }

    /**
     * 거래소 상세 정보 수집 및 저장 (멀티스레드)
     */
    @Bean
    public Step exchangeDetailInfoSyncStep() {
        return new StepBuilder("exchangeDetailInfoSyncStep", jobRepository)
                .tasklet(exchangeInfoBulkTasklet(), batchTransactionManager)
                .build();
    }

    @Bean
    public Tasklet exchangeInfoBulkTasklet() {
        return (contribution, chunkContext) -> {
            log.info("CmcExchange 상세 정보 멀티스레딩 작업 시작 ");
            
            // 모든 CMC Exchange ID 조회
            List<Integer> allExchangeIds = cmcBatchDao.getCmcExchangeIds(1000);
            log.info("총 {} 개의 CMC Exchange ID 조회됨", allExchangeIds.size());
            
            // 100개씩 나누어서 배치 생성
            int batchSize = 100;
            List<List<Integer>> batches = new ArrayList<>();
            for (int i = 0; i < allExchangeIds.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, allExchangeIds.size());
                batches.add(allExchangeIds.subList(i, endIndex));
            }
            
            log.info("총 {} 개의 배치로 분할하여 멀티스레드 처리", batches.size());
            
            // 스레드 풀 생성 (최대 5개 스레드)
            ExecutorService executor = Executors.newFixedThreadPool(Math.min(5, batches.size()));
            AtomicInteger processedCount = new AtomicInteger(0);
            AtomicInteger errorCount = new AtomicInteger(0);
            
            try {
                // 각 배치를 병렬로 처리
                List<CompletableFuture<Void>> futures = batches.stream()
                    .map(batch -> CompletableFuture.runAsync(() -> {
                        try {
                            // CMC API 호출 (Rate Limiter 적용됨)
                            var exchangeInfoMap = cmcExchangeBatchReader.getCmcExchangeInfoComponent().getExchangeInfo(batch);
                            
                            if (exchangeInfoMap != null && !exchangeInfoMap.isEmpty()) {
                                List<CmcExchangeDetailDto> exchangeInfoList = exchangeInfoMap.values().stream().toList();
                                cmcBatchDao.upsertCmcExchangeInfo(exchangeInfoList);
                                int processed = processedCount.addAndGet(exchangeInfoList.size());
                                log.info("Exchange 배치 처리 완료: {} 건 (전체 진행률: {}/{})", 
                                    exchangeInfoList.size(), processed, allExchangeIds.size());
                            }
                        } catch (org.springframework.web.client.RestClientException e) {
                            errorCount.incrementAndGet();
                            log.error("CmcExchange API 호출 중 JSON 파싱 오류 발생 - Exchange IDs: {}", batch, e);
                        } catch (Exception e) {
                            errorCount.incrementAndGet();
                            log.error("CmcExchange 배치 처리 중 예상치 못한 오류 발생 - Exchange IDs: {}", batch, e);
                        }
                    }, executor))
                    .toList();
                
                // 모든 작업 완료 대기
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                
            } finally {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            
            log.info("CmcExchange 상세 정보 일괄 처리 작업 완료 - 처리: {} 건, 오류: {} 건", 
                processedCount.get(), errorCount.get());
            return RepeatStatus.FINISHED;
        };
    }

    /**
     * CmcCoinInfo 일괄 처리 (모든 CMC 코인 정보)
     */
    @Bean
    public Step coinInfoBulkStep() {
        return new StepBuilder("coinInfoBulkStep", jobRepository)
                .tasklet(coinInfoBulkTasklet(), batchTransactionManager)
                .build();
    }

    @Bean
    public Tasklet coinInfoBulkTasklet() {
        return (contribution, chunkContext) -> {
            log.info("CmcCoinInfo 멀티스레딩 작업 시작");
            
            // 모든 CMC Coin ID 조회
            List<Long> allCmcCoinIds = cmcBatchDao.getAllCmcCoinIds();
            log.info("총 {} 개의 CMC Coin ID 조회됨", allCmcCoinIds.size());
            
            // 100개씩 나누어서 배치 생성
            int batchSize = 100;
            List<List<Long>> batches = new ArrayList<>();
            for (int i = 0; i < allCmcCoinIds.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, allCmcCoinIds.size());
                batches.add(allCmcCoinIds.subList(i, endIndex));
            }
            
            log.info("총 {} 개의 배치로 분할하여 멀티스레드 처리", batches.size());
            
            // 스레드 풀 생성
            ExecutorService executor = Executors.newFixedThreadPool(Math.min(5, batches.size()));
            AtomicInteger processedCount = new AtomicInteger(0);
            AtomicInteger errorCount = new AtomicInteger(0);
            
            try {
                // 각 배치를 병렬로 처리
                List<CompletableFuture<Void>> futures = batches.stream()
                    .map(batch -> CompletableFuture.runAsync(() -> {
                        List<Integer> intBatchIds = batch.stream()
                            .map(Long::intValue)
                            .toList();
                        
                        try {
                            // CMC API 호출
                            CmcCoinInfoDataMapDto coinInfoMap = cmcCoinBatchReader.getCmcCoinInfoComponent().getCmcCoinInfos(intBatchIds);
                            
                            if (coinInfoMap != null && !coinInfoMap.isEmpty()) {
                                List<CmcCoinInfoDataDto> coinInfoList = coinInfoMap.values().stream().toList();
                                
                                // CmcCoinInfo 데이터 저장
                                cmcBatchDao.upsertCmcCoinInfoBulk(coinInfoList);
                                
                                // CmcMainnet 데이터 처리 (explorer URLs)
                                cmcBatchDao.upsertCmcMainnet(coinInfoList);
                                
                                // CmcPlatform 데이터 처리 (platform 정보)
                                cmcBatchDao.upsertCmcPlatform(coinInfoList);
                                
                                int processed = processedCount.addAndGet(coinInfoList.size());
                                log.info("배치 처리 완료: {} 건 (전체 진행률: {}/{})", 
                                    coinInfoList.size(), processed, allCmcCoinIds.size());
                            }
                        } catch (org.springframework.web.client.RestClientException e) {
                            errorCount.incrementAndGet();
                            log.error("CmcCoinInfo API 호출 중 JSON 파싱 오류 발생 - Coin IDs: {}", intBatchIds, e);
                        } catch (Exception e) {
                            errorCount.incrementAndGet();
                            log.error("CmcCoinInfo 배치 처리 중 예상치 못한 오류 발생 - Coin IDs: {}", intBatchIds, e);
                        }
                    }, executor))
                    .toList();
                
                // 모든 작업 완료 대기
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                
            } finally {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            
            log.info("CmcCoinInfo 일괄 처리 작업 완료 - 처리: {} 건, 오류: {} 건", 
                processedCount.get(), errorCount.get());
            return RepeatStatus.FINISHED;
        };
    }

    /**
     * CmcCoinMeta 데이터 처리
     */
    @Bean
    public Step coinMetaStep() {
        return new StepBuilder("coinMetaStep", jobRepository)
                .<CmcApiDataDto, CmcApiDataDto>chunk(1000, batchTransactionManager)
                .reader(cmcCoinBatchReader.getLatestCoinInfoReader())
                .writer(coinMetaWriter())
                .build();
    }

    @Bean
    public ItemWriter<CmcApiDataDto> coinMetaWriter() {
        return items -> {
            List<CmcApiDataDto> itemList = new ArrayList<>(items.getItems());
            cmcBatchDao.upsertCmcCoinMeta(itemList);
        };
    }

    /**
     * CMC Coin과 기존 Coin 매핑
     */
    @Bean
    public Step coinMappingStep() {
        return new StepBuilder("coinMappingStep", jobRepository)
                .tasklet(coinMappingTasklet(), batchTransactionManager)
                .build();
    }

    @Bean
    public Tasklet coinMappingTasklet() {
        return (contribution, chunkContext) -> {
            log.info("CMC Coin과 기존 Coin 매핑 작업 시작");
            cmcBatchDao.linkCmcCoinWithExistingCoin();
            log.info("CMC Coin과 기존 Coin 매핑 작업 완료");
            return RepeatStatus.FINISHED;
        };
    }
}
