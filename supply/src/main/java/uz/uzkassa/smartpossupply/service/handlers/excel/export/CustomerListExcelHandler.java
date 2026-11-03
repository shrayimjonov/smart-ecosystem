package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.UserCommonDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerListDTO;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.service.CustomerService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerListExcelHandler extends AbstractExcelHandler<CompanyFilter> {

    CustomerService customerService;

    @Override
    public String getFileName(CompanyFilter filter) {
        return "Customers.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("ИНН");
        header.add("Клиент");
        header.add("Регион");
        header.add("Район");
        header.add("Адрес");
        header.add("Кол-во торговых точек");
        header.add("Контактное лицо");
        header.add("Контактный телефон");
        header.add("Баланс");
        header.add("Лимит");
        header.add("Статус");
        header.add("Кто создал");
        header.add("Роль");
        header.add("Дата создание");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(CompanyFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<CustomerListDTO> customers = customerService.getList(filter).getContent();
        int rowIndex = 0;
        for (CustomerListDTO customer : customers) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(customer.getTin());
            row.createCell(1).setCellValue(customer.getName());
            row.createCell(2).setCellValue(Optional.ofNullable(customer.getAddress()).map(AddressDetailDTO::getRegion).map(CommonDTO::getName).orElse(""));
            row.createCell(3).setCellValue(Optional.ofNullable(customer.getAddress()).map(AddressDetailDTO::getDistrict).map(CommonDTO::getName).orElse(""));
            row.createCell(4).setCellValue(Optional.ofNullable(customer.getAddress()).map(AddressDetailDTO::getFullAddress).orElse(""));
            row.createCell(5).setCellValue(customer.getCustomerBranchCount());
            row.createCell(6).setCellValue(Optional.ofNullable(customer.getContactPerson()).orElse(""));
            row.createCell(7).setCellValue(customer.getPhone());
            row.createCell(8).setCellValue(Optional.ofNullable(customer.getCurrentBalance()).orElse(BigDecimal.ZERO).toString());
            row.createCell(9).setCellValue(Optional.ofNullable(customer.getLimitBalance()).orElse(BigDecimal.ZERO).toString());
            row.createCell(10).setCellValue(Optional.ofNullable(customer.getStatus()).map(CompanyStatus::getName).orElse(""));
            row.createCell(11).setCellValue(Optional.ofNullable(customer.getCreator()).map(UserCommonDTO::getName).orElse(""));
            row.createCell(12).setCellValue(Optional.ofNullable(customer.getCreator()).map(UserCommonDTO::getRole).map(Role::getName).orElse(""));
            row.createCell(13).setCellValue(Optional.ofNullable(customer.getCreatedDate()).map(LocalDateTime::toString).orElse(""));
        }
        return workbook;
    }
}