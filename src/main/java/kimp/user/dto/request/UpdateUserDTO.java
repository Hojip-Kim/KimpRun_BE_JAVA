package kimp.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdateUserDTO {

    public String oldPassword;

    public String newPassword;


    public UpdateUserDTO(String oldPassword, String newPassword) {
        if(!isPasswordValid(oldPassword)){
            throw new IllegalArgumentException("Invalid password");
        }
        if(!isPasswordValid(newPassword)){
            throw new IllegalArgumentException("Invalid newPassword");
        }
        if(isSamePassword(oldPassword, newPassword)){
            throw new IllegalArgumentException("Passwords do not match");
        }
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    private  boolean isPasswordValid(String password){
        if(password.isEmpty() || password.isBlank()){
            return false;
        }
        return true;
    }

    private boolean isSamePassword(String oldPassword, String newPassword){
        String trimOldPassword = oldPassword.trim();
        String trimNewPassword = newPassword.trim();
        if(trimOldPassword.equals(trimNewPassword)){
            return true;
        }
        return false;
    }
}
