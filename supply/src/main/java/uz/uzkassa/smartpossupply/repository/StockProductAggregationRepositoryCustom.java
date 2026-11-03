package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregation;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonNameDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.product.ProductCapacityDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductLookUpDTO;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.AggregationCodesReportFilter;
import uz.uzkassa.smartpossupply.filters.StockInProductFilter;

import java.util.List;

@Repository
public interface StockProductAggregationRepositoryCustom {
    List<StockProductLookUpDTO> getLookUp(StockInProductFilter filter);

    List<ProductCapacityDTO> lookUpProducts(StockInProductFilter filter);

    List<CommonNameDTO> lookUpStockProduct(StockInProductFilter filter);

    ResultList<StockProductAggregation> getCodes(AggregationCodesReportFilter filter);

    ProductCapacityDTO getProduct(Long branchId, Long warehouseId, String code, TransferType transferType);
}
