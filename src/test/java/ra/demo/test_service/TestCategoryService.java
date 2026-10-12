//package ra.demo.test_service;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import ra.demo.model.entity.Category;
//import ra.demo.repository.CategoryRepository;
//import ra.demo.service.CategoryService;
//import ra.demo.service.impl.CategoryServiceImpl;
//
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.assertj.core.api.Assertions.assertThat;
//
//@ExtendWith(MockitoExtension.class)
//public class TestCategoryService {
//    @Mock
//    private CategoryRepository categoryRepository;
//    @InjectMocks
//    private CategoryServiceImpl categoryService;
//
////    @BeforeEach
////    void setUp(){
////        categoryService = new CategoryServiceImpl(categoryRepository);
////    }
//    @Test
//    @DisplayName("Test lấy tên danh mục")
//    public void getCategory_WhenExist_ReturnCategory(){
//        Category mockCategory = new Category(null,"Test thêm mới",true,null);
//        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
//
//        //when: Gọi hàm thật ở service
//        Category result = categoryService.getCategoryById(1L);
//        Assertions.assertEquals("Test thêm mới", result.getCateName());
//        Mockito.verify(categoryRepository,Mockito.times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("Thêm mới category thành công")
//    void shouldCreateCategory() {
//        Mockito.when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        Category category = categoryService.insertCategory(new Category(null, "Test them mới", true, null));
//        assertThat(category != null);
//    }
//
//    @Test
//    @DisplayName("Thêm mới category bị trùng tên")
//    void shouldCreateCategorySameName() {
//        Mockito.when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        Category category = categoryService.insertCategory(new Category(null, "Test them mới", true, null));
//        assertThat(category == null);
//    }
//}
