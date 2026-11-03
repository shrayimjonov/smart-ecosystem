package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.category.Category;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.category.CategoryTreeDTO;
import uz.uzkassa.smartpossupply.dto.category.MobileCategoryTreeDTO;
import uz.uzkassa.smartpossupply.dto.telegram.TelegramCategoryListDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.TelegramCategoryFilter;

import java.util.List;

@Repository
public interface CategoryRepositoryCustom {
    ResultList<Category> getResultList(BaseFilter filter);

    List<Category> lookUp(BaseFilter filter);

    List<CategoryTreeDTO> treeList(BaseFilter filter);

    List<CategoryTreeDTO> treeListSorted(BaseFilter filter);

    List<MobileCategoryTreeDTO> treeListForMobile(BaseFilter filter);

    List<TelegramCategoryListDTO> getListForTelegram(TelegramCategoryFilter filter);
}
