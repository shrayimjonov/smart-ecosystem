package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.customer.Feedback;
import uz.uzkassa.cts.filter.FeedbackFilter;
import uz.uzkassa.cts.repository.FeedbackRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FeedbackExportExcelHandler extends AbstractExportExcelHandler<FeedbackFilter> {

    FeedbackRepository feedbackRepository;

    @Override
    public String getFileName(FeedbackFilter filter) {
        return "Feedback.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Сотрудник", "Регион", "Клиент", "ТТ", "Тип", "Оценка", "Статус", "Дата");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(FeedbackFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Feedback> result = feedbackRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (Feedback feedback : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(feedback.getCreator().getFullName());
            row.createCell(cellIndex++).setCellValue(feedback.getCustomerBranch().getAddress().getRegion().getName());
            row.createCell(cellIndex++).setCellValue(feedback.getCustomerBranch().getCustomer().getName());
            row.createCell(cellIndex++).setCellValue(feedback.getCustomerBranch().getName());
            row.createCell(cellIndex++).setCellValue(feedback.getType().getName());
            row.createCell(cellIndex++).setCellValue(feedback.getRate());
            row.createCell(cellIndex++).setCellValue(feedback.getStatus().getName());
            row.createCell(cellIndex).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(feedback.getCreatedDate()));
        }
        return workbook;
    }
}
