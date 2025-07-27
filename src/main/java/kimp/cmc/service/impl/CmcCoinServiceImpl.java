package kimp.cmc.service.impl;

import kimp.cmc.entity.coin.CmcCoin;
import kimp.cmc.service.CmcCoinService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CmcCoinServiceImpl implements CmcCoinService {
    @Override
    public List<CmcCoin> getAllCmcCoin() {
        return List.of();
    }
}
