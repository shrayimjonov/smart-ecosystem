package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BranchFiscalModuleRepository;
import uz.uzkassa.cts.repository.CustomerFiscalModuleRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerFiscalModuleExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    CustomerFiscalModuleRepository customerFiscalModuleRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Customer_Fiscal_Modules.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Кем создан", "Статус", "Компания", "Филиал", "Клиент");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        customerFiscalModuleRepository
                .getResultList(filter)
                .getList()
                .forEach(customerFiscalModule ->
                        csvWriter.writeNext(
                                new String[]{
                                        customerFiscalModule.getFiscalModule().getSerialNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(customerFiscalModule.getCreatedDate()),
                                        customerFiscalModule.getCreator().getFullName(),
                                        customerFiscalModule.getStatus().getName(),
                                        customerFiscalModule.getCustomer().getCompany().getName(),
                                        customerFiscalModule.getCustomer().getBranch().getName(),
                                        customerFiscalModule.getCustomer().getName()
                                },
                                false
                        )
                );
    }
}
