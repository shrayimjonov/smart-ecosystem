package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.category.CategorySort;

import java.util.List;

@Repository
public interface CategorySortRepository extends BaseRepository<CategorySort, Long> {

    void deleteAllByCategoryIdIn(List<Long> categoryIds);
}
