package kimp.community.service;

import kimp.community.dto.category.response.CategoryDto;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.entity.Category;
import kimp.user.entity.Member;
import kimp.user.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryPacadeService {
    private final CategoryService categoryService;
    private final MemberService memberService;

    public CategoryPacadeService(CategoryService categoryService, MemberService memberService) {
        this.categoryService = categoryService;
        this.memberService = memberService;
    }

    @Transactional
    public Category createCategory(long memberId, CreateCategoryRequestDto categoryDto){
        Category category = categoryService.createCategory(categoryDto);
        Member member = memberService.getMemberEntityById(memberId);
        return category.setMember(member);
    }

    // DTO 반환 메소드들 (Controller용)
    @Transactional
    public CategoryDto createCategoryDto(long memberId, CreateCategoryRequestDto categoryDto) {
        Category category = createCategory(memberId, categoryDto);
        return categoryService.convertCategoryToDto(category);
    }

}
