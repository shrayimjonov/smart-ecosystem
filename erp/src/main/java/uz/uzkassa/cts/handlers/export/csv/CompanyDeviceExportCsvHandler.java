package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CompanyDeviceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CompanyDeviceExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    CompanyDeviceRepository companyDeviceRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Company_Devices.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Статус", "Создатель", "Компания");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        companyDeviceRepository
                .getResultList(filter)
                .getList()
                .forEach(companyDevice ->
                        csvWriter.writeNext(
                                new String[]{
                                        companyDevice.getDevice().getSerialNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(companyDevice.getCreatedDate()),
                                        companyDevice.getStatus().getName(),
                                        companyDevice.getCreator().getName(),
                                        companyDevice.getCompany().getName()
                                },
                                false
                        )
                );
    }
}
