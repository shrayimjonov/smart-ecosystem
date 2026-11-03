package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CtsServiceOrderSpecificationRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ServiceOrderSpecificationCsvHandler extends AbstractExportCsvHandler<BaseFilter> {

    CtsServiceOrderSpecificationRepository ctsServiceOrderSpecificationRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "service_order_specification.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Номер", "ИНН", "Клиент", "Договор", "Дата договора", "Дата счет на оплату", "Сумма", "Статус", "Файл");
    }

    @Override
    protected void drawItemList(BaseFilter filter, CSVWriter csvWriter) {
        ctsServiceOrderSpecificationRepository
                .getResultList(filter)
                .getList()
                .forEach(ctsServiceOrderSpecification ->
                        csvWriter.writeNext(
                                new String[]{
                                        ctsServiceOrderSpecification.getId().toString(),
                                        ctsServiceOrderSpecification.getContract().getCustomer().getTin(),
                                        ctsServiceOrderSpecification.getContract().getCustomer().getName(),
                                        ctsServiceOrderSpecification.getContract().getNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(ctsServiceOrderSpecification.getContract().getCreatedDate()),
                                        ctsServiceOrderSpecification.getNumber(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(ctsServiceOrderSpecification.getCreatedDate()),
                                        ctsServiceOrderSpecification.getTotalSellPrice().toString(),
                                        ctsServiceOrderSpecification.getStatus().getName(),
                                        ctsServiceOrderSpecification.getSpecFileUri()
                                },
                                false
                        )
                );
    }
}
