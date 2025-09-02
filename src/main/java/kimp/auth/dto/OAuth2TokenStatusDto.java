package kimp.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OAuth2TokenStatusDto {
    private boolean hasOAuth;
    private boolean isExpired;
    private boolean isExpiringSoon;
    private LocalDateTime expiresAt;
    private String provider;
    private boolean hasRefreshToken;
    private String message;

    public OAuth2TokenStatusDto(boolean hasOAuth, String message) {
        this.hasOAuth = hasOAuth;
        this.message = message;
        this.isExpired = false;
        this.isExpiringSoon = false;
        this.expiresAt = null;
        this.provider = null;
        this.hasRefreshToken = false;
    }
}