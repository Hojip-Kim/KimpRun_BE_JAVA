package kimp.cmc.service.impl;

import kimp.cmc.component.CoinMarketCapComponent;
import kimp.cmc.dao.exchange.CmcExchangeDao;
import kimp.cmc.dao.exchange.CmcExchangeInfoDao;
import kimp.cmc.dao.exchange.CmcExchangeMetaDao;
import kimp.cmc.dao.exchange.CmcExchangeUrlDao;
import kimp.cmc.dto.common.exchange.CmcExchangeDetailMapDto;
import kimp.cmc.dto.common.exchange.CmcExchangeDto;
import kimp.cmc.service.CmcExchangeManageService;
import kimp.exchange.service.ExchangeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CmcExchangeManageServiceImpl implements CmcExchangeManageService {

    private final CoinMarketCapComponent coinMarketCapComponent;
    private final ExchangeService exchangeService;

    private final CmcExchangeDao cmcExchangeDao;
    private final CmcExchangeInfoDao cmcExchangeInfoDao;
    private final CmcExchangeMetaDao cmcExchangeMetaDao;
    private final CmcExchangeUrlDao cmcExchangeUrlDao;

    public CmcExchangeManageServiceImpl(CoinMarketCapComponent coinMarketCapComponent, ExchangeService exchangeService, CmcExchangeDao cmcExchangeDao, CmcExchangeInfoDao cmcExchangeInfoDao, CmcExchangeMetaDao cmcExchangeMetaDao, CmcExchangeUrlDao cmcExchangeUrlDao) {
        this.coinMarketCapComponent = coinMarketCapComponent;
        this.exchangeService = exchangeService;
        this.cmcExchangeDao = cmcExchangeDao;
        this.cmcExchangeInfoDao = cmcExchangeInfoDao;
        this.cmcExchangeMetaDao = cmcExchangeMetaDao;
        this.cmcExchangeUrlDao = cmcExchangeUrlDao;
    }

    private List<CmcExchangeDto> cmcExchangeDtoList = new ArrayList<>();
    private CmcExchangeDetailMapDto cmcExchangeDetailMapDto;

    public void setCmcExchangeList() {

        for(int i = 0; i < 2; i++){
            List<CmcExchangeDto> broughtCmcExchangeDtos = coinMarketCapComponent.getExchangeMap(i*5000 + 1, 5000);
            cmcExchangeDtoList.addAll(broughtCmcExchangeDtos);
        }
    }

    public void setCmcExchangeDetailMapDto() {

    }



}
