package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.filters.StockProductFilter;
import uz.uzkassa.smartpossupply.repository.StockProductRepository;
import uz.uzkassa.smartpossupply.web.rest.errors.NotFoundException;

import java.util.LinkedList;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 12.06.2022 15:59
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StockOutDetailCsvHandler extends AbstractCsvHandler<StockProductFilter> {

    StockProductRepository stockProductRepository;

    @Override
    public String getFileName(StockProductFilter filter) {
        return "Stock_Out_Detail.csv";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Наименование");
        header.add("Ед. Изм");
        header.add("Количество");
        header.add("Цена");
        header.add("Сумма без НДС");
        header.add("НДС");
        header.add("Сумма с НДС");
        return header;
    }

    @Override
    protected void drawItemList(StockProductFilter filter, CSVWriter csvWriter) {
        StockProduct stockProduct = stockProductRepository
                .findById(filter.getStockProductId()).orElseThrow(() -> new NotFoundException("Stock product not found by id " + filter.getStockProductId()));
        csvWriter.writeNext(
                new String[]{
                        stockProduct.getProduct().getName(),
                        stockProduct.getUnit().getName(),
                        stockProduct.getQty().toString(),
                        stockProduct.getSellingPriceWithoutVat().toString(),
                        stockProduct.getTotalSellingPriceWithoutVat().toString(),
                        stockProduct.getTotalVatPrice().toString(),
                        stockProduct.getTotalSellingPrice().toString()
                },
                false
        );
    }

}
