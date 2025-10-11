package kimp.community.vo;

import kimp.common.dto.request.PageRequestDto;

public class GetBoardsPageVo {

    private final Long categoryId;
    private final PageRequestDto pageRequestDto;

    public GetBoardsPageVo(Long categoryId, PageRequestDto pageRequestDto) {
        this.categoryId = categoryId;
        this.pageRequestDto = pageRequestDto;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public PageRequestDto getPageRequestDto() {
        return pageRequestDto;
    }
}
