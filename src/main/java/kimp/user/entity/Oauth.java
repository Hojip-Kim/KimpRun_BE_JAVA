package kimp.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "oauth")
@Getter
public class Oauth extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name= "refresh_token")
    @JsonIgnore
    private String refreshToken;

    @Column(name = "token_type")
    private String tokenType;

    @Column(name = "expires_in")
    private Long expiresIn;

    @Column(name = "scope")
    private String scope;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public Oauth() {
    }

    public Oauth(Member member, String provider, String providerId) {
        this.member = member;
        this.provider = provider;
        this.providerId = providerId;
    }

    public Oauth setMember(Member member){
        this.member = member;
        if (member.getOauth() != this) { // member간 무한재귀 호출 방지
            member.setOauth(this);
        }
        return this;
    }

    public Oauth setProvider(String provider){
        this.provider = provider;
        return this;
    }

    public Oauth setProviderId(String providerId){
        this.providerId = providerId;
        return this;
    }

    public Oauth setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
    public Oauth setRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
        return this;
    }

    public Oauth setTokenType(String tokenType) {
        this.tokenType = tokenType;
        return this;
    }

    public Oauth setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public Oauth setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public Oauth setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }
}
