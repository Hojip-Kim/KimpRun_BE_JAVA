package kimp.cmc.dao.coin.impl;

import kimp.cmc.dao.coin.CmcRankDao;
import kimp.cmc.repository.coin.CmcRankRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CmcRankDaoImpl implements CmcRankDao {

    private final CmcRankRepository cmcRankRepository;

    public CmcRankDaoImpl(CmcRankRepository cmcRankRepository) {
        this.cmcRankRepository = cmcRankRepository;
    }

}
