package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.CustomerBranchApplication;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.address.District;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.enums.CustomerBranchApplicationStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CustomerBranchApplicationRepository;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerBranchApplicationExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    CustomerBranchApplicationRepository customerBranchApplicationRepository;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Customer_Branch_Applications.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Название", "TT", "Номер телефона", "ИНН", "Регион", "Район", "Сотрудник", "Причина", "Статус заявки", "Дата создания");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<CustomerBranchApplication> result = customerBranchApplicationRepository.getResultList(filter).getList();
        int rowIndex = 0;
        for (CustomerBranchApplication customerBranchApplication : result) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(customerBranchApplication.getCustomerBranch().getCustomer().getName());
            row.createCell(cellIndex++).setCellValue(customerBranchApplication.getCustomerBranch().getName());
            row.createCell(cellIndex++).setCellValue(customerBranchApplication.getCustomerBranch().getPhone());
            row.createCell(cellIndex++).setCellValue(customerBranchApplication.getCustomerBranch().getCustomer().getTin());
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(customerBranchApplication.getCustomerBranch().getAddress()).map(BaseAddress::getRegion).map(Region::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(customerBranchApplication.getCustomerBranch().getAddress()).map(BaseAddress::getDistrict).map(District::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(customerBranchApplication.getManager().getName());
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(customerBranchApplication.getReason()).map(Reference::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(customerBranchApplication.getStatus()).map(CustomerBranchApplicationStatus::getName).orElse(""));
            row.createCell(cellIndex).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(customerBranchApplication.getCreatedDate()));
        }
        return workbook;
    }
}
