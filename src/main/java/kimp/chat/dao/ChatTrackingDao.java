package kimp.chat.dao;

import kimp.chat.entity.ChatTracking;

import java.util.List;
import java.util.Optional;

public interface ChatTrackingDao {

    ChatTracking save(ChatTracking chatTracking);
    
    Optional<ChatTracking> findByMemberId(Long memberId);
    
    Optional<ChatTracking> findByUuid(String uuid);
    
    ChatTracking createOrUpdateChatTracking(String uuid, String nickname, Long memberId);
    
    void updateNicknameByMemberId(Long memberId, String newNickname);
    
    List<ChatTracking> findByMemberIdIn(List<Long> memberIds);
    
    List<ChatTracking> findByUuidIn(List<String> uuids);
}