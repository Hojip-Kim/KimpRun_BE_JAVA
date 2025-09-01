package kimp.chat.service.serviceImpl;

import kimp.chat.dao.ChatDao;
import kimp.chat.dto.vo.DeleteAnonChatMessage;
import kimp.chat.dto.vo.SaveChatMessage;
import kimp.chat.entity.Chat;
import kimp.chat.service.ChatStompService;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class ChatStompServiceImpl implements ChatStompService {
    private final ChatDao chatDao;
    private final BlockingQueue<SaveChatMessage> messageQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean processingActive = new AtomicBoolean(true);
    private Thread queueProcessor;

    public ChatStompServiceImpl(ChatDao chatDao) {
        this.chatDao = chatDao;
    }

    @PostConstruct
    public void initQueueProcessor() {
        queueProcessor = new Thread(this::processMessageQueue, "ChatMessageProcessor");
        queueProcessor.start();
        log.info("Chat message queue processor started");
    }

    @PreDestroy
    public void shutdownQueueProcessor() {
        processingActive.set(false);
        if (queueProcessor != null) {
            queueProcessor.interrupt();
            try {
                queueProcessor.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Queue processor shutdown interrupted");
            }
        }
    }

    private void processMessageQueue() {
        while (processingActive.get() || !messageQueue.isEmpty()) {
            try {
                SaveChatMessage message = messageQueue.take();
                saveMessageSync(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (processingActive.get()) {
                    log.error("Message queue processor interrupted unexpectedly", e);
                }
                break;
            } catch (Exception e) {
                log.error("Error processing message from queue", e);
            }
        }
    }

    private void saveMessageSync(SaveChatMessage saveChatMessage) {
        try {
            if (saveChatMessage.getContent().isEmpty()) {
                log.error("Empty chat message content - UUID: {}", saveChatMessage.getUuid());
                return;
            }

            chatDao.insertChat(saveChatMessage.getChatID(), saveChatMessage.getContent(),
                    saveChatMessage.getAuthenticated(), saveChatMessage.getUserIp(),
                    saveChatMessage.getUuid(), saveChatMessage.getInherienceId(),
                    saveChatMessage.getIsDeleted(), saveChatMessage.getMemberId());

            log.debug("Chat message saved - UUID: {}", saveChatMessage.getUuid());
        } catch (Exception e) {
            log.error("Failed to save chat message - UUID: {}, Error: {}", 
                saveChatMessage.getUuid(), e.getMessage(), e);
        }
    }

    @EventListener
    @Override
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        log.info("STOMP 웹소켓 연결 성공: {}", sessionId);

    }

    @EventListener
    @Override
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        log.info("STOMP 웹소켓 연결 종료: {}", sessionId);
        
    }

    @Override
    public void saveMessage(SaveChatMessage saveChatMessage) {
        if(saveChatMessage.getContent().isEmpty()){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Chat message content cannot be empty", HttpStatus.BAD_REQUEST, "ChatWebsocketServiceImpl.saveMessage");
        }

        chatDao.insertChat(saveChatMessage.getChatID(), saveChatMessage.getContent(), saveChatMessage.getAuthenticated(),
                saveChatMessage.getUserIp(), saveChatMessage.getUuid(), saveChatMessage.getInherienceId(), saveChatMessage.getIsDeleted(), saveChatMessage.getMemberId());
    }

    /**
     * 채팅 메시지를 비동기적으로 저장.
     * 전용 스레드 풀에서 실행되어 메인 처리 스레드를 블로킹하지 않음.
     * 
     * @param saveChatMessage 저장할 채팅 메시지
     * @return CompletableFuture<Void> 비동기 작업 결과
     */
    @Override
    @Async("chatSaveExecutor")
    public CompletableFuture<Void> saveMessageAsync(SaveChatMessage saveChatMessage) {
        try {
            log.debug("Async chat save started - UUID: {}, Thread: {}", 
                saveChatMessage.getUuid(), Thread.currentThread().getName());
            
            // 메시지 유효성 검증
            if(saveChatMessage.getContent().isEmpty()){
                log.error("Empty chat message content - UUID: {}", saveChatMessage.getUuid());
                throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, 
                    "Chat message content cannot be empty", HttpStatus.BAD_REQUEST, 
                    "ChatStompServiceImpl.saveMessageAsync");
            }

            // 비동기 DB 저장
            chatDao.insertChat(saveChatMessage.getChatID(), saveChatMessage.getContent(), 
                saveChatMessage.getAuthenticated(), saveChatMessage.getUserIp(), 
                saveChatMessage.getUuid(), // cookiePayload (uuid 필드에 저장됨)
                saveChatMessage.getInherienceId(), // randomUUID (inherienceId 필드에 저장됨)
                saveChatMessage.getIsDeleted(),
                    saveChatMessage.getMemberId());
            
            log.debug("Async chat save completed - UUID: {}", saveChatMessage.getUuid());
            return CompletableFuture.completedFuture(null);
            
        } catch (Exception e) {
            log.error("Failed to save chat message asynchronously - UUID: {}, Error: {}", 
                saveChatMessage.getUuid(), e.getMessage(), e);
            // 예외를 CompletableFuture로 감싸서 반환
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }
}