package kimp.config.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync(proxyTargetClass = true)
@Configuration
@Slf4j
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(18);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("Scrap-Async-");
        executor.initialize();
        return executor;
    }

    /**
     * 채팅 메시지 저장을 위한 전용 스레드 풀
     * - 코어 스레드: 10개 (기본 채팅 부하 처리)
     * - 최대 스레드: 50개 (피크 시간 대응)
     * - 큐 용량: 1000개 (메시지 버퍼링)
     */
    @Bean(name = "chatSaveExecutor")
    public Executor chatSaveExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("ChatSave-");
        executor.setKeepAliveSeconds(60);
        
        // 큐가 꽉 찰 경우 호출 스레드에서 직접 실행 (메시지 유실 방지)
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                log.warn("Chat save thread pool is full, executing in caller thread");
                if (!executor.isShutdown()) {
                    r.run();
                }
            }
        });
        
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

}
