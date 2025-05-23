package kimp.config.webhook;

import com.slack.api.Slack;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfig {

    @Bean
    public Slack slack() {
        return Slack.getInstance();
    }

}
