package kimp.community.service;

import jakarta.transaction.Transactional;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.entity.Category;
import kimp.user.entity.User;
import kimp.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class CategoryPacadeService {
    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryPacadeService(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @Transactional
    public Category createCategory(long userId, CreateCategoryRequestDto categoryDto){
        Category category = categoryService.createCategory(categoryDto);
        User user = userService.getUserById(userId);
        return category.setUser(user);
    }

}
