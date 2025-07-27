package kimp.webhook.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import kimp.exception.response.ErrorResponseDTO;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SlackComponent {

    private String webhookUrl;
    private Slack slack;
    private ObjectMapper mapper;

    public SlackComponent(@Value("${webhook.slack.url}") String webhookUrl, Slack slack) {
        this.webhookUrl = webhookUrl;
        this.slack = slack != null ? slack : Slack.getInstance();
    }

    public WebhookResponse sendToSimpleText(ErrorResponseDTO errorResponseDTO) throws JsonProcessingException {
        String text;
        try {
             text = mapper.writeValueAsString(errorResponseDTO);

        }catch(JsonProcessingException error){
            text = error.getMessage();
        }

        Payload payload = Payload.builder().text(text).build();
        WebhookResponse response;

        try{
            response = slack.send(webhookUrl, payload);
            log.info(response.toString());
            return response;
        } catch (IOException e) {
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "Failed to send Slack webhook message", HttpStatus.INTERNAL_SERVER_ERROR, "SlackComponent.sendToSimpleText");
        }


    }
}
