package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FiscalizationExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    FiscalizationRepository fiscalizationRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Fiscalization.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Фискальный модуль", "Компания", "Филиал", "Клиент", "ТТ", "Дата", "Статус", "Кто создал");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        fiscalizationRepository
                .getResultList(filter)
                .getList()
                .forEach(fiscalization ->
                        csvWriter.writeNext(
                                new String[]{
                                        fiscalization.getDevice().getSerialNumber(),
                                        fiscalization.getFiscalModule().getSerialNumber(),
                                        fiscalization.getBranch().getCompany().getName(),
                                        fiscalization.getBranch().getName(),
                                        fiscalization.getCustomer().getName(),
                                        fiscalization.getCustomerBranch().getName(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(fiscalization.getCreatedDate()),
                                        fiscalization.getStatus().getName(),
                                        fiscalization.getCreator().getFullName()
                                },
                                false
                        )
                );
    }
}
