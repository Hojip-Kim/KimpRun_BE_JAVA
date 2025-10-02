package kimp.community.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DeactivateBoardsPinVo {
    private List<Long> boardIds;
}
