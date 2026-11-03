package uz.uzkassa.smartpos.repository.clickhouse.receipt;

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
import uz.uzkassa.smartpos.dto.filter.ShiftFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.CustomerReceiptsFilter;
import uz.uzkassa.smartpos.filter.ShiftReportFilter;
import uz.uzkassa.smartpos.repository.clickhouse.impl.receipt.ClickhouseReceiptReportByBranchImpl;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@SpringBootTest(classes = {ClickhouseReceiptReportByBranchImpl.class, ClickhouseConfig.class, JacksonConfiguration.class})
class ClickhouseReceiptReportByBranchIT extends BaseClickhouseIT {
    @Autowired
    ClickhouseReceiptReportByBranchImpl repository;

    @Test
    void getReceiptsAmountByBranch() {
        assertNotNull(repository.getReceiptsAmountByBranch(generateCustomReceiptsFilter()));
    }

    @Test
    void getReceiptsAmountByBranchStat() {
        assertNotNull(repository.getReceiptsAmountByBranchStat(generateCustomReceiptsFilter()));
    }

    @Test
    void getStatsForZReport() {
        assertNotNull(repository.getStatsForZReport(generateShiftFilter()));
    }

    @Test
    void getShiftStats() {
        assertNotNull(repository.getStats(generateShiftFilter()));
    }

    @Test
    void getShiftReportStats() {
        assertNotNull(repository.getStats(generateShiftReportFilter()));
    }

    private CustomerReceiptsFilter generateCustomReceiptsFilter() {
        return Instancio.of(CustomerReceiptsFilter.class)
                .generate(field(CustomerReceiptsFilter::getFrom), gen -> gen.temporal().localDateTime().past())
                .generate(field(CustomerReceiptsFilter::getTo), gen -> gen.temporal().localDateTime().future())
                .generate(field(CustomerReceiptsFilter::getOrderBy), gen -> gen.oneOf("id"))
                .create();
    }

    private ShiftFilter generateShiftFilter() {
        return Instancio.of(ShiftFilter.class)
                .generate(field(ShiftFilter::getFrom), gen -> gen.temporal().localDateTime().past())
                .generate(field(ShiftFilter::getTo), gen -> gen.temporal().localDateTime().future())
                .generate(field(ShiftFilter::getOrderBy), gen -> gen.oneOf("id"))
                .create();
    }

    private ShiftReportFilter generateShiftReportFilter() {
        return Instancio.of(ShiftReportFilter.class)
                .generate(field(ShiftReportFilter::getFrom), gen -> gen.temporal().localDateTime().past())
                .generate(field(ShiftReportFilter::getTo), gen -> gen.temporal().localDateTime().future())
                .create();
    }

}
