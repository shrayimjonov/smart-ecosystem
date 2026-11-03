package uz.uzkassa.cts.handlers.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.Lead;
import uz.uzkassa.cts.filter.LeadFilter;
import uz.uzkassa.cts.repository.LeadRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.LinkedList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class LeadExcelHandler extends AbstractExcelHandler<LeadFilter> {

    LeadRepository leadRepository;

    @Override
    protected String getFileName(LeadFilter filter) {
        return "leads.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("ФИО лида");
        header.add("Роль лида");
        header.add("Номер");
        header.add("Источник лида");
        header.add("Название организации");
        header.add("Дата создания");
        header.add("Статус лида");
        header.add("Статус по результату обзвона");
        header.add("Статус звонка");
        header.add("Время обработки");
        header.add("Источник трафика");
        header.add("Тип трафика");
        header.add("Название рекламной компании");
        header.add("Тип компании");
        header.add("Версия объявления");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(LeadFilter filter) {
        log.info("Leads export to excel. Filter: {}", filter);
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(100000);
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
            row.createCell(cellIndex++).setCellValue(lead.getStatus() != null ? lead.getStatus().getName() : "");
            row.createCell(cellIndex++).setCellValue(lead.getState() != null ? lead.getState().getName() : "");
            row.createCell(cellIndex++).setCellValue(lead.getCallStatus() != null ? lead.getCallStatus().getName() : "");
            row.createCell(cellIndex++).setCellValue(lead.getResolvedDate() != null ? DateUtil.getLocalDateTimeAsStringWithoutSecond(lead.getResolvedDate()) : "");
            row.createCell(cellIndex++).setCellValue(lead.getUtmSource());
            row.createCell(cellIndex++).setCellValue(lead.getUtmMedium());
            row.createCell(cellIndex++).setCellValue(lead.getUtmCampaign());
            row.createCell(cellIndex++).setCellValue(lead.getUtmTerm());
            row.createCell(cellIndex).setCellValue(lead.getUtmContent());
        }
        return workbook;
    }
}
