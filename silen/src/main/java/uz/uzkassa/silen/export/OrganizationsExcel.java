package uz.uzkassa.silen.export;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.dto.filter.OrganizationFilter;
import uz.uzkassa.silen.repository.pg.OrganizationRepository;

import java.io.IOException;
import java.io.OutputStream;


@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class OrganizationsExcel {

    protected int pageSizeLimit = 500;
    protected int pageNumber = 0;
    protected String sheetName = "Отчет";

    private final OrganizationRepository organizationRepository;
    private static final String[] headerNames = {"№",
            "Название компании",
            "Менеджер",
            "ИНН",
            "Товарная группа",
            "X-file",
            "Токен турона",
            "Оферта/Договор"};

    public void writeToStream(OrganizationFilter filter, OutputStream outputStream) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sheet = wb.createSheet(getSheetName());

            // change filter params to default settings
            filter.setPage(getPageNumber());
            filter.setSize(getPageSizeLimit());

            // write header context
            writeHeader(filter, sheet);

            // write content
            writeBody(filter, sheet);

            wb.write(outputStream);
        } catch (IOException e) {
            log.error("Error on time write to stream", e);
        }
    }

    protected void writeHeader(final OrganizationFilter filter, final XSSFSheet sheet) {
        sheet.setHorizontallyCenter(true);
        sheet.setVerticallyCenter(true);
        final XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headerNames.length; i++) {
            final XSSFCell cell = row.createCell(i);
            cell.setCellValue(headerNames[i]);
        }
    }

    protected void writeBody(final OrganizationFilter filter, final XSSFSheet sheet) {
        int rowIndex = 1;
        boolean hasNext;
        int pageNumber = 0;
        do {
            filter.setPage(pageNumber++);
            Page<Organization> allByFilter = organizationRepository.findAllByFilter(filter);

            for (final Organization organization : allByFilter) {
                String[] organizationData = {
                        "" + rowIndex,
                        organization.getName(),
                        (organization.getManager() != null) ? organization.getManager().getName() : "-",
                        organization.getTin(),
                        organization.getProductGroup().getNameRu(),
                        organization.isXfileBinded() ? "Подключен" : "-",
                        organization.getTuronToken() != null ? organization.getTuronToken() : "-",
                        organization.getBillingDocumentType() != null ? "Подписано " + organization.getBillingDocumentType().getNameUzCyrillic() : "-",
                };
                final XSSFRow row = sheet.createRow(rowIndex++);
                writeOrganization(row, organizationData);
            }
            hasNext = allByFilter.hasNext();
        } while (hasNext);
    }

    private void writeOrganization(final XSSFRow row, final String[] organization) {
        for (int i = 0; i < organization.length; i++) {
            final XSSFCell cell = row.createCell(i);
            cell.setCellValue(organization[i]);
        }
    }
}
