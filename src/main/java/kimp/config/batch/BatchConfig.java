package kimp.config.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;


/**
 * Spring Batch 설정 클래스
 * Spring Boot 3.0+ (Spring Batch 5.0+)에서는 완전한 Auto Configuration 활용
 * 
 * Spring Boot가 자동으로 설정하는 것들:
 * - JobRepository (application.yml의 spring.batch.jdbc 설정 적용)  
 * - JobLauncher (SyncTaskExecutor 사용하여 기본적으로 동기 실행)
 * - JobExplorer
 * - DataSource, TransactionManager 자동 연결 (DatabaseConfig의 batchTransactionManager 사용)
 * 
 * 추가 설정이 필요한 경우 application.yml에서 처리:
 * - spring.task.execution.* 설정으로 TaskExecutor 커스터마이징 가능
 */
@Slf4j
@Configuration
public class BatchConfig {
    
    // Spring Boot Auto Configuration이 모든 것을 처리
    // 커스텀 Bean 정의로 인한 충돌 방지를 위해 Bean 정의 제거
    
}
