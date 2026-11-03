package uz.uzkassa.smartpos.repository.clickhouse.report;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uz.uzkassa.smartpos.base.BaseClickhouseIT;
import uz.uzkassa.smartpos.config.JacksonConfiguration;
import uz.uzkassa.smartpos.config.clickhouse.ClickhouseConfig;
import uz.uzkassa.smartpos.dto.fashion.FashionReportByOneProductStatisticsDto;
import uz.uzkassa.smartpos.filter.FashionReportFilter;
import uz.uzkassa.smartpos.repository.clickhouse.impl.report.FashionReportRepositoryImpl;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@SpringBootTest(classes = {FashionReportRepositoryImpl.class, ClickhouseConfig.class, JacksonConfiguration.class})
class FashionReportRepositoryIT extends BaseClickhouseIT {
    @Autowired
    FashionReportRepositoryImpl repository;

    @Test
    void reportByProduct() {
        var filter = Instancio.of(FashionReportFilter.class)
                .generate(field(FashionReportFilter::getFrom), gen -> gen.temporal().localDateTime().past())
                .generate(field(FashionReportFilter::getTo), gen -> gen.temporal().localDateTime().future())
                .generate(field(FashionReportFilter::getOrderBy), gen -> gen.oneOf(
                        "productId",
                        "productName",
                        "categoryName",
                        "avgSalesPrice",
                        "salesCount",
                        "totalDiscount",
                        "totalVat",
                        "totalSumma",
                        "salesReceiptsCount",
                        "returnedReceiptsCount"
                ))
                .create();
        assertNotNull(repository.reportByProduct(filter));
    }

    @Test
    void reportByColorGroup() {
        FashionReportFilter filter = Instancio.of(FashionReportFilter.class)
                .generate(field(FashionReportFilter::getFrom), gen -> gen.temporal().localDateTime().past())
                .generate(field(FashionReportFilter::getTo), gen -> gen.temporal().localDateTime().future())
                .generate(field(FashionReportFilter::getOrderBy), gen -> gen.oneOf("colorGroupId",
                        "colorCode",
                        "colorName",
                        "printName",
                        "avgSalesPrice",
                        "salesCount",
                        "totalDiscount",
                        "totalVat",
                        "totalSumma",
                        "salesReceiptsCount",
                        "returnedReceiptsCount"
                ))
                .create();
        assertNotNull(repository.reportByColorGroup(filter));
    }

    @Test
    void reportByOneProduct() {
        FashionReportFilter filter = Instancio.of(FashionReportFilter.class)
                .generate(field(FashionReportFilter::getFrom), gen -> gen.temporal().localDateTime().past())
                .generate(field(FashionReportFilter::getTo), gen -> gen.temporal().localDateTime().future())
                .generate(field(FashionReportFilter::getOrderBy), gen -> gen.oneOf(
                        "colorGroupId",
                        "colorCode",
                        "colorName",
                        "printName",
                        "itemId",
                        "sizeCode",
                        "sizeValue",
                        "avgSalesPrice",
                        "salesCount",
                        "totalDiscount",
                        "totalVat",
                        "totalSumma",
                        "salesReceiptsCount",
                        "returnedReceiptsCount",
                        "branchId",
                        "branchName"
                ))
                .create();
        assertNotNull(repository.reportByOneProduct(filter));
    }

    @Test
    void reportByCategory() {
        assertNotNull(repository.reportByCategory(generateFilter()));
    }

    @Test
    void reportByUser() {
        var filter = Instancio.of(FashionReportFilter.class)
                .generate(field(FashionReportFilter::getFrom), gen -> gen.temporal().localDateTime().past())
                .generate(field(FashionReportFilter::getTo), gen -> gen.temporal().localDateTime().future())
                .generate(field(FashionReportFilter::getOrderBy), gen -> gen.oneOf(
                        "userId",
                        "userFullName",
                        "totalSumma",
                        "totalDiscount",
                        "avgSumma",
                        "receiptsCount",
                        "branchId",
                        "branchName"
                ))
                .create();
        assertNotNull(repository.reportByUser(filter));
    }

    @Test
    void reportByUserStatistics() {
        assertNotNull(repository.reportByUserStatistics(generateFilter()));
    }

    @Test
    void reportByProductStatistics() {
        assertNotNull(repository.reportByProductStatistics(generateFilter()));
    }

    @Test
    void reportByCategoryStatistics() {
        assertNotNull(repository.reportByCategoryStatistics(generateFilter()));
    }

    @Test
    void reportByOneProductStatistics() {
        FashionReportByOneProductStatisticsDto dto = repository.reportByOneProductStatistics(generateFilter());
        assertNotNull(dto);
    }

    private FashionReportFilter generateFilter() {
        return Instancio.of(FashionReportFilter.class)
                .generate(field(FashionReportFilter::getFrom), gen -> gen.temporal().localDateTime().past())
                .generate(field(FashionReportFilter::getTo), gen -> gen.temporal().localDateTime().future())
                .generate(field(FashionReportFilter::getOrderBy), gen -> gen.oneOf("id"))
                .create();
    }
}