package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Lead;
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
public class LeadExportExcelHandler extends AbstractExportExcelHandler<LeadFilter> {

    LeadRepository leadRepository;

    @Override
    public String getFileName(LeadFilter filter) {
        return "Leads.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ФИО лида", "Роль лида", "Номер", "Источник лида", "Название организации", "Дата создания", "Статус лида",
                "Статус по результату обзвона", "Статус звонка", "Время обработки", "Источник трафика", "Тип трафика", "Название рекламной компании",
                "Тип компании", "Версия объявления");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(LeadFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Lead> result = leadRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Lead lead : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(lead.getName());
            row.createCell(cellIndex++).setCellValue(lead.getRole());
            row.createCell(cellIndex++).setCellValue(lead.getPhone());
            row.createCell(cellIndex++).setCellValue(lead.getSource());
            row.createCell(cellIndex++).setCellValue(lead.getCompanyName());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(lead.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(lead.getStatus().getName());
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(lead.getState()).map(LeadState::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(lead.getCallStatus()).map(CallStatus::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(lead.getLastModifiedDate()).map(DateUtil::getLocalDateTimeAsStringWithoutSecond).orElse(""));
            row.createCell(cellIndex++).setCellValue(lead.getUtmSource());
            row.createCell(cellIndex++).setCellValue(lead.getUtmMedium());
            row.createCell(cellIndex++).setCellValue(lead.getUtmCampaign());
            row.createCell(cellIndex++).setCellValue(lead.getUtmTerm());
            row.createCell(cellIndex).setCellValue(lead.getUtmContent());
        }
        return workbook;
    }
}
