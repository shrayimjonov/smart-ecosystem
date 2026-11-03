package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CustomerBranchFiscalModuleRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerBranchFiscalModuleExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Customer_Branch_Fiscal_Modules.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Кем создан", "Статус", "Компания", "Филиал", "Клиент", "Торговая точка");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        customerBranchFiscalModuleRepository
                .getResultList(filter)
                .getList()
                .forEach(customerBranchFiscalModule ->
                        csvWriter.writeNext(
                                new String[]{
                                        customerBranchFiscalModule.getFiscalModule().getSerialNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(customerBranchFiscalModule.getCreatedDate()),
                                        customerBranchFiscalModule.getCreator().getFullName(),
                                        customerBranchFiscalModule.getStatus().getName(),
                                        customerBranchFiscalModule.getCustomerBranch().getBranch().getCompany().getName(),
                                        customerBranchFiscalModule.getCustomerBranch().getBranch().getName(),
                                        customerBranchFiscalModule.getCustomerBranch().getCustomer().getName(),
                                        customerBranchFiscalModule.getCustomerBranch().getName()
                                },
                                false
                        )
                );
    }
}
