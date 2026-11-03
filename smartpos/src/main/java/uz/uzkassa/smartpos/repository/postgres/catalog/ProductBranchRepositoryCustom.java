package uz.uzkassa.smartpos.repository.postgres.catalog;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.dto.base.PageDto;
import uz.uzkassa.smartpos.dto.catalog.ProductListRecord;
import uz.uzkassa.smartpos.dto.catalog.ProductLookupRecord;
import uz.uzkassa.smartpos.dto.catalog.ProductStatisticsDTO;
import uz.uzkassa.smartpos.dto.catalog.TerminalProductDto;
import uz.uzkassa.smartpos.dto.fashion.catalog.CashboxProductListDto;
import uz.uzkassa.smartpos.dto.filter.CashboxProductFilter;
import uz.uzkassa.smartpos.dto.filter.ProductFilter;
import uz.uzkassa.smartpos.filter.ProductBranchFilter;
import uz.uzkassa.smartpos.filter.TerminalProductFilter;

import java.util.List;

@Repository
public interface ProductBranchRepositoryCustom {
    Page<ProductBranch> findAllByFilter(ProductFilter filter);

    Page<CashboxProductListDto> findAllByFilterForFashion(CashboxProductFilter filter);

    List<ProductBranch> search(ProductFilter filter);

    ProductStatisticsDTO getProductStatistics(ProductFilter filter);

    Page<ProductListRecord> pageCabinet(ProductBranchFilter filter);

    List<ProductLookupRecord> lookup(ProductBranchFilter filter);

    List<ProductBranch> findAllLikeBarcode(String barcode, Long branchId, Long cityId);

    PageDto<TerminalProductDto> findAllByFilter(TerminalProductFilter filter);
}
