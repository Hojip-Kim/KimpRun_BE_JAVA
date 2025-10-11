package kimp.news.service;

import kimp.news.dto.response.NewsResponseDto;
import kimp.news.enums.NewsSource;
import kimp.news.vo.GetNewsVo;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface NewsFacadeService {

    Page<NewsResponseDto> getAllNews(GetNewsVo vo);

    Page<NewsResponseDto> getNewsByNewsSource(GetNewsVo vo);

    Page<NewsResponseDto> getNewsByType(GetNewsVo vo);

    Page<NewsResponseDto> getHeadlines(GetNewsVo vo);

    Optional<NewsResponseDto> getNewsById(Long id);

    Optional<NewsResponseDto> getNewsByNewsSourceAndSourceSequenceId(NewsSource newsSource, Long seq);
}
