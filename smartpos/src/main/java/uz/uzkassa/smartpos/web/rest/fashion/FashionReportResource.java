package uz.uzkassa.smartpos.web.rest.fashion;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.fashion.FashionReportByCategoryStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.FashionReportByOneProductStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.FashionReportByProductStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.report.*;
import uz.uzkassa.smartpos.filter.FashionReportFilter;
import uz.uzkassa.smartpos.service.fashion.FashionReportService;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fashion/cabinet/report")
public class FashionReportResource {
    private final FashionReportService reportService;

    @GetMapping("/product")
    public Page<FashionReportByProductDto> reportByProduct(@ParameterObject FashionReportFilter filter) {
        return reportService.reportByProduct(filter);
    }

    @GetMapping("/product/excel")
    public ResponseEntity<Resource> reportByProductExcel(@ParameterObject FashionReportFilter filter) {
        return ServerUtils.getResponseEntity(reportService.reportByProductExcel(filter), "отчет по продукту");
    }

    @GetMapping("/product/statistics")
    public FashionReportByProductStatisticsDto reportByProductStatistics(@ParameterObject FashionReportFilter filter) {
        return reportService.reportByProductStatistics(filter);
    }

    @GetMapping("/color-groups")
    public List<FashionReportByColorGroupDto> reportByColorGroup(@ParameterObject FashionReportFilter filter) {
        return reportService.reportByColorGroup(filter);
    }

    @GetMapping("/product/one")
    public List<FashionReportByOneProductResultDto> reportByOneProduct(@ParameterObject FashionReportFilter filter) {
        return reportService.reportByOneProduct(filter);
    }

    @GetMapping("/product/one/excel")
    public ResponseEntity<Resource> reportByOneProductExcel(@ParameterObject FashionReportFilter filter) {
        return ServerUtils.getResponseEntity(reportService.reportByOneProductExcel(filter), "отчет по одному продукту");
    }

    @GetMapping("/product/one/statistics")
    public FashionReportByOneProductStatisticsDto reportByOneProductStatistics(@ParameterObject FashionReportFilter filter) {
        return reportService.reportByOneProductStatistics(filter);
    }

    @GetMapping("/category")
    public List<FashionReportByCategoryResultDto> reportByCategory(@ParameterObject FashionReportFilter filter) {
        return reportService.reportByCategory(filter);
    }

    @GetMapping("/category/excel")
    public ResponseEntity<Resource> reportByCategoryExcel(@ParameterObject FashionReportFilter filter) {
        return ServerUtils.getResponseEntity(reportService.reportByCategoryExcel(filter), "отчет по категории");
    }

    @GetMapping("/category/statistics")
    public FashionReportByCategoryStatisticsDto reportByCategoryStatistics(@ParameterObject FashionReportFilter filter) {
        return reportService.reportByCategoryStatistics(filter);
    }

    @GetMapping("/users")
    public Page<FashionReportByUserDto> reportByUser(@ParameterObject FashionReportFilter filter) {
        return reportService.reportByUser(filter);
    }

    @GetMapping("/users/statistics")
    public FashionReportByUserStatisticsDto reportByUserStatistics(@ParameterObject FashionReportFilter filter) {
        return reportService.reportByUserStatistics(filter);
    }
}
