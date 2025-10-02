package kimp.community.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ActivateBoardsPinVo {
    private List<Long> boardIds;
}
