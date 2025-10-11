package kimp.chat.service.serviceImpl;

import kimp.chat.dao.ChatTrackingDao;
import kimp.chat.entity.ChatTracking;
import kimp.chat.service.ChatTrackingService;
import kimp.user.dto.response.UpdateAnonNicknameResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatTrackingServiceImpl implements ChatTrackingService {

    private final ChatTrackingDao chatTrackingDao;

    public ChatTrackingServiceImpl(ChatTrackingDao chatTrackingDao) {
        this.chatTrackingDao = chatTrackingDao;
    }

    @Override
    public String getNicknameByMemberId(Long memberId) {
        Optional<ChatTracking> chatTracking = chatTrackingDao.findByMemberId(memberId);
        return chatTracking.map(ChatTracking::getNickname).orElse(null);
    }

    @Override
    public String getNicknameByUuid(String uuid) {
        Optional<ChatTracking> chatTracking = chatTrackingDao.findByUuid(uuid);
        return chatTracking.map(ChatTracking::getNickname).orElse(null);
    }
    
    @Override
    public String getNicknameByUuidAndAuthenticated(String uuid, Boolean isAuthenticated) {
        Optional<ChatTracking> chatTracking = chatTrackingDao.findByUuidAndIsAuthenticated(uuid, isAuthenticated);
        return chatTracking.map(ChatTracking::getNickname).orElse(null);
    }

    @Override
    public UpdateAnonNicknameResponse createOrUpdateChatTracking(String uuid, String nickname, Long memberId, Boolean isAuthenticated) {

        ChatTracking chatTracking = chatTrackingDao.createOrUpdateChatTracking(uuid, nickname, memberId, isAuthenticated);

        return UpdateAnonNicknameResponse.builder()
                .email(null)
                .name(chatTracking.getNickname())
                .role("GUEST")
                .number(null)
                .build();
    }

    @Override
    public void updateNicknameByMemberId(Long memberId, String newNickname) {
        chatTrackingDao.updateNicknameByMemberId(memberId, newNickname);
    }
    
    @Override
    public void updateNicknameByUuid(String uuid, String newNickname) {
        chatTrackingDao.updateNicknameByUuid(uuid, newNickname);
    }
    
    @Override
    public Map<Long, String> getNicknamesByMemberIds(List<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return Map.of();
        }
        
        List<ChatTracking> trackings = chatTrackingDao.findByMemberIdIn(memberIds);
        return trackings.stream()
                .collect(Collectors.toMap(ChatTracking::getMemberId, ChatTracking::getNickname));
    }
    
    @Override
    public Map<String, String> getNicknamesByUuids(List<String> uuids) {
        if (uuids == null || uuids.isEmpty()) {
            return Map.of();
        }
        
        List<ChatTracking> trackings = chatTrackingDao.findByUuidIn(uuids);
        return trackings.stream()
                .collect(Collectors.toMap(ChatTracking::getUuid, ChatTracking::getNickname));
    }
}