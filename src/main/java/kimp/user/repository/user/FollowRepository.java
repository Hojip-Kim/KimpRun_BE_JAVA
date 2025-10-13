package kimp.user.repository.user;

import kimp.user.entity.Follow;
import kimp.user.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    
    Optional<Follow> findByFollowerAndFollowing(Member follower, Member following);
    
    @Query("SELECT f FROM Follow f JOIN FETCH f.following WHERE f.follower = :follower")
    Page<Follow> findFollowingByFollower(@Param("follower") Member follower, Pageable pageable);
    
    @Query("SELECT f FROM Follow f JOIN FETCH f.follower WHERE f.following = :following")
    Page<Follow> findFollowersByFollowing(@Param("following") Member following, Pageable pageable);
    
    long countByFollower(Member follower);
    
    long countByFollowing(Member following);
    
    boolean existsByFollowerAndFollowing(Member follower, Member following);
}