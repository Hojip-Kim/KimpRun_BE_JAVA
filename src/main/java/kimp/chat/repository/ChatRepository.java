package kimp.chat.repository;

import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.entity.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String> {


    @Query(value="{}", fields = "{chatID: 1, content: 1}")
    List<ChatLogResponseDto> findAllByOrderByRegisted_atAsc(Pageable pageable);
}
