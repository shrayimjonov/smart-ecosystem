package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.inventory.CategoryField;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryFieldRepository extends BaseRepository<CategoryField, Long> {

    Optional<CategoryField> findFirstByCategoryIdAndFieldId(Long categoryId, Long fieldId);

    List<CategoryField> findAllByCategoryId(Long categoryId);

    @Query("select cf.fieldId from CategoryField cf where cf.categoryId=:categoryId")
    List<Long> findAllFieldIdsByCategoryId(@Param("categoryId") Long categoryId);
}
