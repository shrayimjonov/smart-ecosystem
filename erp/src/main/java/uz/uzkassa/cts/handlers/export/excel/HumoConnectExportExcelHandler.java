package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.humoconnect.response.HumoConnectResponseDto;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.HumoConnectFilter;
import uz.uzkassa.cts.repository.HumoConnectRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class HumoConnectExportExcelHandler extends AbstractExportExcelHandler<HumoConnectFilter> {

    HumoConnectRepository humoConnectRepository;

    @Override
    public String getFileName(HumoConnectFilter filter) {
        return "Humo_Connect.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Филиал", "Статус", "Статус подключения", "Адрес", "МФО", "Расчетный счет", "ФС", "Тип банка", "Платежная система", "Компания", "ИНН", "Серийный номер ККМ", "Сотрудник", "Тип кейса", "Лицензия", "Статус документа", "Первый чек", "Последний чек");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(HumoConnectFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<HumoConnectResponseDto> result = humoConnectRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (HumoConnectResponseDto humoConnect : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(humoConnect.getCreatorBranch());
            row.createCell(cellIndex++).setCellValue(humoConnect.getStatus().getName());
            row.createCell(cellIndex++).setCellValue(humoConnect.getOperatorStatus().getName());
            row.createCell(cellIndex++).setCellValue(humoConnect.getAddress().getStreet());
            row.createCell(cellIndex++).setCellValue(humoConnect.getMfo());
            row.createCell(cellIndex++).setCellValue(humoConnect.getAccountNumber());
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(humoConnect.getBusinessType()).map(BusinessType::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(humoConnect.getBankType()).map(BankType::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(humoConnect.getPaymentType()).map(PaymentType::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(humoConnect.getCustomerName());
            row.createCell(cellIndex++).setCellValue(humoConnect.getCustomerTin());
            row.createCell(cellIndex++).setCellValue(humoConnect.getDeviceSerialNumber());
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(humoConnect.getCaseType()).map(CaseType::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(humoConnect.getCaseId());
            row.createCell(cellIndex++).setCellValue(humoConnect.getLicense());
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(humoConnect.getDocFlowStatus()).map(DocFlowStatus::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(humoConnect.getFirstReceiptDate()));
            row.createCell(cellIndex).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(humoConnect.getLastReceiptDate()));
        }
        return workbook;
    }
}
