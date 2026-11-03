package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.RepairTicketVisitType;
import uz.uzkassa.cts.enums.RepairType;
import uz.uzkassa.cts.filter.RepairTicketFilter;
import uz.uzkassa.cts.repository.RepairTicketRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RepairTicketExportCsvHandler extends AbstractExportCsvHandler<RepairTicketFilter> {

    RepairTicketRepository repairTicketRepository;

    @Override
    public String getFileName(RepairTicketFilter filter) {
        return "Repair_Tickets.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Клиент", "Клиент ИНН", "ККМ", "Номер  телефона", "Филиал", "Дата создания тикета", "Тип тикета", "Статус", "Дата приема", "Визит", "Тип ремонта");
    }

    @Override
    protected void drawItemList(RepairTicketFilter filter, CSVWriter csvWriter) {
        repairTicketRepository
                .getResultList(filter)
                .getList()
                .forEach(repairTicket ->
                        csvWriter.writeNext(
                                new String[]{
                                        repairTicket.getCustomer().getName(),
                                        repairTicket.getCustomer().getTin(),
                                        repairTicket.getDevice().getSerialNumber(),
                                        repairTicket.getPhone(),
                                        repairTicket.getFromBranch().getAddress().getRegion().getName(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(repairTicket.getCreatedDate()),
                                        repairTicket.getType().getName(),
                                        repairTicket.getStatus().getName(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(repairTicket.getAcceptedDate()),
                                        Optional.ofNullable(repairTicket.getVisitType()).map(RepairTicketVisitType::getName).orElse(""),
                                        Optional.ofNullable(repairTicket.getRepairType()).map(RepairType::getName).orElse("")
                                },
                                false
                        )
                );
    }
}
