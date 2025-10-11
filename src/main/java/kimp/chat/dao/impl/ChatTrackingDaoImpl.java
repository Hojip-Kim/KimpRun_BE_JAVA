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
    public Optional<ChatTracking> findByNickname(String nickname) {
        return chatTrackingRepository.findByNickname(nickname);
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
            // 기존 레코드가 있으면 닉네임만 업데이트
            ChatTracking chatTracking = existing.get();

            // 닉네임이 변경되었고, 새 닉네임이 이미 다른 레코드에서 사용 중인지 확인
            if (!chatTracking.getNickname().equals(nickname)) {
                Optional<ChatTracking> nicknameExists = chatTrackingRepository.findByNickname(nickname);
                if (nicknameExists.isPresent() && !nicknameExists.get().getId().equals(chatTracking.getId())) {
                    // 닉네임이 이미 다른 레코드에서 사용 중이므로 기존 레코드 반환 (업데이트 안함)
                    return chatTracking;
                }
                chatTracking.updateNickname(nickname);
            }
            return chatTrackingRepository.save(chatTracking);
        } else {
            // 새로운 레코드 생성 전 닉네임 중복 체크
            Optional<ChatTracking> nicknameExists = chatTrackingRepository.findByNickname(nickname);
            if (nicknameExists.isPresent()) {
                // 동일한 닉네임을 가진 레코드가 이미 존재하면 해당 레코드 반환
                return nicknameExists.get();
            }

            // 기존 데이터가 없고 닉네임도 중복이 아니면 새로 생성
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