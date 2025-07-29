package kimp.cmc.dao.coin.impl;

import kimp.cmc.dao.coin.CmcMainnetDao;
import kimp.cmc.repository.coin.CmcMainnetRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CmcMainnetDaoImpl implements CmcMainnetDao {

    private final CmcMainnetRepository cmcMainnetRepository;

    public CmcMainnetDaoImpl(CmcMainnetRepository cmcMainnetRepository) {
        this.cmcMainnetRepository = cmcMainnetRepository;
    }
}
