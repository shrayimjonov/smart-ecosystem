package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BranchDeviceRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BranchDeviceExportCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    BranchDeviceRepository branchDeviceRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Branch_Devices.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Статус", "Создатель", "Компания", "Филиал");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        branchDeviceRepository
                .getResultList(filter)
                .getList()
                .forEach(branchDevice ->
                        csvWriter.writeNext(
                                new String[]{
                                        branchDevice.getDevice().getSerialNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(branchDevice.getCreatedDate()),
                                        branchDevice.getStatus().getName(),
                                        branchDevice.getCreator().getName(),
                                        branchDevice.getBranch().getCompany().getName(),
                                        branchDevice.getBranch().getName()
                                },
                                false
                        )
                );
    }
}
