//package ra.demo.test_api;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import ra.demo.controller.CategoryController;
//import ra.demo.model.entity.Category;
//import ra.demo.service.CategoryService;
//
//@WebMvcTest(controllers = CategoryController.class)
//public class TestCategoryController {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private CategoryService categoryService;
//
//    @Test
//    void testGetCategory() throws Exception {
//        Mockito.when(categoryService.getCategoryById(1L)).thenReturn(new Category(null,"Test thêm mới",true,null));
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories/1").contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.cateName").value("Test thêm mới"));
//    }
//}
