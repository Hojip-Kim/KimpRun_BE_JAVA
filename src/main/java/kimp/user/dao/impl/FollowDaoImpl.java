package kimp.user.dao.impl;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.user.dao.FollowDao;
import kimp.user.entity.Follow;
import kimp.user.entity.Member;
import kimp.user.repository.FollowRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FollowDaoImpl implements FollowDao {

    private final FollowRepository followRepository;

    public FollowDaoImpl(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    @Transactional
    public Follow createFollow(Member follower, Member following) {
        if (follower.getId().equals(following.getId())) {
            throw new KimprunException(
                KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION,
                "자기 자신을 팔로우할 수 없습니다.",
                HttpStatus.BAD_REQUEST,
                "FollowDaoImpl.createFollow"
            );
        }

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new KimprunException(
                KimprunExceptionEnum.INVALID_REQUEST_EXCEPTION,
                "이미 팔로우하고 있습니다.",
                HttpStatus.BAD_REQUEST,
                "FollowDaoImpl.createFollow"
            );
        }

        Follow follow = new Follow(follower, following);
        return followRepository.save(follow);
    }

    @Override
    @Transactional
    public void deleteFollow(Member follower, Member following) {
        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
            .orElseThrow(() -> new KimprunException(
                KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION,
                "팔로우 관계를 찾을 수 없습니다.",
                HttpStatus.NOT_FOUND,
                "FollowDaoImpl.deleteFollow"
            ));
        
        followRepository.delete(follow);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Follow> getFollowersByMember(Member member, Pageable pageable) {
        return followRepository.findFollowersByFollowing(member, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Follow> getFollowingByMember(Member member, Pageable pageable) {
        return followRepository.findFollowingByFollower(member, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Member follower, Member following) {
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }

    @Override
    @Transactional(readOnly = true)
    public long getFollowerCount(Member member) {
        return followRepository.countByFollowing(member);
    }

    @Override
    @Transactional(readOnly = true)
    public long getFollowingCount(Member member) {
        return followRepository.countByFollower(member);
    }
}