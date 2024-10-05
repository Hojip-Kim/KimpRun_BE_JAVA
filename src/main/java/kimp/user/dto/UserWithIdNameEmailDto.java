package kimp.user.dto;

import lombok.Getter;

@Getter
public class UserWithIdNameEmailDto {
    public Long id;
    public String email;
    public String name;

    public UserWithIdNameEmailDto(Long id, String email, String name) {
        this.id = id;
        if(email == null || email.isEmpty()){
            this.email = "";
        }else{
            this.email = email;
        }
        this.name = name;
    }

}
