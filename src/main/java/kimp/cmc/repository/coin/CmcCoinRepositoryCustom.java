package kimp.cmc.repository.coin;

import kimp.cmc.entity.coin.CmcCoin;
import kimp.cmc.entity.coin.CmcMainnet;
import kimp.cmc.entity.coin.CmcPlatform;

import java.util.List;
import java.util.Optional;

public interface CmcCoinRepositoryCustom {
    
    /**
     * CmcCoin을 ID로 조회하면서 OneToOne 관계의 연관 엔티티를 fetch join으로 가져옴
     * CmcRank, CmcCoinInfo, CmcCoinMeta를 한번에 fetch join
     * 
     */
    Optional<CmcCoin> findByIdWithOneToOneRelations(Long coinId);
    
    /**
     * cmcCoinId로 CmcMainnet 목록을 조회
     * 
     */
    List<CmcMainnet> findMainnetsByCmcCoinId(Long cmcCoinId);
    
    /**
     * cmcCoinId로 CmcPlatform 목록을 조회
     * 
     */
    List<CmcPlatform> findPlatformsByCmcCoinId(Long cmcCoinId);
}