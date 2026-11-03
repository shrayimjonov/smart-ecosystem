package uz.uzkassa.smartpos.report;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.domain.postgres.company.Branch_;
import uz.uzkassa.smartpos.domain.postgres.receipt.ShiftReport;
import uz.uzkassa.smartpos.domain.postgres.receipt.ShiftReport_;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device_;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.shift.ShiftReportDTO;
import uz.uzkassa.smartpos.filter.ShiftReportFilter;
import uz.uzkassa.smartpos.filter.specification.ShiftReportSpecificationFilter;
import uz.uzkassa.smartpos.repository.postgres.specifications.ShiftReportSpecification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Component
@Slf4j
public class ShiftExcelReport extends AbstractExcelReport {
    public static final String[] HEADER_TITLES = new String[]{"Начало смены", "Конец смены", "Кол-во чеков", "Cумма", "Наличные", "Карта", "НДС", "Смена", "Филиал", "ККМ"};

    public ByteArrayResource getExcelReport(BaseFilter filter) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(); var bos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = wb.createSheet("Z - Отчет");

            createHeader(wb, sheet, HEADER_TITLES);

            createBody(getShiftReports(filter), sheet);

            setColumnWidth(sheet);

            wb.write(bos);
            return new ByteArrayResource(bos.toByteArray());
        }
    }

    public ByteArrayResource getExcelReportCabinet(ShiftReportFilter filter) throws IOException {
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        try (XSSFWorkbook wb = new XSSFWorkbook(); var bos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = wb.createSheet("Z - Отчет");

            createHeader(wb, sheet, HEADER_TITLES);
            createBody(getShiftReports(filter), sheet);

            setColumnWidth(sheet);

            wb.write(bos);
            return new ByteArrayResource(bos.toByteArray());
        }
    }

    protected void createBody(Page<ShiftReport> shiftReports, XSSFSheet sheet) {
        Page<ShiftReportDTO> page = shiftReports.map(ShiftReport::getDTO);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int r = 1;
        for (ShiftReportDTO shiftReportDTO : page.getContent()) {
            XSSFRow row = sheet.createRow(r++);
            int c = 0;
            XSSFCell cell;

            cell = row.createCell(c++);
            cell.setCellValue(shiftReportDTO.getStartDateTime().format(formatter));

            cell = row.createCell(c++);
            cell.setCellValue(shiftReportDTO.getEndDateTime().format(formatter));

            cell = row.createCell(c++);
            cell.setCellValue(shiftReportDTO.getReceipts() == null ? "0" : shiftReportDTO.getReceipts().toString());

            cell = row.createCell(c++);
            cell.setCellValue(TotalSales(shiftReportDTO.getTotalCash(), shiftReportDTO.getTotalCard()));

            cell = row.createCell(c++);
            cell.setCellValue(shiftReportDTO.getTotalCash() == null ? BigDecimal.ZERO.toString() : shiftReportDTO.getTotalCash().toString());

            cell = row.createCell(c++);
            cell.setCellValue(shiftReportDTO.getTotalCard() == null ? BigDecimal.ZERO.toString() : shiftReportDTO.getTotalCard().toString());

            cell = row.createCell(c++);
            cell.setCellValue(shiftReportDTO.getNDS() == null ? BigDecimal.ZERO.toString() : shiftReportDTO.getNDS().toString());

            cell = row.createCell(c++);
            cell.setCellValue(shiftReportDTO.getFiscalNumber() == null ? "" : shiftReportDTO.getFiscalNumber().toString());

            cell = row.createCell(c++);
            cell.setCellValue(shiftReportDTO.getBranchName());

            cell = row.createCell(c);
            cell.setCellValue(shiftReportDTO.getTerminalSN());
        }
    }

    private @NotNull Page<ShiftReport> getShiftReports(BaseFilter filter) {
        if (filter.getBranchId() == null) {
            filter.setBranchId(getCurrentBranchId());
        }
        filter.setPage(0);
        filter.setSize(10000);
        ShiftReportSpecificationFilter specificationFilter = ShiftReportSpecificationFilter.builder()
                .branchId(filter.getBranchId())
                .from(filter.getFrom())
                .to(filter.getTo())
                .terminalId(filter.getTerminalId())
                .search(filter.getSearch())
                .orderBy(filter.getOrderBy())
                .sortOrder(filter.getSortOrder())
                .build();
        return shiftReportRepository
                .findAll((Specification<ShiftReport>) (root, criteriaQuery, criteriaBuilder) ->
                        ShiftReportSpecification.getPredicateWithSort(root, criteriaQuery, criteriaBuilder, specificationFilter), filter.getPageable());
    }

    private @NotNull Page<ShiftReport> getShiftReports(ShiftReportFilter filter) {
        return shiftReportRepository
                .findAll((Specification<ShiftReport>) (root, query, criteriaBuilder) -> {
                    var predicates = new ArrayList<Predicate>();
                    if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
                        predicates.add(root.get(ShiftReport_.branchId).in(filter.getBranchIds()));
                    } else {
                        var branchJoin = root.join(ShiftReport_.branch);
                        predicates.add(criteriaBuilder.equal(branchJoin.get(Branch_.companyId), getCurrentCompanyId()));
                    }
                    if (filter.getFrom() != null) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(ShiftReport_.endDateTime), filter.getFrom()));
                    }
                    if (filter.getTo() != null) {
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(ShiftReport_.endDateTime), filter.getTo()));
                    }
                    if (filter.getUserId() != null) {
                        predicates.add(criteriaBuilder.equal(root.get(ShiftReport_.userId), filter.getUserId()));
                    }
                    if (StringUtils.isNotBlank(filter.getSearch())) {
                        var searchPredicate = criteriaBuilder.or(
                                criteriaBuilder.like(root.get(ShiftReport_.branch).get(Branch_.serialNumber), filter.getSearchForQuery()),
                                criteriaBuilder.like(root.get(ShiftReport_.terminal).get(Device_.serialNumber), filter.getSearchForQuery())
                        );
                        predicates.add(searchPredicate);

                    }
                    query.orderBy(criteriaBuilder.desc(root.get(ShiftReport_.endDateTime)));
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }, PageRequest.of(0, 10000));
    }

    private String TotalSales(BigDecimal cash, BigDecimal card) {
        return (card == null
                ? BigDecimal.ZERO.add(cash == null ? BigDecimal.ZERO : cash)
                : card.add(cash == null ? BigDecimal.ZERO : cash)).toString();
    }

    private void setColumnWidth(XSSFSheet sheet) {
        sheet.setColumnWidth(0, 5000);//Время начала смены
        sheet.setColumnWidth(1, 5000);//Время конца смены
        sheet.setColumnWidth(2, 4000);//Кол-во чеков
        sheet.setColumnWidth(3, 4000);//Cумма
        sheet.setColumnWidth(4, 4000);//Наличными
        sheet.setColumnWidth(5, 4000);//Картой
        sheet.setColumnWidth(6, 2500);//НДС
        sheet.setColumnWidth(7, 3200);//Смена
        sheet.setColumnWidth(8, 5500);//Филиал
        sheet.setColumnWidth(9, 5000);//ККМ
    }
}
