package unit.kimp.webhook.slack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import kimp.exception.response.ErrorResponseDTO;
import kimp.webhook.slack.SlackComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SlackComponentTest {

    @Mock
    private Slack slack;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SlackComponent slackComponent;

    private final String webhookUrl = "https://dummy-webhook-url.com/test";

    @BeforeEach
    void setUp() {
        // SlackComponent를 생성자 주입으로 초기화
        slackComponent = new SlackComponent(webhookUrl, slack);
    }

    @Test
    @DisplayName("sendToSimpleText: DTO를 JSON으로 변환 후 Slack.send 호출 및 응답 반환")
    void shouldSendToSimpleTextAndReturnResponse() throws Exception {
        // given: DTO와 fake JSON
        ErrorResponseDTO dto = new ErrorResponseDTO(HttpStatus.ACCEPTED, "2nd test", "trace");
        String fakeJson = "{\"status\":202,\"message\":\"2nd test\",\"trace\":\"trace\"}";
        when(objectMapper.writeValueAsString(dto)).thenReturn(fakeJson);

        // and: Slack.send stub
        WebhookResponse fakeResponse = WebhookResponse.builder()
                .code(202)
                .body("ok")
                .build();
        when(slack.send(eq(webhookUrl), any(Payload.class))).thenReturn(fakeResponse);

        // when: 메서드 호출
        WebhookResponse actual = slackComponent.sendToSimpleText(dto);

        // then: Slack.send 결과를 그대로 반환
        assertThat(actual).isSameAs(fakeResponse);
    }
}
