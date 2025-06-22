package kimp.market.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MarketCommonMethod {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MarketCommonMethod(RestTemplate restTemplate, ObjectMapper objectMapper){
        this.restTemplate =restTemplate;
        this.objectMapper = objectMapper;
    }


    /**
     * @param url : 데이터를 원하는 url (외부 API)
     * @param startWith : 파라미터로 보내진 DTO의 메서드 필드에서 해당 string으로 시작하는 값들의
     * @param method : startWith의 대상이 되는 Dto의 method(필드)
     * @param dtoClass : 원하는 url에따른 dto클래스가 다르며, 필드도 다르므로 Generic형식으로 dto class List 추가
     *
     * @return : 파라미터로 넣어준 인자 dto 클래스의 List형식으로 데이터 반환
     */
    public <T> List<String> getMarketListByURLAndStartWith(String url, String startWith, String method,  Class<T[]> dtoClass) throws IOException {
        String data = restTemplate.getForObject(url, String.class);

        T[] marketData = objectMapper.readValue(data, dtoClass);

        Class<?> componentType = dtoClass.getComponentType();
        List<String> list = Arrays.stream(marketData)
                .map(dto -> {
                    try {
                        return (String)componentType.getMethod(method).invoke(dto);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to invoke method : " + method + " on DTO : " + dtoClass, e);
                    }
                })
                .filter(market -> market != null && market.startsWith(startWith))
                .collect(Collectors.toList());

        return new ArrayList<>(list);
    }

    /**
     * @param url : 데이터를 원하는 url (외부 API)
     * @param endWith : 파라미터로 보내진 DTO의 메서드 필드에서 해당 string으로 시작하는 값들의
     * @param method : endWith의 대상이 되는 Dto의 method(필드)
     * @param dtoClass : 원하는 url에따른 dto클래스가 다르며, 필드도 다르므로 Generic형식으로 dto class List 추가
     *
     * @return : 파라미터로 넣어준 인자 dto 클래스의 List형식으로 데이터 반환
     */
    public <T> List<String> getMarketListByURLAndEndWith(String url, String endWith, String method,  Class<T[]> dtoClass) throws IOException {
        String data = restTemplate.getForObject(url, String.class);

        T[] marketData = objectMapper.readValue(data, dtoClass);

        Class<?> componentType = dtoClass.getComponentType();
        return Arrays.stream(marketData)
                .map(dto -> {
                    try {
                        return (String)componentType.getMethod(method).invoke(dto);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to invoke method : " + method + " on DTO : " + dtoClass, e);
                    }
                })
                .filter(market -> market != null && market.endsWith(endWith))
                .collect(Collectors.toList());
    }

    public <T> T getMarketByURLAndStartWith(String url, String startWith, String method,  Class<T> dtoClass) throws IOException {
        String data = restTemplate.getForObject(url, String.class);

        return objectMapper.readValue(data, dtoClass);
    }

    public static BigDecimal setScale(BigDecimal input) {
        if(input == null){
            return BigDecimal.ZERO;
        }
        return input.setScale(7, RoundingMode.HALF_UP);
    }



}
