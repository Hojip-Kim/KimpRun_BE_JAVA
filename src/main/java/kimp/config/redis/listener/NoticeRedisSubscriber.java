package kimp.config.redis.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.common.redis.constant.RedisChannelType;
import kimp.common.redis.pubsub.subscriber.AbstractRedisMessageSubscriber;
import kimp.notice.dto.response.NoticeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis Pub/Sub 공지사항 Subscriber
 * 분산 서버 환경에서 공지사항 이벤트를 모든 서버에 전파하는 모듈
 *
 */
@Component
@Slf4j
public class NoticeRedisSubscriber extends AbstractRedisMessageSubscriber<NoticeDto> {

    private final SimpMessagingTemplate messagingTemplate;

    public NoticeRedisSubscriber(ObjectMapper objectMapper, SimpMessagingTemplate messagingTemplate) {
        super(objectMapper);
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void handleMessage(String channel, NoticeDto notice) {
        // Stomp로 브로드캐스트
        String destination = "/topic/notices/" + notice.getExchangeType().name().toLowerCase();
        messagingTemplate.convertAndSend(destination, notice);
    }

    @Override
    public String getChannelPattern() {
        return RedisChannelType.NOTICE_ALL.getChannel();
    }

    @Override
    public Class<NoticeDto> getMessageType() {
        return NoticeDto.class;
    }
}
