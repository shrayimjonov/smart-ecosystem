package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.WarehouseRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WarehouseExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    WarehouseRepository warehouseRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Warehouses.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название", "Компания", "Филиал", "Регион", "Район", "Менеджер", "Статус");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        warehouseRepository
                .getResultList(filter)
                .getList()
                .forEach(warehouse ->
                        csvWriter.writeNext(
                                new String[]{
                                        warehouse.getName(),
                                        warehouse.getBranch().getCompany().getName(),
                                        warehouse.getBranch().getName(),
                                        warehouse.getAddress().getRegion().getName(),
                                        warehouse.getAddress().getDistrict().getName(),
                                        warehouse.getManager().getFullName(),
                                        warehouse.getStatus().getName()
                                },
                                false
                        )
                );
    }
}
