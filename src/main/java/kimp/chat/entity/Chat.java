package kimp.chat.entity;
import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document
@Getter
public class Chat {

    @Id
    private String id;

    private String chatID;

    private String content;

    private String authenticated;

    @CreatedDate
    @Indexed
    @Field("registed_at")
    private LocalDateTime registedAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    public Chat(){};
    public Chat(String chatID, String content, String authenticated){
        this.chatID = chatID;
        this.content = content;
        this.authenticated = authenticated;
    };

}
