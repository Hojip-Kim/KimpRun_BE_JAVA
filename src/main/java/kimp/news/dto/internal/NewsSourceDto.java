package kimp.news.dto.internal;

/**
 * 모든 뉴스 소스 DTO의 마커 인터페이스
 * 새로운 뉴스 소스 DTO는 이 인터페이스를 구현해야 함
 */
public interface NewsSourceDto {

    /**
     * 뉴스의 고유 식별자 반환 (뉴스 소스에서 제공하는 ID)
     * @return 뉴스 고유 ID
     */
    Long getSourceSequenceId();

    /**
     * 뉴스 제목 반환
     * @return 뉴스 제목
     */
    String getTitle();

    /**
     * 뉴스 원본 URL 반환
     * @return 원본 URL
     */
    String getSourceUrl();

    /**
     * 뉴스 생성 시각 (epoch 밀리초)
     * @return 생성 시각
     */
    Long getCreateEpochMillis();
}
