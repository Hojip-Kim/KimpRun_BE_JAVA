package kimp.news.component.impl;

import kimp.news.component.NewsComponent;
import kimp.news.dto.internal.bloomingbit.BloomingBitNewsDto;
import kimp.news.dto.internal.bloomingbit.BloomingBitResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class BloomingBitComponent implements NewsComponent<BloomingBitNewsDto> {

    private final RestClient restClient;

    @Value("${bloomingbit.api.url:https://v3-gtw.bloomingbit.io/postbox/v1/news/rank/list}")
    private String apiUrl;

    @Value("${bloomingbit.api.limit:20}")
    private Integer limit;

    @Value("${bloomingbit.api.locale:ko}")
    private String locale;

    public BloomingBitComponent(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public List<BloomingBitNewsDto> fetchNews() {
        try {
            String url = String.format("%s?limit=%d&locale=%s", apiUrl, limit, locale);
            log.info("블루밍비트 API에서 뉴스 조회 시작: {}", url);

            BloomingBitResponseDto responseDto = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(BloomingBitResponseDto.class);

            if (responseDto == null || responseDto.getData() == null || responseDto.getData().getContent() == null) {
                log.warn("블루밍비트 API 응답 구조가 올바르지 않음");
                return Collections.emptyList();
            }

            log.info("블루밍비트에서 {}건의 뉴스 조회 완료", responseDto.getData().getContent().size());
            return responseDto.getData().getContent();

        } catch (Exception e) {
            log.error("블루밍비트 API 뉴스 조회 중 오류 발생", e);
            return Collections.emptyList();
        }
    }

    public List<BloomingBitNewsDto> fetchNewsWithOffset(int offset) {
        try {
            String url = String.format("%s?limit=%d&offset=%d&locale=%s", apiUrl, limit, offset, locale);
            log.info("블루밍비트 API에서 오프셋 {}로 뉴스 조회 시작: {}", offset, url);

            BloomingBitResponseDto responseDto = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(BloomingBitResponseDto.class);

            if (responseDto == null || responseDto.getData() == null || responseDto.getData().getContent() == null) {
                log.warn("블루밍비트 API 응답 구조가 올바르지 않음");
                return Collections.emptyList();
            }

            log.info("블루밍비트에서 오프셋 {}로 {}건의 뉴스 조회 완료",
                    offset, responseDto.getData().getContent().size());
            return responseDto.getData().getContent();

        } catch (Exception e) {
            log.error("블루밍비트 API 오프셋 {} 뉴스 조회 중 오류 발생", offset, e);
            return Collections.emptyList();
        }
    }

    @Override
    public String getNewsSource() {
        return "BloomingBit";
    }

    @Override
    public String getApiUrl() {
        return this.apiUrl;
    }
}
