package uz.uzkassa.smartpossupply.service.handlers.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.customer.CustomerListDTO;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.service.CustomerService;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerListCsvHandler extends AbstractCsvHandler<CompanyFilter> {

    CustomerService customerService;

    @Override
    public String getFileName(CompanyFilter filter) {
        return "Customers.csv";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("ИНН");
        header.add("Клиент");
        header.add("Бранд");
        header.add("Регион");
        header.add("Район");
        header.add("Адрес");
        header.add("Кол-во торговых точек");
        header.add("Контактное лицо");
        header.add("Контактный телефон");
        header.add("Баланс");
        header.add("Лимит");
        header.add("Статус");
        return header;
    }

    @Override
    protected void drawItemList(CompanyFilter filter, CSVWriter csvWriter) {
        List<CustomerListDTO> resultList = customerService.getList(filter).getContent();
        resultList
                .forEach(customer ->
                        csvWriter.writeNext(
                                new String[]{
                                        customer.getTin(),
                                        customer.getName(),
                                        Optional.ofNullable(customer.getBrand()).orElse(""),
                                        customer.getAddress() != null ? customer.getAddress().getRegion() != null ? customer.getAddress().getRegion().getName() : "" : "",
                                        customer.getAddress() != null ? customer.getAddress().getDistrict() != null ? customer.getAddress().getDistrict().getName() : "" : "",
                                        customer.getAddress() != null ? customer.getAddress().getFullAddress() : "",
                                        String.valueOf(customer.getCustomerBranchCount()),
                                        Optional.ofNullable(customer.getContactPerson()).orElse(customer.getContactPerson()),
                                        Optional.ofNullable(customer.getPhone()).orElse(customer.getPhone()),
                                        Optional.ofNullable(customer.getCurrentBalance()).orElse(BigDecimal.ZERO).toString(),
                                        Optional.ofNullable(customer.getLimitBalance()).orElse(BigDecimal.ZERO).toString(),
                                        customer.getStatus() != null ? customer.getStatus().getName() : "",
                                },
                                false
                        )
                );
    }
}
