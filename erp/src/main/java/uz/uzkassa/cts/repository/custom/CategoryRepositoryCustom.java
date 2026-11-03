package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.Category;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.category.admin.CategoryTreeAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

@Repository
public interface CategoryRepositoryCustom {

    ResultList<Category> getResultList(BaseFilter filter);

    List<CategoryTreeAdminDTO> treeList(BaseFilter filter);
}
