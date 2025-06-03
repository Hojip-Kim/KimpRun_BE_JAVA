package kimp.logs.controller;

import kimp.logs.dto.LogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/logs")
@RestController
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger("frontend-logs");

    @PostMapping
    public ResponseEntity<Void> logMessage(@RequestBody LogMessage logMessage) {
        MDC.put("application", logMessage.getApplication());
        MDC.put("timestamp", logMessage.getTimestamp());

        switch (logMessage.getLevel().toLowerCase()) {
            case "info":
                logger.info(logMessage.getMessage());
                break;
            case "error":
                logger.error(logMessage.getMessage());
                break;
            case "warn":
                logger.warn(logMessage.getMessage());
                break;
            case "debug":
                logger.debug(logMessage.getMessage());
                break;
            default:
                logger.info(logMessage.getMessage());
        }

        MDC.clear();
        return ResponseEntity.ok().build();
    }


}
