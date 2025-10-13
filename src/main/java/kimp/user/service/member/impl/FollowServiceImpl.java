package kimp.user.service.member.impl;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.FollowDao;
import kimp.user.dao.MemberDao;
import kimp.user.dto.response.FollowResponse;
import kimp.user.entity.Follow;
import kimp.user.entity.Member;
import kimp.user.service.member.FollowService;
import kimp.user.vo.*;
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
    public void followMember(FollowMemberVo vo) {
        Member follower = memberDao.findMemberById(vo.getFollowerId());
        Member following = memberDao.findMemberById(vo.getFollowingId());
        
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
    public void unfollowMember(FollowMemberVo vo) {
        Member follower = memberDao.findMemberById(vo.getFollowerId());
        Member following = memberDao.findMemberById(vo.getFollowingId());
        
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
    public Page<FollowResponse> getFollowers(GetFollowersVo vo) {
        Member member = memberDao.findMemberById(vo.getMemberId());
        if (member == null) {
            throw new KimprunException(
                KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION,
                "사용자를 찾을 수 없습니다.",
                HttpStatus.NOT_FOUND,
                "FollowServiceImpl.getFollowers"
            );
        }

        Pageable pageable = PageRequest.of(vo.getPage(), vo.getSize());
        Page<Follow> followers = followDao.getFollowersByMember(member, pageable);

        return followers.map(follow -> FollowResponse.builder()
                .memberId(follow.getFollower().getId())
                .nickname(follow.getFollower().getNickname())
                .profileImageUrl(follow.getFollower().getProfile() != null ? follow.getFollower().getProfile().getImageUrl() : null)
                .followedAt(follow.getRegistedAt())
                .build());
    }

    @Override
    public Page<FollowResponse> getFollowing(GetFollowingVo vo) {
        Member member = memberDao.findMemberById(vo.getMemberId());
        if (member == null) {
            throw new KimprunException(
                KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION,
                "사용자를 찾을 수 없습니다.",
                HttpStatus.NOT_FOUND,
                "FollowServiceImpl.getFollowing"
            );
        }

        Pageable pageable = PageRequest.of(vo.getPage(), vo.getSize());
        Page<Follow> following = followDao.getFollowingByMember(member, pageable);

        return following.map(follow -> FollowResponse.builder()
                .memberId(follow.getFollowing().getId())
                .nickname(follow.getFollowing().getNickname())
                .profileImageUrl(follow.getFollowing().getProfile() != null ? follow.getFollowing().getProfile().getImageUrl() : null)
                .followedAt(follow.getRegistedAt())
                .build());
    }

    @Override
    public boolean isFollowing(GetFollowStatusVo vo) {
        Member follower = memberDao.findMemberById(vo.getFollowerId());
        Member following = memberDao.findMemberById(vo.getFollowingId());
        
        if (follower == null || following == null) {
            return false;
        }
        
        return followDao.isFollowing(follower, following);
    }
}