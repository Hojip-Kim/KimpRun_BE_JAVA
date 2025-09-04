package kimp.chat.dao.impl;

import kimp.chat.dao.ChatTrackingDao;
import kimp.chat.entity.ChatTracking;
import kimp.chat.repository.ChatTrackingRepository;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChatTrackingDaoImpl implements ChatTrackingDao {

    private final ChatTrackingRepository chatTrackingRepository;

    public ChatTrackingDaoImpl(ChatTrackingRepository chatTrackingRepository) {
        this.chatTrackingRepository = chatTrackingRepository;
    }

    @Override
    public ChatTracking save(ChatTracking chatTracking) {
        return chatTrackingRepository.save(chatTracking);
    }

    @Override
    public Optional<ChatTracking> findByMemberId(Long memberId) {
        return chatTrackingRepository.findByMemberId(memberId);
    }

    @Override
    public Optional<ChatTracking> findByUuid(String uuid) {
        return chatTrackingRepository.findByUuid(uuid);
    }

    @Override
    public Optional<ChatTracking> findByUuidAndIsAuthenticated(String uuid, Boolean isAuthenticated) {
        return chatTrackingRepository.findByUuidAndIsAuthenticated(uuid, isAuthenticated);
    }

    @Override
    public ChatTracking createOrUpdateChatTracking(String uuid, String nickname, Long memberId, Boolean isAuthenticated) {
        Optional<ChatTracking> existing;
        
        // 인증된 사용자의 경우 memberId로 찾기
        if (isAuthenticated && memberId != null) {
            existing = chatTrackingRepository.findByMemberId(memberId);
        } else {
            // 비인증 사용자의 경우 uuid와 authenticated 상태로 정확히 찾기
            existing = chatTrackingRepository.findByUuidAndIsAuthenticated(uuid, isAuthenticated);
        }
        
        if (existing.isPresent()) {
            ChatTracking chatTracking = existing.get().updateNickname(nickname);
            return chatTrackingRepository.save(chatTracking);
        } else {
            // 기존 데이터가 없는 경우 새로 생성
            ChatTracking newChatTracking = new ChatTracking(uuid, nickname, memberId, isAuthenticated);
            return chatTrackingRepository.save(newChatTracking);
        }
    }

    @Override
    public void updateNicknameByMemberId(Long memberId, String newNickname) {
        Optional<ChatTracking> chatTracking = findByMemberId(memberId);
        if (chatTracking.isPresent()) {
            ChatTracking updated = chatTracking.get().updateNickname(newNickname);
            chatTrackingRepository.save(updated);
        }else{
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "not have matched memberId : " + memberId, HttpStatus.BAD_REQUEST, "ChatTrackingDaoImpl.updateNicknameByMemberId");
        }
    }
    
    @Override
    public void updateNicknameByUuid(String uuid, String newNickname) {
        Optional<ChatTracking> chatTracking = findByUuidAndIsAuthenticated(uuid, false);
        if (chatTracking.isPresent()) {
            ChatTracking updated = chatTracking.get().updateNickname(newNickname);
            chatTrackingRepository.save(updated);
        }else{
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "not have matched uuid : " + uuid, HttpStatus.BAD_REQUEST, "ChatTrackingDaoImpl.updateNicknameByUuid");
        }
    }
    
    @Override
    public List<ChatTracking> findByMemberIdIn(List<Long> memberIds) {
        return chatTrackingRepository.findByMemberIdIn(memberIds);
    }
    
    @Override
    public List<ChatTracking> findByUuidIn(List<String> uuids) {
        return chatTrackingRepository.findByUuidIn(uuids);
    }
}