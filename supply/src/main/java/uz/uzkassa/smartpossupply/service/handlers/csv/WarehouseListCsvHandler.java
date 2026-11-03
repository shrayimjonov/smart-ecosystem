package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.warehouse.WarehouseListDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.WarehouseService;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WarehouseListCsvHandler extends AbstractCsvHandler<BaseFilter> {

    WarehouseService warehouseService;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Warehouses.csv";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Название");
        header.add("Филиалы");
        header.add("Ответственное лицо");
        header.add("Основной");
        header.add("Статус");
        return header;
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        List<WarehouseListDTO> resultList = warehouseService.getList(filter).getContent();
        resultList
                .forEach(warehouse ->
                        csvWriter.writeNext(
                                new String[]{
                                        warehouse.getName(),
                                        warehouse.getBranch() != null ? warehouse.getBranch().getName() : "",
                                        warehouse.getOwner() != null ? (warehouse.getOwner().getFirstName() + " " + warehouse.getOwner().getLastName()) : "",
                                        warehouse.isMain() ? "Да" : "Нет",
                                        warehouse.getStatus() != null ? warehouse.getStatus().getName() : "",
                                },
                                false
                        )
                );
    }
}
