package kimp.user.dto;

import lombok.Getter;

@Getter
public class UserWithIdNameEmailDto {
    public String email;
    public String name;
    public String role;

    public UserWithIdNameEmailDto(String name) {
        this.role = null;
        this.email = null;
        this.name = name;
    }

    public UserWithIdNameEmailDto(String email, String name, String role) {
        if(email == null || email.isEmpty()){
            this.email = "";
        }else{
            this.email = email;
        }
        this.name = name;
        this.role = role;
    }

}
