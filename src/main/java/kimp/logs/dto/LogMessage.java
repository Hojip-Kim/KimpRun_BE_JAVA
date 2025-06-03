package kimp.logs.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class LogMessage {
    private String level;
    private String message;
    private String timestamp;
    private String application;


    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getApplication() {
        return application;
    }


}
