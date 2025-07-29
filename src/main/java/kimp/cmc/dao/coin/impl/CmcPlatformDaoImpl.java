package kimp.cmc.dao.coin.impl;

import kimp.cmc.dao.coin.CmcPlatformDao;
import kimp.cmc.repository.coin.CmcPlatformRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CmcPlatformDaoImpl implements CmcPlatformDao {

    private final CmcPlatformRepository cmcPlatformRepository;

    public CmcPlatformDaoImpl(CmcPlatformRepository cmcPlatformRepository) {
        this.cmcPlatformRepository = cmcPlatformRepository;
    }
}
