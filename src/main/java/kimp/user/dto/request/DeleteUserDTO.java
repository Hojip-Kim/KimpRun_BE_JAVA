package kimp.user.dto.request;

import lombok.Getter;

@Getter
public class DeleteUserDTO {

    public long userId;

    public DeleteUserDTO(long userId) {
        this.userId = userId;
    }


}
