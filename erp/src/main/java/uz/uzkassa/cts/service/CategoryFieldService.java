package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.inventory.CategoryField;
import uz.uzkassa.cts.dto.categoryfield.CategoryFieldBatchDTO;
import uz.uzkassa.cts.dto.categoryfield.CategoryFieldDetailAdminDTO;
import uz.uzkassa.cts.dto.categoryfield.CategoryFieldValueDetailAdminDTO;

public interface CategoryFieldService extends BaseService<CategoryField, Long> {

    void createBatch(CategoryFieldBatchDTO categoryFieldBatchDTO);

    CategoryFieldDetailAdminDTO getByCategoryId(Long categoryId);

    CategoryFieldValueDetailAdminDTO getFieldAndValuesByCategoryId(Long categoryId);
}
