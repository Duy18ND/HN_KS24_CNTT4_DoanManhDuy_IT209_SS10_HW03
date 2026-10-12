//package ra.demo.test_category;
//
//import jakarta.persistence.EntityManager;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
//import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
//import org.springframework.test.context.ActiveProfiles;
//import ra.demo.model.entity.Category;
//import ra.demo.repository.CategoryRepository;
//
//import java.util.List;
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@ActiveProfiles("test")
//public class CategoryRepositoryTest {
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    private TestEntityManager testEntityManager;
//
//    @Test
//    @DisplayName("Lưu category và lấy danh sách")
//    void shouldFindAll(){
//        List<Category> list = List.of(
//          new Category(null,"Điện tử",true,null),
//          new Category(null,"Điện lạnh",true,null),
//          new Category(null,"Điện dân dụng",true,null),
//          new Category(null,"Điện lưới",true,null)
//        );
//
//        List<Category> categories = categoryRepository.saveAll(list);
//        Assertions.assertThat(categories).hasSize(4);
////        assertThat(categories).allMatch(s->s.getCateId()!=null);
//    }
//
//    @Test
//    @DisplayName("Thêm mới 1 danh mục")
//    void testSaveCategory_Success(){
//        Category cate = new Category(null,"Danh mục 100",true,null);
//        Category saveCategory = testEntityManager.persistAndFlush(cate);
//        Category foundCategory = categoryRepository.findById(saveCategory.getCateId()).orElse(null);
//        assertThat(saveCategory.getCateName().equals(foundCategory.getCateName()));
//    }
//}
