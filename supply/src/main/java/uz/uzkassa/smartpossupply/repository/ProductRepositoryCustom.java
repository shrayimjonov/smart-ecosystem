package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.product.*;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferProductListDTO;
import uz.uzkassa.smartpossupply.dto.telegram.TelegramTopProductListDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.ProductFilter;

import java.util.List;

@Repository
public interface ProductRepositoryCustom {
    ResultList<Product> getResultList(ProductFilter filter);

    ResultList<ProductListForOfflineDTO> getResultListForOffline(ProductFilter filter);

    List<ProductResidueDTO> getProductResidueList(BaseFilter filter);

    List<TopProductDTO> getTopProductList(BaseFilter filter);

    List<TelegramTopProductListDTO> getTopProductsForTelegram(Long branchId);

    List<ProductLookUpWithWarehouseQtyDTO> getProductLookUpWithWarehouseQty(ProductFilter filter);

    ResultList<StockTransferProductListDTO> getListToStockTransfer(BaseFilter filter);

    ResultList<WebKassaProductListDTO> getWebKassaProductList(ProductFilter filter);

}
