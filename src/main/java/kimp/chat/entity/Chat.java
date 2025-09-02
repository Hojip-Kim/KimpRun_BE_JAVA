package kimp.chat.entity;
import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document( collection = "chat")
@Getter
public class Chat {

    @Id
    private String id;

    @Field("chat_id")
    private String chatID;

    @Field("content")
    private String content;

    @Field("authenticated")
    private Boolean authenticated;

    @Field("user_ip")
    private String userIp;

    @Field("cookie_payload")
    private String cookiePayload;

    @Field("inherence_id")
    private String inherenceId;

    @Field("is_deleted")
    private Boolean isDeleted;

    // user mapping
    // 만약 auth가 아니면 user는 null인 상태임.
    @Field("user_id")
    private Long userId;

    @CreatedDate
    @Indexed
    @Field("registed_at")
    private LocalDateTime registedAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    public Chat(){};
    
    public Chat(String chatID, String content, Boolean authenticated){
        this.chatID = chatID;
        this.content = content;
        this.authenticated = authenticated;
    };

    public Chat(String chatID, String content, Boolean authenticated, String userIp, String cookiePayload, String inherenceId, Boolean isDeleted){
        this.chatID = chatID;
        this.content = content;
        this.authenticated = authenticated;
        this.userIp = userIp;
        this.cookiePayload = cookiePayload;
        this.inherenceId = inherenceId;
        this.isDeleted = isDeleted;
    };

    public Chat softDeleteChat() {
        this.isDeleted = true;
        return this;
    }

    public Chat setUser(Long userId) {
        this.userId = userId;
        return this;
    }

}
