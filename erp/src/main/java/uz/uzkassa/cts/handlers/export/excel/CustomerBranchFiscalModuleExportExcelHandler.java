package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.CustomerBranchFiscalModule;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CustomerBranchFiscalModuleRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerBranchFiscalModuleExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Customer_Fiscal_Modules.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Серийный номер", "Дата регистрации", "Кем создан", "Статус", "Компания", "Филиал", "Клиент", "Торговая точка");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<CustomerBranchFiscalModule> result = customerBranchFiscalModuleRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (CustomerBranchFiscalModule customerBranchFiscalModule : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(customerBranchFiscalModule.getFiscalModule().getSerialNumber());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(customerBranchFiscalModule.getCreatedDate()));
            row.createCell(cellIndex++).setCellValue(customerBranchFiscalModule.getCreator().getFullName());
            row.createCell(cellIndex++).setCellValue(customerBranchFiscalModule.getStatus().getName());
            row.createCell(cellIndex++).setCellValue(customerBranchFiscalModule.getCustomerBranch().getBranch().getCompany().getName());
            row.createCell(cellIndex++).setCellValue(customerBranchFiscalModule.getCustomerBranch().getBranch().getName());
            row.createCell(cellIndex++).setCellValue(customerBranchFiscalModule.getCustomerBranch().getCustomer().getName());
            row.createCell(cellIndex).setCellValue(customerBranchFiscalModule.getCustomerBranch().getName());
        }
        return workbook;
    }
}
