package uz.uzkassa.silen.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.dto.filter.UnitCodesReportFilter;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class UnitCodesExcel extends BaseExcelExport implements IExportHandler {

    private final BlockRepository blockRepository;
    private final MarkRepository markRepository;
    static int palletIndex = 0;
    static int boxIndex = 1;
    static int blockIndex = 2;
    static int markIndex = 3;
    private final AggregationRepository aggregationRepository;

    @Override
    public ExportReportType getReportType() {
        return ExportReportType.EXCEL_UNIT_CODES_REPORT;
    }

    @Override
    protected void writeHeader(ExportReport exportReport, XSSFSheet sheet) {
        sheet.setHorizontallyCenter(true);
        sheet.setVerticallyCenter(true);
        XSSFRow row = sheet.createRow(0);
        row.createCell(palletIndex).setCellValue("LEVEL3");
        row.createCell(boxIndex).setCellValue("LEVEL2");
        row.createCell(blockIndex).setCellValue("LEVEL1");
        row.createCell(markIndex).setCellValue("UNIT");
    }

    @Override
    protected void writeBody(ExportReport exportReport, XSSFSheet sheet) {

        final UnitCodesReportFilter filter = readFilter(exportReport.getFilters(), UnitCodesReportFilter.class);
        if (PackageType.BLOCK.equals(filter.getUnit())) {
            MongoFilter mongoFilter = new MongoFilter();
            mongoFilter.setUnit(filter.getUnit());
            mongoFilter.setFrom(filter.getFrom());
            mongoFilter.setTo(filter.getTo());
            mongoFilter.setLogin(filter.getLogin());
            mongoFilter.setOrganizationId(filter.getOrganizationId());
            mongoFilter.setPartyAggregationId(filter.getPartyAggregationId());
            mongoFilter.setAggregationStatus(filter.getAggregationStatus());
            mongoFilter.setPartyId(filter.getProductId());
            mongoFilter.setProductId(filter.getProductId());
            mongoFilter.setSerialId(filter.getSerialId());
            mongoFilter.setSortOrder(filter.getSortOrder());
            mongoFilter.setPage(filter.getPage());
            mongoFilter.setOrderBy(filter.getOrderBy());
            mongoFilter.setType(filter.getType());


            int rowIndex = 1;
            boolean hasNext;
            int pageNumber = 0;
            do {
                filter.setPage(pageNumber++);
                final Page<Block> allByFilter = blockRepository.findAllByFilter(mongoFilter);
                for (Block block : allByFilter) {
                    XSSFRow row = sheet.createRow(rowIndex++);
                    XSSFCell cell = row.createCell(blockIndex);
                    cell.setCellValue(block.getPrintCode());
                    for (Mark mark : markRepository.findAllByParentCode(block.getCode())) {
                        String markCode = mark.getPrintCode();
                        XSSFRow markRow = sheet.createRow(rowIndex++);
                        XSSFCell cell10 = markRow.createCell(markIndex);
                        cell10.setCellValue(markCode);
                    }
                }
                hasNext = allByFilter.hasNext();
            } while (hasNext);
        }

        AggregationFilter aggregationFilter = new AggregationFilter();
        aggregationFilter.setUnit(filter.getUnit());
        aggregationFilter.setFrom(filter.getFrom());
        aggregationFilter.setTo(filter.getTo());
        aggregationFilter.setLogin(filter.getLogin());
        aggregationFilter.setOrganizationId(filter.getOrganizationId());
        aggregationFilter.setPartyAggregationId(filter.getPartyAggregationId());
        aggregationFilter.setAggregationStatus(filter.getAggregationStatus());
        aggregationFilter.setProductId(filter.getProductId());
        aggregationFilter.setSerialId(filter.getSerialId());
        aggregationFilter.setSortOrder(filter.getSortOrder());
        aggregationFilter.setPage(filter.getPage());
        aggregationFilter.setOrderBy(filter.getOrderBy());
        aggregationFilter.setType(filter.getType());
        int rowIndex = 1;
        boolean hasNextLevel1;
        int pageNumber = 0;
        do {
            filter.setPage(pageNumber++);
            final Page<Aggregation> allByFilter = aggregationRepository.findAllByFilter(aggregationFilter);
            for (Aggregation aggregation : allByFilter) {
                final String aggregationCode = aggregation.getCode();
                XSSFRow row = sheet.createRow(rowIndex++);

                if (PackageType.BOX.getCode() == aggregation.getUnit()) {
                    XSSFCell boxCell = row.createCell(boxIndex);
                    boxCell.setCellValue(aggregation.getPrintCode());
                    for (Mark markOrBlock : markRepository.findAllByParentCode(aggregationCode)) {
                        if (markOrBlock.isBlock()) {
                            XSSFRow markOrBlockRow = sheet.createRow(rowIndex++);
                            XSSFCell markOrBlockRowCell = markOrBlockRow.createCell(blockIndex);
                            markOrBlockRowCell.setCellValue(markOrBlock.getPrintCode());
                            for (Mark markInBlock : markRepository.findAllByParentCode(markOrBlock.getCode())) {
                                XSSFRow markInBlockRow = sheet.createRow(rowIndex++);
                                XSSFCell markInBlockCell = markInBlockRow.createCell(markIndex);
                                markInBlockCell.setCellValue(markInBlock.getPrintCode());
                            }
                        } else {
                            XSSFRow markInBoxRow = sheet.createRow(rowIndex++);
                            XSSFCell markInBoxRowCell = markInBoxRow.createCell(markIndex);
                            markInBoxRowCell.setCellValue(markOrBlock.getPrintCode());
                        }
                    }
                } else {
                    // here only pallet
                    XSSFCell palletCell = row.createCell(palletIndex);
                    palletCell.setCellValue(aggregation.getPrintCode());

                    List<Aggregation> allByParentCode = aggregationRepository.findAllByParentCode(aggregationCode);
                    if (CollectionUtils.isNotEmpty(allByParentCode)) {
                        for (Aggregation boxCode : allByParentCode) {
                            XSSFRow boxRow = sheet.createRow(rowIndex++);
                            XSSFCell boxCell = boxRow.createCell(boxIndex);
                            boxCell.setCellValue(boxCode.getPrintCode());
                            for (Mark markOrBlock : markRepository.findAllByParentCode(boxCode.getCode())) {
                                if (markOrBlock.isBlock()) {
                                    XSSFRow markOrBlockRow = sheet.createRow(rowIndex++);
                                    XSSFCell markOrBlockCell = markOrBlockRow.createCell(blockIndex);
                                    markOrBlockCell.setCellValue(markOrBlock.getPrintCode());
                                    for (Mark markInBlock : markRepository.findAllByParentCode(markOrBlock.getCode())) {
                                        XSSFRow markInBlockRow = sheet.createRow(rowIndex++);
                                        XSSFCell markInBlockRowCell = markInBlockRow.createCell(markIndex);
                                        markInBlockRowCell.setCellValue(markInBlock.getPrintCode());
                                    }
                                } else {
                                    XSSFRow markRow = sheet.createRow(rowIndex++);
                                    XSSFCell markInBoxCell = markRow.createCell(markIndex);
                                    markInBoxCell.setCellValue(markOrBlock.getPrintCode());
                                }

                            }
                        }
                    } else {
                        for (Mark markOrBlock : markRepository.findAllByParentCode(aggregationCode)) {
                            if (markOrBlock.isBlock()) {
                                XSSFRow markOrBlockRow = sheet.createRow(rowIndex++);
                                XSSFCell markOrBlockCell = markOrBlockRow.createCell(blockIndex);
                                markOrBlockCell.setCellValue(markOrBlock.getPrintCode());
                                for (Mark markInBlock : markRepository.findAllByParentCode(markOrBlock.getCode())) {
                                    XSSFRow markRow = sheet.createRow(rowIndex++);
                                    XSSFCell markCell = markRow.createCell(markIndex);
                                    markCell.setCellValue(markInBlock.getPrintCode());
                                }
                            } else {
                                XSSFRow markRow = sheet.createRow(rowIndex++);
                                XSSFCell markRowCell = markRow.createCell(markIndex);
                                markRowCell.setCellValue(markOrBlock.getPrintCode());
                            }
                        }
                    }
                }
            }

            hasNextLevel1 = allByFilter.hasNext();
        } while (hasNextLevel1);

    }
}
