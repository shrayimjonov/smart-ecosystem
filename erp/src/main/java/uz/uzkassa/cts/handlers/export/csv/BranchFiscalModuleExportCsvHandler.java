package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BranchFiscalModuleRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BranchFiscalModuleExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    BranchFiscalModuleRepository branchFiscalModuleRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Branch_Fiscal_Modules.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Кем создан", "Статус", "Компания", "Филиал");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        branchFiscalModuleRepository
                .getResultList(filter)
                .getList()
                .forEach(branchFiscalModule ->
                        csvWriter.writeNext(
                                new String[]{
                                        branchFiscalModule.getFiscalModule().getSerialNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(branchFiscalModule.getCreatedDate()),
                                        branchFiscalModule.getCreator().getFullName(),
                                        branchFiscalModule.getStatus().getName(),
                                        branchFiscalModule.getBranch().getCompany().getName(),
                                        branchFiscalModule.getBranch().getName()
                                },
                                false
                        )
                );
    }
}
