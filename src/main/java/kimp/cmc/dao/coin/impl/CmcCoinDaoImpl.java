package kimp.cmc.dao.coin.impl;

import kimp.cmc.dao.coin.CmcCoinDao;
import kimp.cmc.entity.coin.CmcCoin;
import kimp.cmc.entity.coin.CmcMainnet;
import kimp.cmc.entity.coin.CmcPlatform;
import kimp.cmc.repository.coin.CmcCoinRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CmcCoinDaoImpl implements CmcCoinDao {

    private final CmcCoinRepository cmcCoinRepository;

    public CmcCoinDaoImpl(CmcCoinRepository cmcCoinRepository) {
        this.cmcCoinRepository = cmcCoinRepository;
    }

    @Transactional
    public Boolean saveAllCmcCoin(List<CmcCoin> cmcCoinList) {
        List<CmcCoin> savedCmcCoinList = this.cmcCoinRepository.saveAll(cmcCoinList);

        if( savedCmcCoinList.size() != cmcCoinList.size()){
            throw new RuntimeException("cmcCoinList save Failed.");
        }else{
            return true;
        }
    }

    @Transactional
    public CmcCoin findCmcCoinById(Long id){
        return this.cmcCoinRepository.findById(id).orElse(null);
    }
    
    @Transactional(readOnly = true)
    public CmcCoin findCmcCoinByIdWithOneToOneRelations(Long id){
        return this.cmcCoinRepository.findByIdWithOneToOneRelations(id).orElse(null);
    }
    
    @Transactional(readOnly = true)
    public List<CmcMainnet> findMainnetsByCmcCoinId(Long cmcCoinId){
        return this.cmcCoinRepository.findMainnetsByCmcCoinId(cmcCoinId);
    }
    
    @Transactional(readOnly = true)
    public List<CmcPlatform> findPlatformsByCmcCoinId(Long cmcCoinId){
        return this.cmcCoinRepository.findPlatformsByCmcCoinId(cmcCoinId);
    }
}
