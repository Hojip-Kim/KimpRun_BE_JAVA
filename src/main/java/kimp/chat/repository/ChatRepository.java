package kimp.chat.repository;

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

    Page<Chat> findAllByIsDeletedFalseOrderByRegistedAtDesc(Pageable pageable);

    Optional<Chat> findOneByInherenceId(String inherienceId);
    
    @Query("{ 'isDeleted': false, $and: [ " +
           "{ $or: [ " +
           "  { 'authenticated': true, 'userId': { $nin: ?0 } }, " +
           "  { 'authenticated': false, 'cookiePayload': { $nin: ?1 } } " +
           "] } " +
           "] }")
    Page<Chat> findAllByIsDeletedFalseAndNotBlockedOrderByRegistedAtDesc(List<Long> blockedMemberIds, List<String> blockedGuestUuids, Pageable pageable);
}
