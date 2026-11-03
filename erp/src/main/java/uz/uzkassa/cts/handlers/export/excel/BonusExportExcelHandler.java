package uz.uzkassa.cts.handlers.export.excel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.dto.bonus.BonusSummaryExcelExportDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BonusRepository;
import uz.uzkassa.cts.service.AddressService;
import uz.uzkassa.cts.utils.DateUtil;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BonusExportExcelHandler extends AbstractExportExcelHandler<BaseFilter> {

    BonusRepository bonusRepository;
    AddressService addressService;

    @Override
    public String getFileName(BaseFilter filter) {
        return "Bonuses.xlsx";
    }

    public String getSummaryFileName() {
        return "Summary.xlsx";
    }

    @Override
    protected List<String> getHeaders() {
        return List.of("Регион", "Сотрудник", "Компания", "Клиент", "Сумма", "Сумма бонуса", "Тип тарифа", "Дата начала", "Дата окончания");
    }

    protected List<String> getSummaryHeaders() {
        return List.of("Регион", "Сотрудник", "Компания", "Количество", "Сумма", "Сумма бонуса");
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(BaseFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        XSSFSheet summarySheet = createSheet(workbook, getSummaryFileName());
        createHeader(getHeaders(), sheet);
        createHeader(getSummaryHeaders(), summarySheet);
        filter.setPage(0);
        filter.setSize(EXCEL_EXPORT_MAX_LIMIT);
        List<Bonus> bonusList = bonusRepository.findAll(filter);
        List<BonusSummaryExcelExportDTO> bonusSummary = bonusRepository.getBonusSummary(filter);
        int rowIndex = 0;
        for (Bonus bonus : bonusList) {
            Row row = sheet.createRow(++rowIndex);
            int cellIndex = 0;
            Address address = Optional.ofNullable(bonus.getEmployee()).map(employee -> addressService.getPermanentAddress(employee.getEntityId(), employee.getEntityType())).orElse(null);
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(address).map(Address::getRegion).map(Region::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(bonus.getEmployee()).map(User::getFullName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(bonus.getCompany()).map(Company::getName).orElse(""));
            row.createCell(cellIndex++).setCellValue(Optional.ofNullable(bonus.getCustomer()).map(Customer::getCompanyName).orElse(""));
            row.createCell(cellIndex++).setCellValue(bonus.getAmount().toString());
            row.createCell(cellIndex++).setCellValue(bonus.getBonusAmount().toString());
            row.createCell(cellIndex++).setCellValue(bonus.getTariffType());
            row.createCell(cellIndex++).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(bonus.getStartDate()));
            row.createCell(cellIndex).setCellValue(DateUtil.getLocalDateTimeAsStringWithoutSecond(bonus.getEndDate()));
        }
        rowIndex = 0;
        for (BonusSummaryExcelExportDTO bonusSummaryExcelExportDTO : bonusSummary) {
            Row row = summarySheet.createRow(++rowIndex);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(bonusSummaryExcelExportDTO.getRegion());
            row.createCell(cellIndex++).setCellValue(bonusSummaryExcelExportDTO.getEmployee());
            row.createCell(cellIndex++).setCellValue(bonusSummaryExcelExportDTO.getCompany());
            row.createCell(cellIndex++).setCellValue(bonusSummaryExcelExportDTO.getQtyServices());
            row.createCell(cellIndex++).setCellValue(bonusSummaryExcelExportDTO.getTotalAmount().toString());
            row.createCell(cellIndex).setCellValue(bonusSummaryExcelExportDTO.getTotalBonusAmount().toString());
        }
        return workbook;
    }
}
