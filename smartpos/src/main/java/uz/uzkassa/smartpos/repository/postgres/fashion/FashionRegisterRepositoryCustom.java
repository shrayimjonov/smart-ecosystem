package uz.uzkassa.smartpos.repository.postgres.fashion;

import org.springframework.data.domain.Page;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionRegister;
import uz.uzkassa.smartpos.dto.analytics.RegisterStatsDTO;
import uz.uzkassa.smartpos.dto.fashion.FashionBranchColorGroupRegisterDto;
import uz.uzkassa.smartpos.dto.product.ProductItemBranchStockDTO;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionColorGroupRegisterDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionRegisterDto;
import uz.uzkassa.smartpos.filter.FashionRegisterFilter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FashionRegisterRepositoryCustom {
    RegisterStatsDTO getStats(FashionRegisterFilter filter);

    Page<FashionRegisterDto> findAllFashionRegister(FashionRegisterFilter filter);

    List<FashionColorGroupRegisterDto> colorGroupRegisters(FashionRegisterFilter filter);

    Page<FashionBranchColorGroupRegisterDto> branchColorGroupRegisters(FashionRegisterFilter filter);

    List<ProductItemBranchStockDTO> productItemBranchStock(Long productItemId);

    List<FashionRegister> colorGroupItemsRegisters(FashionRegisterFilter filter);

    Map<Long, BigDecimal> getProductsStockAmount(Long branchId, List<Long> productIds);
}
