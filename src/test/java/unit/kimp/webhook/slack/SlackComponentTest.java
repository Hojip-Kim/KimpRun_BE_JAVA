package unit.kimp.webhook.slack;

import com.slack.api.Slack;
import kimp.webhook.slack.SlackComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class SlackComponentTest {

    private String webhookUrl;

    @Mock
    private Slack slack;

    private SlackComponent slackComponent;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Yaml yaml = new Yaml();
        try{
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.yml");
            if(inputStream != null){

                Map<String, Object> props = yaml.load(inputStream);
                Map<String, Object> webhook = (Map<String, Object>) props.get("webhook");
                Map<String, Object> slack = (Map<String, Object>) webhook.get("slack");
                this.webhookUrl = slack.get("url").toString();

            } else {
                throw new IllegalStateException("application.yml not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load application.yml", e);
        }
        slackComponent = new SlackComponent(webhookUrl, slack);
    }

    @Test
    @DisplayName("슬랙컴포넌트가 제대로 작동을 하는지 여부")
    public void slackComponentTest(){
        String res = slackComponent.sendToSimpleText("두 번째 테스트").toString();
        System.out.println(res);
    }

}
