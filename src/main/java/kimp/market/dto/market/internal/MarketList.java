package kimp.market.dto.market.internal;

import kimp.market.dto.coin.internal.crypto.CryptoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class MarketList<T extends CryptoDto> {

    List<T> cryptoDtoList;

    public MarketList(List<T> cryptoDtoList) {
        this.cryptoDtoList = cryptoDtoList;
    }

    // currency(통화)를 제외한 데이터만 return합니다.
    public List<String> getPairList() {
        return new ArrayList<>(cryptoDtoList.stream().map(CryptoDto::getName).collect(Collectors.toList()));
    }

    // currency(통화)를 제외한 데이터를 lower-case로 return합니다.
    public List<String> getLowerPairList() {
        return new ArrayList<>(cryptoDtoList.stream().map(data -> data.getName().toLowerCase()).collect(Collectors.toList()));
    }

    // currnecy(통화)를 포함한 데이터만 return합니다.
    // 예 : BTCUSDT / BTCKRW
    public List<String> getCryptoList() {
        return new ArrayList<>(cryptoDtoList.stream().map(data -> data.getFullName()).collect(Collectors.toList()));
    }

    public List<T> getCryptoDtoList() {
        return cryptoDtoList;
    }

    public List<String> getKrCryptoNameList() {
        return new ArrayList<>(cryptoDtoList.stream().map(data -> data.getCurrency() + "-" + data.getName()).collect(Collectors.toList()));
    }
}
