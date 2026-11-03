package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.ApplicationType;
import uz.uzkassa.cts.enums.OfdStatus;
import uz.uzkassa.cts.filter.OfdApplicationFilter;
import uz.uzkassa.cts.repository.OfdApplicationRepository;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OfdApplicationExportCsvHandler extends AbstractExportCsvHandler<OfdApplicationFilter> {

    OfdApplicationRepository ofdApplicationRepository;

    @Override
    public String getFileName(OfdApplicationFilter filter) {
        return "Ofd_Applications.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ИНН", "Клиент", "Филиал", "Дата отправки", "Статус", "ККМ", "ФМ", "Тип");
    }

    @Override
    protected void drawItemList(OfdApplicationFilter filter, CSVWriter csvWriter) {
        ofdApplicationRepository
                .getResultList(filter)
                .getList()
                .forEach(ofdApplication ->
                        csvWriter.writeNext(
                                new String[]{
                                        ofdApplication.getCustomer().getTin(),
                                        ofdApplication.getCustomer().getName(),
                                        ofdApplication.getCustomer().getBranch().getName(),
                                        ofdApplication.getCreatedDate().toString(),
                                        Optional.ofNullable(ofdApplication.getOfdStatus()).map(OfdStatus::getName).orElse(""),
                                        ofdApplication.getDevice().getSerialNumber(),
                                        ofdApplication.getFiscalModule().getSerialNumber(),
                                        Optional.ofNullable(ofdApplication.getApplicationType()).map(ApplicationType::getName).orElse("")
                                },
                                false
                        )
                );
    }
}
