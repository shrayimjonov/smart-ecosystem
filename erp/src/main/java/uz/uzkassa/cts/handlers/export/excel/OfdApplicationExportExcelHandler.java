package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.OfdApplication;
import uz.uzkassa.cts.enums.ApplicationType;
import uz.uzkassa.cts.enums.OfdStatus;
import uz.uzkassa.cts.filter.OfdApplicationFilter;
import uz.uzkassa.cts.repository.OfdApplicationRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OfdApplicationExportExcelHandler extends AbstractExportExcelHandler<OfdApplicationFilter> {

    OfdApplicationRepository ofdApplicationRepository;

    @Override
    public String getFileName(OfdApplicationFilter filter) {
        return "Ofd_Applications.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("ИНН", "Клиент", "Филиал", "Дата отправки", "Статус", "ККМ", "ФМ", "Тип");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(OfdApplicationFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<OfdApplication> result = ofdApplicationRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (OfdApplication ofdApplication : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(ofdApplication.getCustomer().getTin());
            row.createCell(cellIndex++).setCellValue(ofdApplication.getCustomer().getName());
            row.createCell(cellIndex++).setCellValue(ofdApplication.getCustomer().getBranch().getName());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(ofdApplication.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(ofdApplication.getOfdStatus()).map(OfdStatus::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(ofdApplication.getDevice().getSerialNumber());
            row.createCell(cellIndex++).setCellValue(ofdApplication.getFiscalModule().getSerialNumber());
            row.createCell(cellIndex).setCellValue(Optional.ofNullable(ofdApplication.getApplicationType()).map(ApplicationType::getName).orElse(""));
        }
        return workbook;
    }
}
