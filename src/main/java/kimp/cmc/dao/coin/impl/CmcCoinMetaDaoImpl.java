package kimp.cmc.dao.coin.impl;

import kimp.cmc.dao.coin.CmcCoinMetaDao;
import kimp.cmc.repository.coin.CmcCoinMetaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CmcCoinMetaDaoImpl implements CmcCoinMetaDao {
    private CmcCoinMetaRepository cmcCoinMetaRepository;

    public CmcCoinMetaDaoImpl(CmcCoinMetaRepository cmcCoinMetaRepository) {
        this.cmcCoinMetaRepository = cmcCoinMetaRepository;
    }


}
