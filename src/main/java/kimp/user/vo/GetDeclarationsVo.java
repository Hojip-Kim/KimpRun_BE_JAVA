package kimp.user.vo;

import org.springframework.data.domain.Pageable;

public class GetDeclarationsVo {

    private final Pageable pageable;

    public GetDeclarationsVo(Pageable pageable) {
        this.pageable = pageable;
    }

    public Pageable getPageable() {
        return pageable;
    }
}
