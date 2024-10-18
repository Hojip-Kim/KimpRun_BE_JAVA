package kimp.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeleteUserDTO {

    public String password;


    public DeleteUserDTO(String password) {
        if(!isPasswordValid(password)){
            throw new IllegalArgumentException("Invalid password");
        }
        this.password = password;
    }

    private  boolean isPasswordValid(String password){
        if(password.isEmpty() || password.isBlank()){
            return false;
        }
        return true;
    }
}
