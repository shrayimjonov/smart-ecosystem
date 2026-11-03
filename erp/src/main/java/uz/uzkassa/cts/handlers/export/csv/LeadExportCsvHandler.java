package uz.uzkassa.cts.handlers.export.csv;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.CallStatus;
import uz.uzkassa.cts.enums.LeadState;
import uz.uzkassa.cts.filter.LeadFilter;
import uz.uzkassa.cts.repository.LeadRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LeadExportCsvHandler extends AbstractExportCsvHandler<LeadFilter> {

    LeadRepository leadRepository;

    @Override
    public String getFileName(LeadFilter filter) {
        return "Leads.csv";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ФИО лида", "Роль лида", "Номер", "Источник лида", "Название организации", "Дата создания", "Статус лида",
                "Статус по результату обзвона", "Статус звонка", "Время обработки", "Источник трафика", "Тип трафика", "Название рекламной компании",
                "Тип компании", "Версия объявления");
    }

    @Override
    protected void drawItemList(LeadFilter filter, CSVWriter csvWriter) {
        leadRepository
                .getResultList(filter)
                .getList()
                .forEach(lead ->
                        csvWriter.writeNext(
                                new String[]{
                                        lead.getName(),
                                        lead.getRole(),
                                        lead.getPhone(),
                                        lead.getSource(),
                                        lead.getCompanyName(),
                                        DateUtil.getLocalDateTimeAsStringWithoutSecond(lead.getCreatedDate()),
                                        lead.getStatus().getName(),
                                        Optional.ofNullable(lead.getState()).map(LeadState::getName).orElse(""),
                                        Optional.ofNullable(lead.getCallStatus()).map(CallStatus::getName).orElse(""),
                                        Optional.ofNullable(lead.getLastModifiedDate()).map(DateUtil::getLocalDateTimeAsStringWithoutSecond).orElse(""),
                                        lead.getUtmSource(),
                                        lead.getUtmMedium(),
                                        lead.getUtmCampaign(),
                                        lead.getUtmTerm(),
                                        lead.getUtmContent()
                                },
                                false
                        )
                );
    }
}
