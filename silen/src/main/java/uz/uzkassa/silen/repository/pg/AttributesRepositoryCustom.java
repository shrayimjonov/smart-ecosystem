package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Attributes;
import uz.uzkassa.silen.dto.AttributeValueDTO;
import uz.uzkassa.silen.dto.filter.AttributesFilter;
import uz.uzkassa.silen.enumeration.ProductType;

import java.util.List;


public interface AttributesRepositoryCustom {

    Page<Attributes> findAllByFilter(AttributesFilter filter);

    List<AttributeValueDTO> getAttributesValue(String productId);

    int getMaxSortOrderValue();

    List<Attributes> getAttributesByType(ProductType productType);
}
