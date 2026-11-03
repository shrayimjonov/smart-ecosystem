package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.filter.OneTimeOrderFilter;
import uz.uzkassa.cts.repository.OneTimeOrderRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OneTimeOrderExportCsvHandler extends AbstractExportCsvHandler<OneTimeOrderFilter> {

    OneTimeOrderRepository onetimeOrderRepository;

    @Override
    public String getFileName(OneTimeOrderFilter filter) {
        return "One_time_order.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Номер", "ИНН", "Серийный номер", "Клиент", "Договор", "Дата счета на оплату", "Сумма", "Статус", "Создал", "Последние изменения внес");
    }

    @Override
    protected void drawItemList(OneTimeOrderFilter filter, CSVWriter csvWriter) {
        onetimeOrderRepository
                .getResultList(filter)
                .getList()
                .forEach(oneTimeOrder ->
                        csvWriter.writeNext(
                                new String[]{
                                        oneTimeOrder.getNumber(),
                                        oneTimeOrder.getContract().getCustomer().getTin(),
                                        oneTimeOrder.getSerialNumberKKM(),
                                        oneTimeOrder.getContract().getCustomer().getName(),
                                        oneTimeOrder.getContract().getName(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(oneTimeOrder.getCreatedDate()),
                                        oneTimeOrder.getStatus().getName(),
                                        oneTimeOrder.getCreator().getName(),
                                        oneTimeOrder.getLastModifier().getName()
                                },
                                false
                        )
                );
    }
}
