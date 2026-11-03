package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.DeviceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DeviceExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    DeviceRepository deviceRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Devices.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Создатель", "Статус");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        deviceRepository
                .getResultList(filter)
                .getList()
                .forEach(device ->
                        csvWriter.writeNext(
                                new String[]{
                                        device.getSerialNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(device.getCreatedDate()),
                                        device.getCreator().getName(),
                                        device.getStatus().getName()
                                },
                                false
                        )
                );
    }
}
