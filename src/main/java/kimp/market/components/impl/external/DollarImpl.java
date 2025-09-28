package kimp.market.components.impl.external;

import kimp.market.components.Dollar;
import kimp.market.dto.market.response.DollarResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 달러 환율 정보를 조회하는 컴포넌트 구현체
 */
@Component
@Slf4j
public class DollarImpl implements Dollar {

    private final RestClient restClient;

    @Value("${dollar.api.url}")
    private String dollarUrl;

    public DollarImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * API를 통해 현재 달러/원 환율을 조회
     * 
     * @return 현재 달러/원 환율
     */
    @Override
    public double getApiDollar() {
        log.info("dollar 정보 업데이트 성공");
        DollarResponseDto dollarDto = restClient.get()
                .uri(dollarUrl)
                .retrieve()
                .body(DollarResponseDto.class);

        if(dollarDto == null){
            throw new IllegalCallerException("Dollar Data is null.");
        }
        return dollarDto.getRates().getKRW();
    }
}