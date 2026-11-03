package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.filter.DeviceSuspendHistoryFilter;
import uz.uzkassa.cts.repository.DeviceSuspendHistoryRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DeviceSuspendHistoryExportCsvHandler extends AbstractExportCsvHandler<DeviceSuspendHistoryFilter> {

    DeviceSuspendHistoryRepository deviceSuspendHistoryRepository;

    @Override
    public String getFileName(DeviceSuspendHistoryFilter filter) {
        return "Device_Suspend_History.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Клиент", "Серийный номер", "Тариф", "Дата начала", "Дата окончания", "Создал", "Последние изменения внес");
    }

    @Override
    protected void drawItemList(DeviceSuspendHistoryFilter filter, CSVWriter csvWriter) {
        deviceSuspendHistoryRepository
                .getResultList(filter)
                .getList()
                .forEach(deviceSuspendHistory ->
                        csvWriter.writeNext(
                                new String[]{
                                        deviceSuspendHistory.getContract().getCustomer().getName(),
                                        deviceSuspendHistory.getDeviceSerialNumber(),
                                        deviceSuspendHistory.getTariff().getName(),
                                        DateUtil.getLocalDateAsString(deviceSuspendHistory.getStartDate()),
                                        DateUtil.getLocalDateAsString(deviceSuspendHistory.getDueDate()),
                                        Optional.ofNullable(deviceSuspendHistory.getCreator()).map(User::getName).orElse(""),
                                        Optional.ofNullable(deviceSuspendHistory.getLastModifier()).map(User::getName).orElse("")
                                },
                                false
                        )
                );
    }
}
