package kimp.user.enums;

import lombok.Getter;

@Getter
public enum Oauth {
    GOOGLE("google");

    private final String name;

    Oauth(String name) {
        this.name = name;
    }
}
