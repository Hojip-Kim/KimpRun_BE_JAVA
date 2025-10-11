package kimp.user.vo;

public class GetFollowStatusVo {

    private final Long followerId;
    private final Long followingId;

    public GetFollowStatusVo(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public Long getFollowingId() {
        return followingId;
    }
}
