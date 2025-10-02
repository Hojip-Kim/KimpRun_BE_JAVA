package kimp.community.vo;

import kimp.community.dto.board.request.UpdateBoardRequestDto;

public class UpdateBoardVo {

    private final long memberId;
    private final long boardId;
    private final UpdateBoardRequestDto updateBoardRequestDto;

    public UpdateBoardVo(long memberId, long boardId, UpdateBoardRequestDto updateBoardRequestDto) {
        this.memberId = memberId;
        this.boardId = boardId;
        this.updateBoardRequestDto = updateBoardRequestDto;
    }

    public long getMemberId() {
        return memberId;
    }

    public long getBoardId() {
        return boardId;
    }

    public UpdateBoardRequestDto getUpdateBoardRequestDto() {
        return updateBoardRequestDto;
    }
}
