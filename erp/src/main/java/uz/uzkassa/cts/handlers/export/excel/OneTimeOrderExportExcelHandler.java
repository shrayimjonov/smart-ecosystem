package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.OneTimeOrder;
import uz.uzkassa.cts.filter.OneTimeOrderFilter;
import uz.uzkassa.cts.repository.OneTimeOrderRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OneTimeOrderExportExcelHandler extends AbstractExportExcelHandler<OneTimeOrderFilter> {

    OneTimeOrderRepository oneTimeOrderRepository;

    @Override
    public String getFileName(OneTimeOrderFilter filter) {
        return "one_time_order.xls";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Номер", "ИНН", "Серийный номер", "Клиент", "Договор", "Дата счета на оплату", "Сумма", "Статус", "Создал", "Последние изменения внес");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(OneTimeOrderFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<OneTimeOrder> result = oneTimeOrderRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (OneTimeOrder oneTimeOrder : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(oneTimeOrder.getNumber());
            row.createCell(cellIndex++).setCellValue(oneTimeOrder.getContract().getCustomer().getTin());
            row.createCell(cellIndex++).setCellValue(oneTimeOrder.getSerialNumberKKM());
            row.createCell(cellIndex++).setCellValue(oneTimeOrder.getContract().getCustomer().getName());
            row.createCell(cellIndex++).setCellValue(oneTimeOrder.getContract().getName());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(oneTimeOrder.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(oneTimeOrder.getTotalSellPrice().toString());
            row.createCell(cellIndex++).setCellValue(oneTimeOrder.getStatus().getName());
            row.createCell(cellIndex++).setCellValue(oneTimeOrder.getCreator().getName());
            row.createCell(cellIndex).setCellValue(oneTimeOrder.getLastModifier().getName());
        }
        return workbook;
    }
}
