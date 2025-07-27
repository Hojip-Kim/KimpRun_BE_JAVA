package kimp.cmc.service.impl;

import kimp.cmc.component.CoinMarketCapComponent;
import kimp.cmc.dao.exchange.CmcExchangeDao;
import kimp.cmc.dao.exchange.CmcExchangeInfoDao;
import kimp.cmc.dao.exchange.CmcExchangeMetaDao;
import kimp.cmc.dao.exchange.CmcExchangeUrlDao;
import kimp.cmc.service.CmcExchangeService;
import org.springframework.stereotype.Service;

@Service
public class CmcExchangeServiceImpl implements CmcExchangeService {
    private final CoinMarketCapComponent coinMarketCapComponent;

    private final CmcExchangeDao cmcExchangeDao;
    private final CmcExchangeInfoDao cmcExchangeInfoDao;
    private final CmcExchangeMetaDao cmcExchangeMetaDao;
    private final CmcExchangeUrlDao cmcExchangeUrlDao;

    public CmcExchangeServiceImpl(CoinMarketCapComponent coinMarketCapComponent, CmcExchangeDao cmcExchangeDao, CmcExchangeInfoDao cmcExchangeInfoDao, CmcExchangeMetaDao cmcExchangeMetaDao, CmcExchangeUrlDao cmcExchangeUrlDao) {
        this.coinMarketCapComponent = coinMarketCapComponent;
        this.cmcExchangeDao = cmcExchangeDao;
        this.cmcExchangeInfoDao = cmcExchangeInfoDao;
        this.cmcExchangeMetaDao = cmcExchangeMetaDao;
        this.cmcExchangeUrlDao = cmcExchangeUrlDao;
    }
}
