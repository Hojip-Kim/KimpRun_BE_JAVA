package kimp.chat.repository;

import kimp.chat.entity.ChatTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatTrackingRepository extends JpaRepository<ChatTracking, Long> {

    Optional<ChatTracking> findByMemberId(Long memberId);
    
    Optional<ChatTracking> findByUuid(String uuid);
    
    Optional<ChatTracking> findByUuidAndIsAuthenticated(String uuid, Boolean isAuthenticated);
    
    Optional<ChatTracking> findByMemberIdOrUuid(Long memberId, String uuid);

    Optional<ChatTracking> findByNickname(String nickname);

    List<ChatTracking> findByMemberIdIn(List<Long> memberIds);

    List<ChatTracking> findByUuidIn(List<String> uuids);
}