package kimp.user.service.impl;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.FollowDao;
import kimp.user.dao.MemberDao;
import kimp.user.dto.response.FollowResponse;
import kimp.user.entity.Follow;
import kimp.user.entity.Member;
import kimp.user.service.FollowService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements FollowService {

    private final FollowDao followDao;
    private final MemberDao memberDao;

    public FollowServiceImpl(FollowDao followDao, MemberDao memberDao) {
        this.followDao = followDao;
        this.memberDao = memberDao;
    }

    @Override
    public void followMember(Long followerId, Long followingId) {
        Member follower = memberDao.findMemberById(followerId);
        Member following = memberDao.findMemberById(followingId);
        
        if (follower == null) {
            throw new KimprunException(
                KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION,
                "팔로워를 찾을 수 없습니다.",
                HttpStatus.NOT_FOUND,
                "FollowServiceImpl.followMember"
            );
        }
        
        if (following == null) {
            throw new KimprunException(
                KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION,
                "팔로우할 사용자를 찾을 수 없습니다.",
                HttpStatus.NOT_FOUND,
                "FollowServiceImpl.followMember"
            );
        }

        followDao.createFollow(follower, following);
    }

    @Override
    public void unfollowMember(Long followerId, Long followingId) {
        Member follower = memberDao.findMemberById(followerId);
        Member following = memberDao.findMemberById(followingId);
        
        if (follower == null) {
            throw new KimprunException(
                KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION,
                "팔로워를 찾을 수 없습니다.",
                HttpStatus.NOT_FOUND,
                "FollowServiceImpl.unfollowMember"
            );
        }
        
        if (following == null) {
            throw new KimprunException(
                KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION,
                "언팔로우할 사용자를 찾을 수 없습니다.",
                HttpStatus.NOT_FOUND,
                "FollowServiceImpl.unfollowMember"
            );
        }

        followDao.deleteFollow(follower, following);
    }

    @Override
    public Page<FollowResponse> getFollowers(Long memberId, int page, int size) {
        Member member = memberDao.findMemberById(memberId);
        if (member == null) {
            throw new KimprunException(
                KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION,
                "사용자를 찾을 수 없습니다.",
                HttpStatus.NOT_FOUND,
                "FollowServiceImpl.getFollowers"
            );
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Follow> followers = followDao.getFollowersByMember(member, pageable);
        
        return followers.map(follow -> new FollowResponse(
            follow.getFollower().getId(),
            follow.getFollower().getNickname(),
            follow.getFollower().getProfile() != null ? follow.getFollower().getProfile().getImageUrl() : null,
            follow.getRegistedAt()
        ));
    }

    @Override
    public Page<FollowResponse> getFollowing(Long memberId, int page, int size) {
        Member member = memberDao.findMemberById(memberId);
        if (member == null) {
            throw new KimprunException(
                KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION,
                "사용자를 찾을 수 없습니다.",
                HttpStatus.NOT_FOUND,
                "FollowServiceImpl.getFollowing"
            );
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Follow> following = followDao.getFollowingByMember(member, pageable);
        
        return following.map(follow -> new FollowResponse(
            follow.getFollowing().getId(),
            follow.getFollowing().getNickname(),
            follow.getFollowing().getProfile() != null ? follow.getFollowing().getProfile().getImageUrl() : null,
            follow.getRegistedAt()
        ));
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        Member follower = memberDao.findMemberById(followerId);
        Member following = memberDao.findMemberById(followingId);
        
        if (follower == null || following == null) {
            return false;
        }
        
        return followDao.isFollowing(follower, following);
    }
}