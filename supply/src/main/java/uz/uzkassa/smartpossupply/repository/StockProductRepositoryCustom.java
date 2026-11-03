package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.dto.aggregationcodes.AggregationCodeStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonNameDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalTopProductsStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCapacityDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductLookUpDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportByProductDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportByProductExtraInfoDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportCompanyDTO;
import uz.uzkassa.smartpossupply.filters.*;

import java.util.List;

@Repository
public interface StockProductRepositoryCustom {
    List<StockProductLookUpDTO> getLookUp(StockInProductFilter filter);

    List<ProductCapacityDTO> lookUpSortedProducts(StockInProductFilter filter);

    List<ProductCapacityDTO> lookUpProducts(StockInProductFilter filter);

    StockProductLookUpDTO getProduct(Long productId, BaseFilter filter);

    List<CommonNameDTO> lookUpStockProduct(StockInProductFilter filter);

    Page<StockProductQtyDetailListDTO> getQtyDetails(StockFilter filter);

    StockProductQtyStatisticsDTO getProductQtyStatistics(StockFilter filter);

    AggregationCodeStatisticsDTO getAggregationCodeStatistics(AggregationCodesReportFilter filter);

    boolean checkProductInStock(Long productId);

    ResultList<StockProduct> getList(ReconciliationActFilter filter);

    ResultList<StockReportByProductDTO> getStockReport(StockReportByProductFilter filter);

    ResultList<StockReportByProductExtraInfoDTO> getStockReportExtraInfo(StockReportByProductFilter filter);

    List<StockReportCompanyDTO> getStockReportCompanyInfo(StockReportByProductFilter filter);

    ResultList<TerminalTopProductsStatisticsDTO> getTopProducts(StockFilter filter);
}
