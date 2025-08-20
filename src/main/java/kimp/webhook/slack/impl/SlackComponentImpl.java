package kimp.webhook.slack.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import kimp.exception.response.ErrorResponseDTO;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.webhook.slack.SlackComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SlackComponentImpl implements SlackComponent {

    private final String webhookUrl;
    private final Slack slack;
    private final ObjectMapper mapper;

    public SlackComponentImpl(@Value("${webhook.slack.url}") String webhookUrl, Slack slack, ObjectMapper mapper) {
        this.webhookUrl = webhookUrl;
        this.slack = slack != null ? slack : Slack.getInstance();
        this.mapper = mapper != null ? mapper : new ObjectMapper();
    }

    public WebhookResponse sendToSimpleText(ErrorResponseDTO errorResponseDTO) throws JsonProcessingException {
        String text;
        try {
            text = mapper.writeValueAsString(errorResponseDTO);
        } catch (JsonProcessingException error) {
            text = error.getMessage();
            throw error;
        }

        return sendMessage(text);
    }

    @Override
    public WebhookResponse sendErrorMessage(String errorMessage, String source) {
        String formattedMessage = String.format("🚨 Error in %s: %s", source, errorMessage);
        return sendMessage(formattedMessage);
    }

    @Override
    public WebhookResponse sendSchedulerFailureMessage(String schedulerName, String errorMessage, Exception exception) {
        String formattedMessage = String.format(
            "Scheduler 실패 메시지\n" +
            "Scheduler: %s\n" +
            "에러: %s\n" +
            "예외: %s\n" +
            "시간: %s",
            schedulerName,
            errorMessage,
            exception.getClass().getSimpleName() + ": " + exception.getMessage(),
            java.time.LocalDateTime.now()
        );
        return sendMessage(formattedMessage);
    }

    private WebhookResponse sendMessage(String text) {
        Payload payload = Payload.builder().text(text).build();
        
        try {
            WebhookResponse response = slack.send(webhookUrl, payload);
            return response;
        } catch (IOException e) {
            log.error("Failed to send Slack webhook message", e);
            throw new KimprunException(
                KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, 
                "Failed to send Slack webhook message: " + e.getMessage(), 
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "SlackComponent.sendMessage"
            );
        }
    }
}
