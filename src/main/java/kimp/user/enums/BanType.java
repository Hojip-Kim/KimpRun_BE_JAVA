package kimp.user.enums;

import lombok.Getter;

@Getter
public enum BanType {
    APPLICATION("application"),
    CDN("cdn");

    private final String name;

    BanType(String name) {
        this.name = name;
    }
}
