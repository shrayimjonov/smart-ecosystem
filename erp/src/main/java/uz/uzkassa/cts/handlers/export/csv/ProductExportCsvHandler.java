package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.ProductFilter;
import uz.uzkassa.cts.repository.ProductRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductExportCsvHandler extends AbstractExportCsvHandler<ProductFilter> {

    ProductRepository productRepository;

    @Override
    public String getFileName(ProductFilter filter) {
        return "Products.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Товар", "Категория", "НДС", "Ед. изм.", "Цена");
    }

    @Override
    protected void drawItemList(ProductFilter filter, CSVWriter csvWriter) {
        productRepository
                .getResultList(filter)
                .getList()
                .forEach(product ->
                        csvWriter.writeNext(
                                new String[]{
                                        product.getName(),
                                        product.getCategory().getName(),
                                        product.getVat().getName(),
                                        product.getUnit().getName(),
                                        product.getPrice().toString()
                                },
                                false
                        )
                );
    }
}
