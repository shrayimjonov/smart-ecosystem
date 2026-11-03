package uz.uzkassa.smartpos.service.fashion;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranch;
import uz.uzkassa.smartpos.dto.fashion.FashionReportByCategoryStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.FashionReportByOneProductStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.FashionReportByProductStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.report.*;
import uz.uzkassa.smartpos.filter.FashionReportFilter;
import uz.uzkassa.smartpos.repository.clickhouse.report.FashionReportRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.CategoryBranchRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.service.SmartposService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class FashionReportService extends SmartposService {
    private final FashionReportRepository fashionReportRepository;
    private final CategoryBranchRepository categoryBranchRepository;

    public Page<FashionReportByProductDto> reportByProduct(FashionReportFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return fashionReportRepository.reportByProduct(filter);
    }

    public ByteArrayResource reportByProductExcel(FashionReportFilter filter) {
        filter.setPage(0);
        filter.setSize(10000);
        Page<FashionReportByProductDto> page = reportByProduct(filter);
        try (var workbook = new XSSFWorkbook(); var out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet();
            var boldStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            boldStyle.setFont(font);
            var rowIndex = new AtomicInteger(0);
            createHeader(sheet.createRow(rowIndex.getAndIncrement()), List.of("Продукция", "Категория", "Стоимость", "Продано", "Скидка", "НДС", "Сумма", "Кол-во чеков", "Возврат"), boldStyle);
            page.getContent().forEach(report -> {
                var row = sheet.createRow(rowIndex.getAndIncrement());
                var cell = row.createCell(0);
                cell.setCellValue(report.getProductName());
                cell = row.createCell(1);
                cell.setCellValue(report.getCategoryName());
                cell = row.createCell(2);
                cell.setCellValue(report.getAvgSalesPrice().toString());
                cell = row.createCell(3);
                cell.setCellValue(report.getSalesCount());
                cell = row.createCell(4);
                cell.setCellValue(report.getTotalDiscount().toString());
                cell = row.createCell(5);
                cell.setCellValue(report.getTotalVat().toString());
                cell = row.createCell(6);
                cell.setCellValue(report.getTotalSumma().toString());
                cell = row.createCell(7);
                cell.setCellValue(report.getSalesReceiptsCount());
                cell = row.createCell(8);
                cell.setCellValue(report.getReturnedReceiptsCount());
            });
            IntStream.range(0, 9).forEach(sheet::autoSizeColumn);
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<FashionReportByColorGroupDto> reportByColorGroup(FashionReportFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return fashionReportRepository.reportByColorGroup(filter);
    }

    public List<FashionReportByOneProductResultDto> reportByOneProduct(FashionReportFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        List<FashionReportByOneProductDto> reports = fashionReportRepository.reportByOneProduct(filter);
        Map<Pair<Long, Long>, FashionReportByOneProductResultDto> map = new HashMap<>();
        reports.forEach(report -> {
            Pair<Long, Long> key = Pair.of(report.getColorGroupId(), report.getBranchId());
            if (!map.containsKey(key)) {
                map.put(key, new FashionReportByOneProductResultDto(report));
            }
            var dto = map.get(key);
            dto.addItem(new FashionReportByOneProductItemDto(report));
        });
        return new ArrayList<>(map.values());
    }

    public ByteArrayResource reportByOneProductExcel(FashionReportFilter filter) {
        filter.setPage(0);
        filter.setSize(10000);
        List<FashionReportByOneProductResultDto> reports = reportByOneProduct(filter);
        try (var workbook = new XSSFWorkbook(); var outputStream = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet();
            var boldStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            boldStyle.setFont(font);
            createHeader(sheet.createRow(0), List.of("Название","Филиал", "Продано", "Стоимость", "Сумма", "Скидка", "Кол-во чеков", "НДС", "Возврат"), boldStyle);
            AtomicInteger rowIndex = new AtomicInteger(1);
            reports.forEach(report -> {
                report.getItems().forEach(item -> {
                    var row = sheet.createRow(rowIndex.getAndIncrement());
                    var cell = row.createCell(0);
                    cell.setCellValue(item.getSizeValue());
                    cell = row.createCell(1);
                    cell.setCellValue(report.getBranchName());
                    cell = row.createCell(2);
                    cell.setCellValue(item.getSalesCount());
                    cell = row.createCell(3);
                    cell.setCellValue(item.getAvgSalesPrice().toString());
                    cell = row.createCell(4);
                    cell.setCellValue(item.getTotalSumma().toString());
                    cell = row.createCell(5);
                    cell.setCellValue(item.getTotalDiscount().toString());
                    cell = row.createCell(6);
                    cell.setCellValue(item.getSalesReceiptsCount());
                    cell = row.createCell(7);
                    cell.setCellValue(item.getTotalVat().toString());
                    cell = row.createCell(8);
                    cell.setCellValue(item.getReturnedReceiptsCount());
                });
                var row = sheet.createRow(rowIndex.getAndIncrement());
                var cell = row.createCell(0);
                cell.setCellValue(report.getColorName() + Optional.ofNullable(report.getPrintName()).map(s -> " " + s).orElse(""));
                cell = row.createCell(1);
                cell.setCellValue(report.getBranchName());
                cell = row.createCell(2);
                cell.setCellValue(report.getSalesCount());
                cell = row.createCell(3);
                cell.setCellValue(report.getAvgSalesPrice().toString());
                cell = row.createCell(4);
                cell.setCellValue(report.getTotalSumma().toString());
                cell = row.createCell(5);
                cell.setCellValue(report.getTotalDiscount().toString());
                cell = row.createCell(6);
                cell.setCellValue(report.getSalesReceiptsCount());
                cell = row.createCell(7);
                cell.setCellValue(report.getTotalVat().toString());
                cell = row.createCell(8);
                cell.setCellValue(report.getReturnedReceiptsCount());
                sheet.createRow(rowIndex.getAndIncrement());

            });
            IntStream.range(0, 8).forEach(sheet::autoSizeColumn);
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<FashionReportByCategoryResultDto> reportByCategory(FashionReportFilter filter) {
        if (CollectionUtils.size(filter.getBranchIds()) != 1) {
            filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()));
        }
        List<CategoryBranch> categoryBranches = categoryBranchRepository.findAllByBranchIdAndEnabledIsTrue(filter.getBranchIds().stream().findFirst().orElseThrow());
        List<FashionReportByCategoryDto> reportByCategory = fashionReportRepository.reportByCategory(filter);
        Map<Long, FashionReportByCategoryDto> reportMap = reportByCategory.stream().collect(Collectors.toMap(FashionReportByCategoryDto::getCategoryId, Function.identity()));
        var categoryMap = categoryBranches.stream().map(categoryBranch -> {
            FashionReportByCategoryResultDto resultDto;
            if (reportMap.containsKey(categoryBranch.getCategoryId())) {
                resultDto = new FashionReportByCategoryResultDto(reportMap.get(categoryBranch.getCategoryId()));
            } else {
                resultDto = new FashionReportByCategoryResultDto();
            }
            resultDto.setCategoryId(categoryBranch.getCategoryId());
            resultDto.setCategoryName(categoryBranch.getName());
            resultDto.setParentId(categoryBranch.getParentId());
            return resultDto;
        }).collect(Collectors.toMap(FashionReportByCategoryResultDto::getCategoryId, Function.identity()));
        var result = new ArrayList<FashionReportByCategoryResultDto>();
        categoryMap.values().forEach(resultDto -> {
            if (resultDto.getParentId() == null || !categoryMap.containsKey(resultDto.getParentId())) {
                result.add(resultDto);
            } else {
                categoryMap.get(resultDto.getParentId()).addChild(resultDto);
            }
        });
        return result.stream().peek(this::calculate).toList();
    }

    public ByteArrayResource reportByCategoryExcel(FashionReportFilter filter) {
        filter.setPage(0);
        filter.setSize(10000);
        List<FashionReportByCategoryResultDto> reports = reportByCategory(filter);
        try (var workbook = new XSSFWorkbook(); var outputStream = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet();
            var boldStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            boldStyle.setFont(font);
            var rowIndex = new AtomicInteger(0);
            AtomicInteger cellIndex = new AtomicInteger(0);
            createHeader(sheet.createRow(rowIndex.getAndIncrement()), List.of("Название", "Количество", "Сумма", "НДС", "Скидка", "Кол-во чеков"), boldStyle);
            writeRecursive(sheet, reports, rowIndex, cellIndex);
            IntStream.range(0, 6).forEach(sheet::autoSizeColumn);
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeRecursive(XSSFSheet sheet, List<FashionReportByCategoryResultDto> reports, AtomicInteger rowIndex, AtomicInteger cellIndex) {
        reports.forEach(report -> {
            var row = sheet.createRow(rowIndex.getAndIncrement());
            var cell = row.createCell(cellIndex.getAndIncrement());
            cell.setCellValue(report.getCategoryName());
            cell = row.createCell(cellIndex.getAndIncrement());
            cell.setCellValue(report.getSalesCount());
            cell = row.createCell(cellIndex.getAndIncrement());
            cell.setCellValue(report.getTotalSumma().toString());
            cell = row.createCell(cellIndex.getAndIncrement());
            cell.setCellValue(report.getTotalVat().toString());
            cell = row.createCell(cellIndex.getAndIncrement());
            cell.setCellValue(report.getTotalDiscount().toString());
            cell = row.createCell(cellIndex.getAndIncrement());
            cell.setCellValue(report.getSalesReceiptsCount());
            cellIndex.set(0);
            writeRecursive(sheet, report.getChildren(), rowIndex, cellIndex);
        });
    }

    public Page<FashionReportByUserDto> reportByUser(FashionReportFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return fashionReportRepository.reportByUser(filter);
    }

    public FashionReportByUserStatisticsDto reportByUserStatistics(FashionReportFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return fashionReportRepository.reportByUserStatistics(filter);
    }

    private FashionReportByCategoryDto calculate(FashionReportByCategoryResultDto dto) {
        if (dto.getChildren().isEmpty()) {
            return dto;
        }
        FashionReportByCategoryDto result = dto.getChildren().stream().map(this::calculate).reduce(this::mergeReportCategoryDtos).orElseGet(FashionReportByCategoryResultDto::new);
        dto.setSalesCount(dto.getSalesCount() + result.getSalesCount());
        dto.setTotalDiscount(dto.getTotalDiscount().add(result.getTotalDiscount()));
        dto.setTotalVat(dto.getTotalVat().add(result.getTotalVat()));
        dto.setTotalSumma(dto.getTotalSumma().add(result.getTotalSumma()));
        dto.setSalesReceiptsCount(dto.getSalesReceiptsCount() + result.getSalesReceiptsCount());
        dto.setReturnedReceiptsCount(dto.getReturnedReceiptsCount() + result.getReturnedReceiptsCount());
        return dto;
    }

    @NotNull
    private FashionReportByCategoryDto mergeReportCategoryDtos(FashionReportByCategoryDto first, FashionReportByCategoryDto second) {
        FashionReportByCategoryDto result = new FashionReportByCategoryDto();
        result.setSalesCount(first.getSalesCount() + second.getSalesCount());
        result.setTotalDiscount(first.getTotalDiscount().add(second.getTotalDiscount()));
        result.setTotalVat(first.getTotalVat().add(second.getTotalVat()));
        result.setTotalSumma(first.getTotalSumma().add(second.getTotalSumma()));
        result.setSalesReceiptsCount(first.getSalesReceiptsCount() + second.getSalesReceiptsCount());
        result.setReturnedReceiptsCount(first.getReturnedReceiptsCount() + second.getReturnedReceiptsCount());
        return result;
    }

    public FashionReportByProductStatisticsDto reportByProductStatistics(FashionReportFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return fashionReportRepository.reportByProductStatistics(filter);
    }

    public FashionReportByCategoryStatisticsDto reportByCategoryStatistics(FashionReportFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return fashionReportRepository.reportByCategoryStatistics(filter);
    }

    public FashionReportByOneProductStatisticsDto reportByOneProductStatistics(FashionReportFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return fashionReportRepository.reportByOneProductStatistics(filter);
    }

    private void createHeader(Row row, List<String> headerTitles, CellStyle cellStyle) {
        AtomicInteger cellIndex = new AtomicInteger(0);
        headerTitles.forEach(title -> {
            Cell cell = row.createCell(cellIndex.getAndIncrement());
            cell.setCellValue(title);
            cell.setCellStyle(cellStyle);
        });
    }

}
