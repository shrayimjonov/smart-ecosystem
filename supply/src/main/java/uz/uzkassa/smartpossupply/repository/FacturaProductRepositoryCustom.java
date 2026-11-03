package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.factura.FacturaProduct;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.util.List;

@Repository
public interface FacturaProductRepositoryCustom {
    List<FacturaProduct> findAllByCodeAndCodeTypeAndProductTypeStatusNot(String code, CodeType codeType, Status status, ProductType productType);
}
