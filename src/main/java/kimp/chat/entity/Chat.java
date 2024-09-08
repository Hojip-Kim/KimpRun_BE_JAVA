package kimp.chat.entity;
import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Getter
public class Chat {

    @Id
    private String id;

    private String chatID;

    private String content;

    @CreatedDate
    @Indexed
    private LocalDateTime registed_at;

    @LastModifiedDate
    private LocalDateTime updated_at;

    public Chat(){};
    public Chat(String chatID, String content){
        this.chatID = chatID;
        this.content = content;
    };

}
