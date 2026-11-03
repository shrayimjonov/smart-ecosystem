package uz.uzkassa.smartpos.report;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Component
@Slf4j
public class ReceiptExcelReport extends AbstractExcelReport {
    public static final String[] HEADER_TITLES = new String[]{"Номер чека", "Время", "Сумма", "Наличными", "Картой", "Статус", "№ Смены", "НДС", "Кассир", "Кол-во позиций", "Филиал", "Фискальный модуль"};

    public ByteArrayResource getExcelReport(ReceiptFilter filter, boolean isAdmin) throws IOException {

        if (!isAdmin) {
            filter.setCompanyId(getCurrentCompanyId());
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchId(getCurrentBranchId());
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
            filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        }

        try (XSSFWorkbook wb = new XSSFWorkbook(); var bos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = wb.createSheet("Отчет");

            createHeader(wb, sheet, HEADER_TITLES);
            createBody(filter, sheet);
            setColumnWidth(sheet);

            wb.write(bos);
            return new ByteArrayResource(bos.toByteArray());
        }

    }


    protected void createBody(ReceiptFilter filter, XSSFSheet sheet) {

        Page<ReceiptDTO> page = clickhouseReceiptGenericRepository.findAllByFilter(filter, true);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int r = 1;
        for (ReceiptDTO receipt : page.getContent()) {
            XSSFRow row = sheet.createRow(r++);
            int c = 0;
            XSSFCell cell = row.createCell(c++);
            cell.setCellValue(receipt.getUid());

            cell = row.createCell(c++);
            cell.setCellValue(receipt.getReceiptDateTime().format(formatter));

            cell = row.createCell(c++);
            cell.setCellValue(receipt.getTotalCost().toString());

            cell = row.createCell(c++);
            cell.setCellValue(receipt.getTotalCash().toString());

            cell = row.createCell(c++);
            cell.setCellValue(receipt.getTotalCard().toString());

            cell = row.createCell(c++);
            cell.setCellValue(receipt.getStatus().getNameRu());

            cell = row.createCell(c++);
            cell.setCellValue(receipt.getShiftNo() == null ? "" : receipt.getShiftNo().toString());

            cell = row.createCell(c++);
            cell.setCellValue(receipt.getTotalNds().toString());

            cell = row.createCell(c++);
            cell.setCellValue(receipt.getUser() == null ? "" : receipt.getUser().getFullName().getName());

            cell = row.createCell(c++);
            cell.setCellValue(receipt.getPositionCount());

            cell = row.createCell(c++);
            cell.setCellValue(receipt.getBranchName());

            cell = row.createCell(c);
            cell.setCellValue(receipt.getAdditional() != null ? receipt.getAdditional().getSamModuleSerialNumber() : null);
        }
    }

    private void setColumnWidth(XSSFSheet sheet) {
        sheet.setColumnWidth(0, 5000);//Номер чека
        sheet.setColumnWidth(1, 5000);//Время
        sheet.setColumnWidth(2, 4000);//Сумма
        sheet.setColumnWidth(3, 4000);//Наличными
        sheet.setColumnWidth(4, 4000);//Картой
        sheet.setColumnWidth(5, 3000);//Статус
        sheet.setColumnWidth(6, 3200);//Смены
        sheet.setColumnWidth(7, 2500);//НДС
        sheet.setColumnWidth(8, 5500);//Кассир
        sheet.setColumnWidth(9, 5000);//Кол-во позиций
    }
}
