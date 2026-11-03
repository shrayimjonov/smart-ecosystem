package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.category.CategorySort;
import uz.uzkassa.smartpossupply.dto.category.CategorySortDTO;
import uz.uzkassa.smartpossupply.dto.category.CategorySortItemDTO;
import uz.uzkassa.smartpossupply.repository.CategorySortRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategorySortService extends BaseService {

    CategorySortRepository categorySortRepository;

    @Transactional
    public void create(CategorySortDTO categorySortDTO) {
        categorySortRepository.deleteAllByCategoryIdIn(categorySortDTO.getItems().stream().map(CategorySortItemDTO::getCategoryId).toList());
        List<CategorySort> categorySortList = categorySortDTO.getItems().stream().map(categorySortItemDTO -> {
            CategorySort categorySort = new CategorySort();
            categorySort.setCategoryId(categorySortItemDTO.getCategoryId());
            categorySort.setSortOrder(categorySortItemDTO.getSortOrder());
            return categorySort;
        }).toList();
        categorySortRepository.saveAll(categorySortList);
    }
}