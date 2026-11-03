package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.apay.APayCategory;
import uz.uzkassa.cts.dto.apay.category.APayCategoryDTO;
import uz.uzkassa.cts.dto.payload.FilterPayload;
import uz.uzkassa.cts.filter.ApayCategoryFilter;

import java.util.List;

public interface APayCategoryService extends BaseService<APayCategory, Long> {

    void sync();

    void syncQueue(FilterPayload filter);

    List<APayCategoryDTO> lookUp(ApayCategoryFilter filter);
}
