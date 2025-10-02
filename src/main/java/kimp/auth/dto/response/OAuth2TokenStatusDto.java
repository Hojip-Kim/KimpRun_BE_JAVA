package kimp.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class OAuth2TokenStatusDto {
    private boolean hasOAuth;
    private boolean isExpired;
    private boolean isExpiringSoon;
    private LocalDateTime expiresAt;
    private String provider;
    private boolean hasRefreshToken;
    private String message;

    // 간단한 에러 응답용 정적 팩토리 메서드
    public static OAuth2TokenStatusDto ofError(boolean hasOAuth, String message) {
        return OAuth2TokenStatusDto.builder()
                .hasOAuth(hasOAuth)
                .message(message)
                .isExpired(false)
                .isExpiringSoon(false)
                .expiresAt(null)
                .provider(null)
                .hasRefreshToken(false)
                .build();
    }
}