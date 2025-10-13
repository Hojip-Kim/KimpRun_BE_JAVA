package kimp.news.controller;

import kimp.exception.response.ApiResponse;
import kimp.news.dto.response.NewsResponseDto;
import kimp.news.enums.NewsSource;
import kimp.news.service.NewsFacadeService;
import kimp.news.vo.GetNewsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/news")
public class NewsController {

    private final NewsFacadeService newsFacadeService;

    public NewsController(NewsFacadeService newsFacadeService) {
        this.newsFacadeService = newsFacadeService;
    }

    @GetMapping
    public ApiResponse<Page<NewsResponseDto>> getAllNews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        GetNewsVo vo = GetNewsVo.builder()
                .page(page)
                .size(size)
                .build();

        Page<NewsResponseDto> responsePage = newsFacadeService.getAllNews(vo);

        return ApiResponse.success(responsePage);
    }

    @GetMapping("/type/{newsType}")
    public ApiResponse<Page<NewsResponseDto>> getNewsByType(
            @PathVariable String newsType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        GetNewsVo vo = GetNewsVo.builder()
                .newsType(newsType)
                .page(page)
                .size(size)
                .build();

        Page<NewsResponseDto> responsePage = newsFacadeService.getNewsByType(vo);

        return ApiResponse.success(responsePage);
    }

    @GetMapping("/headlines")
    public ApiResponse<Page<NewsResponseDto>> getHeadlines(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        GetNewsVo vo = GetNewsVo.builder()
                .page(page)
                .size(size)
                .build();

        Page<NewsResponseDto> responsePage = newsFacadeService.getHeadlines(vo);

        return ApiResponse.success(responsePage);
    }

    @GetMapping("/{id}")
    public ApiResponse<NewsResponseDto> getNewsById(@PathVariable Long id) {
        return newsFacadeService.getNewsById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Not Found", null));
    }

    @GetMapping("/source/{newsSource}")
    public ApiResponse<Page<NewsResponseDto>> getNewsByNewsSource(
            @PathVariable String newsSource,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        NewsSource newsSourceEnum = NewsSource.fromCode(newsSource);

        GetNewsVo vo = GetNewsVo.builder()
                .newsSource(newsSourceEnum)
                .page(page)
                .size(size)
                .build();

        Page<NewsResponseDto> responsePage = newsFacadeService.getNewsByNewsSource(vo);

        return ApiResponse.success(responsePage);
    }

    @GetMapping("/{newsSource}/{sourceSequenceId}")
    public ApiResponse<NewsResponseDto> getNewsByNewsSourceAndSourceSequenceId(
            @PathVariable String newsSource,
            @PathVariable Long sourceSequenceId) {

        NewsSource newsSourceEnum = NewsSource.fromCode(newsSource);

        return newsFacadeService.getNewsByNewsSourceAndSourceSequenceId(newsSourceEnum, sourceSequenceId)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Not Found", null));
    }
}
