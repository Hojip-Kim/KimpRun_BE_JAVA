package kimp.webhook.slack;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SlackComponent {

    @Value("${webhook.slack.url}")
    private String webhookUrl;
    private final Slack slack;

    public SlackComponent(@Value("${webhook.slack.url}") String webhookUrl, Slack slack) {
        this.webhookUrl = webhookUrl;
        this.slack = slack != null ? slack : Slack.getInstance();
    }

    public WebhookResponse sendToSimpleText(String paramText){
        Payload payload = Payload.builder().text(paramText).build();
        WebhookResponse response;

        try{
            response = slack.send(webhookUrl, payload);
            log.info(response.toString());
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
