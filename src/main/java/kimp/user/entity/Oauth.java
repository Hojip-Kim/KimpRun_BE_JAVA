package kimp.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.user.entity.Member;
import lombok.Getter;

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
}
