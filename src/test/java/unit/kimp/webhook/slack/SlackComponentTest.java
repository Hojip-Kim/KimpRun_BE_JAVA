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
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SlackComponentTest {

    @Mock
    private Slack slack;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private SlackComponent slackComponent;

    private String webhookUrl;

    @BeforeEach
    void setUp() throws Exception {
        // application-test.yml에서 webhook.url 읽어오기
        Yaml yaml = new Yaml();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("application-test.yml")) {
            if (is == null) {
                throw new IllegalStateException("application-test.yml not found in classpath");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> props = yaml.load(is);
            @SuppressWarnings("unchecked")
            Map<String, Object> webhook = (Map<String, Object>) props.get("webhook");
            @SuppressWarnings("unchecked")
            Map<String, Object> slackMap = (Map<String, Object>) webhook.get("slack");
            this.webhookUrl = slackMap.get("url").toString();
        }

        slackComponent = new SlackComponent(webhookUrl, slack);

        Field mapperField = SlackComponent.class.getDeclaredField("mapper");
        mapperField.setAccessible(true);
        mapperField.set(slackComponent, mapper);
    }

    @Test
    @DisplayName("sendToSimpleText: JSON 변환 후 Payload로 slack.send 호출 테스트")
    void sendToSimpleText_callsSlackSend() throws IOException {
        // given
        ErrorResponseDTO dto = new ErrorResponseDTO(
                HttpStatus.ACCEPTED, "2nd test", "trace"
        );
        String fakeJson = "{\"status\":202,\"message\":\"2nd test\",\"trace\":\"trace\"}";

        when(mapper.writeValueAsString(dto)).thenReturn(fakeJson);

        WebhookResponse fakeResponse = WebhookResponse.builder()
                .code(202)
                .body("ok")
                .build();
        when(slack.send(
                eq(webhookUrl),
                any(Payload.class)
        )).thenReturn(fakeResponse);

        // when
        WebhookResponse actual = slackComponent.sendToSimpleText(dto);

        // then
        assertThat(actual).isSameAs(fakeResponse);
    }
}
