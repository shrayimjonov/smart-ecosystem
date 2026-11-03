package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.dto.agent.performancereport.AgentPerformanceReportDTO;
import uz.uzkassa.smartpossupply.filters.AgentPerformanceReportFilter;
import uz.uzkassa.smartpossupply.service.AgentService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AgentPerformanceReportExcelHandler extends AbstractExcelHandler<AgentPerformanceReportFilter> {

    AgentService agentService;

    private static final int COL_BRANCH = 0;
    private static final int COL_AGENT = 1;
    private static final int COL_VISITS = 2;
    private static final int COL_ORDERS = 3;
    private static final int COL_ORDER_TOTAL = 4;
    private static final int COL_PAYMENTS = 5;
    private static final int COL_PRODUCTS = 6;
    private static final int COL_CATEGORIES_START = 7;

    @Override
    public String getFileName(AgentPerformanceReportFilter filter) {
        StringBuilder fileName = new StringBuilder();
        fileName.append("Agent_Performance_Report");
        if (filter.getFrom() != null && filter.getTo() != null) {
            fileName.append("_From_");
            fileName.append(filter.getFrom().toLocalDate().toString());
            fileName.append("_To_");
            fileName.append(filter.getTo().toLocalDate().toString());
        }
        fileName.append(".xlsx");
        return fileName.toString();
    }

    @Override
    protected LinkedList<String> getHeaders() {
        LinkedList<String> header = new LinkedList<>();
        header.add("Филиал");
        header.add("Торговый агент ФИО");
        header.add("Посешения");
        header.add("Заказы: Всего/Черновик");
        header.add("Сумма Заказов");
        header.add("По типам оплат нал/репечисление");
        header.add("Товары шт/бл/тонна/репечисление");
        header.add("Категория Товара шт/бл/тонна");
        header.add("Категория Товара шт/бл/тонна");
        header.add("Категория Товара шт/бл/тонна");
        header.add("Категория Товара шт/бл/тонна");
        header.add("Категория Товара шт/бл/тонна");
        return header;
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(AgentPerformanceReportFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        List<AgentPerformanceReportDTO> report = agentService.getPerformanceReport(filter);
        int rowIndex = 0;
        for (AgentPerformanceReportDTO stockReturnListDTO : report) {
            Row row = sheet.createRow(++rowIndex);

            row.createCell(COL_BRANCH).setCellValue(stockReturnListDTO.getBranch() != null ? stockReturnListDTO.getBranch().getName() : "");

            row.createCell(COL_AGENT).setCellValue(stockReturnListDTO.getAgent().getName());

            row.createCell(COL_VISITS).setCellValue(
                joinCounts(
                    stockReturnListDTO.getVisits(),
                    v -> v.getTotalCount().toString()
                )
            );

            row.createCell(COL_ORDERS).setCellValue(
                joinCounts(
                    stockReturnListDTO.getOrders(),
                    o -> o.getTotalCount().toString()
                )
            );

            row.createCell(COL_ORDER_TOTAL).setCellValue(stockReturnListDTO.getOrderTotalPrice() != null ? stockReturnListDTO.getOrderTotalPrice().toString() : "");

            row.createCell(COL_PAYMENTS).setCellValue(
                joinCounts(
                    stockReturnListDTO.getPayments(),
                    p -> p.getTotalPrice().toString()
                )
            );

            row.createCell(COL_PRODUCTS).setCellValue(
                joinCounts(
                    stockReturnListDTO.getProducts(),
                    p -> p.getTotalCount().toString()
                )
            );

            int cellIndex = COL_CATEGORIES_START;

            if (!CollectionUtils.isEmpty(stockReturnListDTO.getCategories())) {
                for (var categoryDTO : stockReturnListDTO.getCategories()) {

                    String units = categoryDTO.getUnits().stream()
                        .map(u -> u.getTotalCount() + "/" + u.getUnit().getName())
                        .collect(Collectors.joining(", "));

                    row.createCell(cellIndex++)
                        .setCellValue(categoryDTO.getCategory().getName() + " " + units);
                }
            }
        }
        return workbook;
    }

    private static <T> String joinCounts(
        Collection<T> items,
        Function<T, String> mapper
    ) {
        if (CollectionUtils.isEmpty(items)) {
            return "";
        }
        return items.stream()
            .map(mapper)
            .collect(Collectors.joining("/"));
    }
}
