package kimp.cmc.dao.coin;

import kimp.cmc.entity.coin.CmcCoin;
import kimp.cmc.entity.coin.CmcMainnet;
import kimp.cmc.entity.coin.CmcPlatform;

import java.util.List;

public interface CmcCoinDao {

    public Boolean saveAllCmcCoin(List<CmcCoin> cmcCoinList);

    public CmcCoin findCmcCoinById(Long id);
    
    public CmcCoin findCmcCoinByIdWithOneToOneRelations(Long id);
    
    public List<CmcMainnet> findMainnetsByCmcCoinId(Long cmcCoinId);
    
    public List<CmcPlatform> findPlatformsByCmcCoinId(Long cmcCoinId);
    
    public List<CmcMainnet> findMainnetsByCmcCoinIds(List<Long> cmcCoinIds);
    
    public List<CmcPlatform> findPlatformsByCmcCoinIds(List<Long> cmcCoinIds);
}
