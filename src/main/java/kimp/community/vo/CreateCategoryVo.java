package kimp.community.vo;

import kimp.community.dto.category.request.CreateCategoryRequestDto;

public class CreateCategoryVo {

    private final long memberId;
    private final CreateCategoryRequestDto createCategoryRequestDto;

    public CreateCategoryVo(long memberId, CreateCategoryRequestDto createCategoryRequestDto) {
        this.memberId = memberId;
        this.createCategoryRequestDto = createCategoryRequestDto;
    }

    public long getMemberId() {
        return memberId;
    }

    public CreateCategoryRequestDto getCreateCategoryRequestDto() {
        return createCategoryRequestDto;
    }
}
