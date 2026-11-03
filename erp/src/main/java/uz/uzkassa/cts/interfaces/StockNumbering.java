package uz.uzkassa.cts.interfaces;

import uz.uzkassa.cts.dto.stockin.StockInItemLookUpDTO;
import uz.uzkassa.cts.filter.ProductFilter;

import java.util.List;

public interface StockNumbering {

    List<StockInItemLookUpDTO> getItemsLookUp(ProductFilter filter);
}
