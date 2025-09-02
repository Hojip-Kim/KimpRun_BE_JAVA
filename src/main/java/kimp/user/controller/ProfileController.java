package kimp.user.controller;

import kimp.exception.response.ApiResponse;
import kimp.security.user.CustomUserDetails;
import kimp.user.dto.request.FollowRequest;
import kimp.user.dto.response.FollowResponse;
import kimp.user.dto.response.ProfileInfoResponse;
import kimp.user.service.FollowService;
import kimp.user.service.ProfileService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final FollowService followService;

    public ProfileController(ProfileService profileService, FollowService followService) {
        this.profileService = profileService;
        this.followService = followService;
    }

    @GetMapping("/{memberId}")
    public ApiResponse<ProfileInfoResponse> getProfileInfo(@PathVariable Long memberId) {
        ProfileInfoResponse profile = profileService.getProfileInfo(memberId);
        return ApiResponse.success(profile);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR', 'INFLUENCER', 'USER')")
    @PostMapping("/follow")
    public ApiResponse<String> followMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FollowRequest request) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        followService.followMember(customUserDetails.getId(), request.getFollowingId());
        return ApiResponse.success("팔로우 완료");
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR', 'INFLUENCER', 'USER')")
    @DeleteMapping("/follow/{followingId}")
    public ApiResponse<String> unfollowMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long followingId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        followService.unfollowMember(customUserDetails.getId(), followingId);
        return ApiResponse.success("언팔로우 완료");
    }

    @GetMapping("/{memberId}/followers")
    public ApiResponse<Page<FollowResponse>> getFollowers(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Page<FollowResponse> followers = followService.getFollowers(memberId, page, size);
        return ApiResponse.success(followers);
    }

    @GetMapping("/{memberId}/following")
    public ApiResponse<Page<FollowResponse>> getFollowing(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Page<FollowResponse> following = followService.getFollowing(memberId, page, size);
        return ApiResponse.success(following);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR', 'INFLUENCER', 'USER')")
    @GetMapping("/follow-status/{followingId}")
    public ApiResponse<Boolean> getFollowStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long followingId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        boolean isFollowing = followService.isFollowing(customUserDetails.getId(), followingId);
        return ApiResponse.success(isFollowing);
    }
}
