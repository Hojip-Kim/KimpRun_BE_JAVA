package kimp.user.entity;

import jakarta.persistence.*;
import kimp.common.entity.TimeStamp;
import kimp.user.enums.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_role")
@NoArgsConstructor
@Getter
public class MemberRole extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "role_key", unique = true, nullable = false)
    private String roleKey;
    @Column(name = "role_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole roleName;

    public MemberRole(String roleKey, UserRole roleName) {
        this.roleKey = roleKey;
        this.roleName = roleName;
    }

    public MemberRole updateRole(UserRole role){
        this.roleName = role;
        return this;
    }
}
