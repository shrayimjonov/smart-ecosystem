package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CompanyFiscalModuleRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CompanyFiscalModuleExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    CompanyFiscalModuleRepository companyFiscalModuleRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Company_Fiscal_Modules.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Кем создан", "Статус", "Компания");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        companyFiscalModuleRepository
                .getResultList(filter)
                .getList()
                .forEach(companyFiscalModule ->
                        csvWriter.writeNext(
                                new String[]{
                                        companyFiscalModule.getFiscalModule().getSerialNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(companyFiscalModule.getCreatedDate()),
                                        companyFiscalModule.getCreator().getFullName(),
                                        companyFiscalModule.getStatus().getName(),
                                        companyFiscalModule.getCompany().getName()
                                },
                                false
                        )
                );
    }
}
