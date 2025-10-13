package kimp.news.component;

import kimp.news.dto.internal.NewsSourceDto;

import java.util.List;

/**
 * 뉴스 소스 컴포넌트 인터페이스
 * 새로운 뉴스 소스 추가 시 이 인터페이스를 구현
 */
public interface NewsComponent<T extends NewsSourceDto> {

    /**
     * 뉴스 API에서 데이터 가져오기
     * @return 뉴스 DTO 리스트
     */
    List<T> fetchNews();

    /**
     * offset을 사용하여 페이지네이션된 뉴스 가져오기
     * @param offset 페이지 오프셋
     * @return 뉴스 DTO 리스트
     */
    List<T> fetchNewsWithOffset(int offset);

    /**
     * 뉴스 소스 이름 반환
     * @return 뉴스 소스 이름 (예: "BloomingBit", "CoinDesk", etc.)
     */
    String getNewsSource();

    /**
     * API URL 반환
     * @return API URL
     */
    String getApiUrl();
}
