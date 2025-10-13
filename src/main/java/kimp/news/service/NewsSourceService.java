package kimp.news.service;

import kimp.news.dto.internal.NewsSourceDto;
import kimp.news.entity.News;

import java.util.List;

/**
 * 뉴스 소스별 서비스 인터페이스
 * 새로운 뉴스 소스 추가 시 이 인터페이스를 구현
 * @param <T> NewsSourceDto를 구현한 뉴스 소스 DTO 타입
 */
public interface NewsSourceService<T extends NewsSourceDto> {

    /**
     * 뉴스 소스 DTO로부터 News 엔티티 생성
     * @param newsSourceDto 뉴스 소스 DTO
     * @return News 엔티티
     */
    News createNewsFromSource(T newsSourceDto);

    /**
     * 뉴스 소스 DTO로 기존 News 엔티티 업데이트
     * @param existingNews 기존 News 엔티티
     * @param newsSourceDto 뉴스 소스 DTO
     * @return 업데이트된 News 엔티티
     */
    News updateNewsFromSource(News existingNews, T newsSourceDto);

    /**
     * 뉴스 소스 DTO 리스트로부터 News 엔티티 리스트 생성
     * @param newsSourceDtos 뉴스 소스 DTO 리스트
     * @return News 엔티티 리스트
     */
    List<News> createNewsListFromSource(List<T> newsSourceDtos);

    /**
     * 뉴스 소스 이름 반환
     * @return 뉴스 소스 이름
     */
    String getNewsSourceName();
}
