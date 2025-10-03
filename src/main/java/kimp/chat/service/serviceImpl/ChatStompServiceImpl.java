package kimp.chat.service.serviceImpl;

import kimp.chat.dao.ChatDao;
import kimp.chat.vo.SaveChatMessage;
import kimp.chat.service.ChatStompService;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.BlockingQueue;
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
        log.info("채팅 메시지 큐 프로세서 시작됨");
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
                    log.error("메시지큐 프로세서 인터럽트 발생", e);
                }
                break;
            } catch (Exception e) {
                log.error("큐에서 메시지 처리 중 오류 발생", e);
            }
        }
    }

    private void saveMessageSync(SaveChatMessage saveChatMessage) {
        try {
            if (saveChatMessage.getContent().isEmpty()) {
                log.error("빈 채팅 메시지 내용 - UUID: {}", saveChatMessage.getUuid());
                return;
            }

            chatDao.insertChat(saveChatMessage.getChatID(), saveChatMessage.getContent(),
                    saveChatMessage.getAuthenticated(), saveChatMessage.getUserIp(),
                    saveChatMessage.getUuid(), saveChatMessage.getInherienceId(),
                    saveChatMessage.getIsDeleted(), saveChatMessage.getMemberId());

            log.debug("채팅 메시지 저장됨 - UUID: {}", saveChatMessage.getUuid());
        } catch (Exception e) {
            log.error("채팅 메시지 저장 실패 - UUID: {}, 오류: {}", 
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
     * 채팅 메시지를 BlockingQueue에 추가하여 순서를 보장하며 비동기 저장
     * 큐에 추가하는 작업은 동기적으로 처리되지만 매우 빠르므로 성능 영향 최소
     * 실제 DB 저장은 별도 스레드에서 비동기로 처리됨
     * 
     * @param saveChatMessage 저장할 채팅 메시지
     * @return boolean 큐 추가 성공 여부 (true: 성공, false: 실패)
     */
    @Override
    public boolean saveMessageAsync(SaveChatMessage saveChatMessage) {
        try {
            // 메시지 유효성 검증
            if(saveChatMessage.getContent().isEmpty()){
                log.error("빈 채팅 메시지 내용 - UUID: {}", saveChatMessage.getUuid());
                return false;
            }

            // BlockingQueue에 메시지 추가 (순서 보장)
            // offer는 즉시 반환되므로 동기 처리해도 성능 영향 최소
            boolean offered = messageQueue.offer(saveChatMessage);
            if (!offered) {
                // 큐가 가득 찬 경우 실패 반환 (블로킹 방지)
                log.error("메시지 큐가 가득 참 - UUID: {}, 큐 크기: {}", 
                    saveChatMessage.getUuid(), messageQueue.size());
                return false;
            }
            
            log.debug("저장을 위해 메시지가 큐에 추가됨 - UUID: {}, 큐 크기: {}", 
                saveChatMessage.getUuid(), messageQueue.size());
            
            return true;
            
        } catch (Exception e) {
            log.error("채팅 메시지 큐 추가 실패 - UUID: {}, 오류: {}", 
                saveChatMessage.getUuid(), e.getMessage(), e);
            return false;
        }
    }
}