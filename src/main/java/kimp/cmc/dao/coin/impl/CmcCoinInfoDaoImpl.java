package kimp.cmc.dao.coin.impl;

import kimp.cmc.dao.coin.CmcCoinInfoDao;
import kimp.cmc.repository.coin.CmcCoinInfoRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CmcCoinInfoDaoImpl implements CmcCoinInfoDao {

    private final CmcCoinInfoRepository cmcCoinInfoRepository;

    public CmcCoinInfoDaoImpl(CmcCoinInfoRepository cmcCoinInfoRepository) {
        this.cmcCoinInfoRepository = cmcCoinInfoRepository;
    }
}
