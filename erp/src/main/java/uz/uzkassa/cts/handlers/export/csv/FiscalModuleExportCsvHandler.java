package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.FiscalModuleRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FiscalModuleExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    FiscalModuleRepository fiscalModuleRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Fiscal_Modules.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Кем создан", "Статус");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        fiscalModuleRepository
                .getResultList(filter)
                .getList()
                .forEach(fiscalModule ->
                        csvWriter.writeNext(
                                new String[]{
                                        fiscalModule.getSerialNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(fiscalModule.getCreatedDate()),
                                        fiscalModule.getCreator().getFullName(),
                                        fiscalModule.getStatus().getName()
                                },
                                false
                        )
                );
    }
}
