package kimp.community.controller;

import jakarta.servlet.http.HttpServletRequest;
import kimp.community.dto.category.CategoryDto;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.dto.category.request.UpdateCategoryRequestDto;
import kimp.community.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> getAllCategories(HttpServletRequest request){

        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategoryDto getCategory(HttpServletRequest request, @PathVariable Long id){
        if(id < 0){
            throw new IllegalArgumentException("category Id is not available.");
        }
        return categoryService.getCategoryByID(id);
    }

    @PostMapping
    public CategoryDto createCategory(HttpServletRequest request, @RequestBody CreateCategoryRequestDto createCategoryRequestDto){

        return categoryService.createCategory(createCategoryRequestDto);
    }

    @PatchMapping
    public CategoryDto patchCategory(HttpServletRequest request, @RequestBody UpdateCategoryRequestDto updateCategoryRequestDto){

        return categoryService.updatedCategory(updateCategoryRequestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        if(id < 0){
            throw new IllegalArgumentException("category Id is not available");
        }
        boolean deleted = categoryService.deleteCategory(id);
        if(!deleted){
            return ResponseEntity.notFound().build(); // 404 반환
           }

        return ResponseEntity.noContent().build(); // 204 반환
    }

}
