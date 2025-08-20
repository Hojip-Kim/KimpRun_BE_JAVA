package kimp.chat.repository;

import kimp.chat.dto.response.ChatLogResponseDto;
import kimp.chat.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {


    @Query(value="{}", fields = "{chat_id: 1, content: 1, authenticated: 1, cookie_payload:  1, user_ip: 1, registed_at: 1 }")
    Page<Chat> findAllByOrderByRegistedAtDesc(Pageable pageable);

    @Query(value="{}", fields = "{chat_id: 1, content: 1, authenticated: 1, cookie_payload:  1, user_ip: 1, registed_at: 1 }")
    Page<Chat> findAllByOrderByRegistedAtAsc(Pageable pageable);
}
