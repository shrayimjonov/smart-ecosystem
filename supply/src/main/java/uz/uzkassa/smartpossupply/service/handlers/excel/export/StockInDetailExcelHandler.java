package uz.uzkassa.smartpossupply.service.handlers.excel.export;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.filters.StockProductFilter;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationRepository;
import uz.uzkassa.smartpossupply.repository.StockProductMarkRepository;

import java.util.*;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.12.2022 18:57
 */
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockInDetailExcelHandler extends AbstractExcelHandler<StockProductFilter> {

    StockProductAggregationRepository stockProductAggregationRepository;
    StockProductMarkRepository stockProductMarkRepository;
    StockProductAggregationBlockRepository stockProductAggregationBlockRepository;

    @Override
    public String getFileName(StockProductFilter filter) {
        return "Stock_In_Detail.xlsx";
    }

    @Override
    protected LinkedList<String> getHeaders() {
        return new LinkedList<>();
    }

    @Override
    protected XSSFWorkbook getXSSFWorkbook(StockProductFilter filter) {
        XSSFWorkbook workbook = createWorkbook();
        XSSFSheet sheet = createSheet(workbook, getFileName(filter));
        createHeader(getHeaders(), sheet);
        int rowIndex = 0;
        for (String code : getList(filter)) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(code);
        }
        return workbook;
    }

    public List<String> getList(StockProductFilter filter) {
        Map<CodeType, List<String>> result = new HashMap<>();
        result.put(
            CodeType.AGGREGATION,
            new ArrayList<>(stockProductAggregationRepository.findAllCodesByStockProductIdAndDeletedIsFalse(filter.getStockProductId()))
        );
        if (!CollectionUtils.isEmpty(result)) {
            return result.get(CodeType.AGGREGATION);
        }

        result.put(
            CodeType.MARK,
            new ArrayList<>(stockProductMarkRepository.findAllCodesByStockProductIdAndDeletedIsFalse(filter.getStockProductId()))
        );
        if (!CollectionUtils.isEmpty(result)) {
            return result.get(CodeType.MARK);
        }
        result.put(
            CodeType.BLOCK,
            new ArrayList<>(stockProductAggregationBlockRepository.findAllCodesByStockProductIdAndDeletedIsFalse(filter.getStockProductId()))
        );
        return result.get(CodeType.BLOCK);
    }
}
