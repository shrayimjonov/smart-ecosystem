package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.CtsServiceOrderSpecification;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CtsServiceOrderSpecificationRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ServiceOrderSpecificationExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    CtsServiceOrderSpecificationRepository ctsServiceOrderSpecificationRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "service_order_specification.xls";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Номер", "ИНН", "Клиент", "Договор", "Дата договора", "Дата счет на оплату", "Сумма", "Статус", "Файл");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<CtsServiceOrderSpecification> result = ctsServiceOrderSpecificationRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (CtsServiceOrderSpecification ctsServiceOrderSpecification : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(ctsServiceOrderSpecification.getId());
            row.createCell(cellIndex++).setCellValue(ctsServiceOrderSpecification.getContract().getCustomer().getTin());
            row.createCell(cellIndex++).setCellValue(ctsServiceOrderSpecification.getContract().getCustomer().getName());
            row.createCell(cellIndex++).setCellValue(ctsServiceOrderSpecification.getContract().getNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(ctsServiceOrderSpecification.getContract().getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(ctsServiceOrderSpecification.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(ctsServiceOrderSpecification.getTotalSellPrice().toString());
            row.createCell(cellIndex++).setCellValue(ctsServiceOrderSpecification.getStatus().getName());
            row.createCell(cellIndex).setCellValue(ctsServiceOrderSpecification.getSpecFileUri());
        }
        return workbook;
    }
}
