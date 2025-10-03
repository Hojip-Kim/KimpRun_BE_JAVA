package kimp.scrap.dto.internal.coinone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CoinoneCreatedByDto {
    private String uuid;
    private int trading_level;
    private String nickname;
    private int comment_count;
    private int thread_count;
    private int vote_count;
    private int level;
    private String signature;
    private String user_type;
    private boolean is_blocked;
}
