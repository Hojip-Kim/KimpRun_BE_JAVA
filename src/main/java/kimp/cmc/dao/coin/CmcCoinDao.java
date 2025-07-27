package kimp.cmc.dao.coin;

import kimp.cmc.entity.coin.CmcCoin;

import java.util.List;

public interface CmcCoinDao {

    public Boolean saveAllCmcCoin(List<CmcCoin> cmcCoinList);
}
