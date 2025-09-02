package kimp.user.dao;

import kimp.user.entity.Follow;
import kimp.user.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowDao {
    Follow createFollow(Member follower, Member following);
    void deleteFollow(Member follower, Member following);
    Page<Follow> getFollowersByMember(Member member, Pageable pageable);
    Page<Follow> getFollowingByMember(Member member, Pageable pageable);
    boolean isFollowing(Member follower, Member following);
    long getFollowerCount(Member member);
    long getFollowingCount(Member member);
}