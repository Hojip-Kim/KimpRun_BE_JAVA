package kimp.community.vo;

import kimp.community.dto.board.request.CreateBoardRequestDto;

public class CreateBoardVo {

    private final long memberId;
    private final long categoryId;
    private final CreateBoardRequestDto createBoardRequestDto;

    public CreateBoardVo(long memberId, long categoryId, CreateBoardRequestDto createBoardRequestDto) {
        this.memberId = memberId;
        this.categoryId = categoryId;
        this.createBoardRequestDto = createBoardRequestDto;
    }

    public long getMemberId() {
        return memberId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public CreateBoardRequestDto getCreateBoardRequestDto() {
        return createBoardRequestDto;
    }
}
