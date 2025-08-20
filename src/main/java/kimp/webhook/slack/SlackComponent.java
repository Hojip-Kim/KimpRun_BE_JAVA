package kimp.webhook.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.slack.api.webhook.WebhookResponse;
import kimp.exception.response.ErrorResponseDTO;

public interface SlackComponent {
    WebhookResponse sendToSimpleText(ErrorResponseDTO errorResponseDTO) throws JsonProcessingException;
    WebhookResponse sendErrorMessage(String errorMessage, String source);
    WebhookResponse sendSchedulerFailureMessage(String schedulerName, String errorMessage, Exception exception);
}
