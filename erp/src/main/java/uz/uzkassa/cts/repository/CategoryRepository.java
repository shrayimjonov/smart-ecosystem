package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.Category;
import uz.uzkassa.cts.repository.custom.CategoryRepositoryCustom;

@Repository
public interface CategoryRepository extends BaseRepository<Category, Long>, CategoryRepositoryCustom {
}
