package kimp.chat.service;

import kimp.user.dto.response.UpdateAnonNicknameResponse;

import java.util.List;
import java.util.Map;

public interface ChatTrackingService {

    String getNicknameByMemberId(Long memberId);
    
    String getNicknameByUuid(String uuid);
    
    String getNicknameByUuidAndAuthenticated(String uuid, Boolean isAuthenticated);

    UpdateAnonNicknameResponse createOrUpdateChatTracking(String uuid, String nickname, Long memberId, Boolean isAuthenticated);
    
    void updateNicknameByMemberId(Long memberId, String newNickname);
    
    void updateNicknameByUuid(String uuid, String newNickname);
    
    Map<Long, String> getNicknamesByMemberIds(List<Long> memberIds);
    
    Map<String, String> getNicknamesByUuids(List<String> uuids);
}