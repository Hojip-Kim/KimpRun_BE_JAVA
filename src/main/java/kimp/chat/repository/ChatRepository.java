package kimp.chat.repository;

import kimp.chat.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {


    Page<Chat> findAllByIsDeletedFalseOrderByRegistedAtDesc(Pageable pageable);

    Optional<Chat> findOneByInherenceId(String inherienceId);
}
