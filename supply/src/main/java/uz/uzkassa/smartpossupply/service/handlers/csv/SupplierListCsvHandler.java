package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.supplier.SupplierListDTO;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.service.SupplierService;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SupplierListCsvHandler extends AbstractCsvHandler<CompanyFilter> {

    SupplierService supplierService;

    @Override
    public String getFileName(CompanyFilter filter) {
        return "Suppliers.csv";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("ИНН");
        header.add("Название");
        header.add("Регион");
        header.add("Район");
        header.add("Адрес");
        header.add("Контактное лицо");
        return header;
    }

    @Override
    protected void drawItemList(CompanyFilter filter, CSVWriter csvWriter) {
        List<SupplierListDTO> resultList = supplierService.getList(filter).getContent();
        resultList
                .forEach(supplier ->
                        csvWriter.writeNext(
                                new String[]{
                                        supplier.getTin(),
                                        supplier.getName(),
                                        supplier.getAddress() != null && supplier.getAddress().getRegion() != null ? supplier.getAddress().getRegion().getName() : "",
                                        supplier.getAddress() != null && supplier.getAddress().getDistrict() != null ? supplier.getAddress().getDistrict().getName() : "",
                                        Optional.ofNullable(supplier.getAddress() != null ? supplier.getAddress().getFullAddress() : null).orElse(""),
                                        Optional.ofNullable(supplier.getContactPerson()).orElse("")
                                },
                                false
                        )
                );
    }
}
