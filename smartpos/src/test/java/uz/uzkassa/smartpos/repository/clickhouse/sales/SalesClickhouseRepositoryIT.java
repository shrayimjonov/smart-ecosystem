package uz.uzkassa.smartpos.repository.clickhouse.sales;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import uz.uzkassa.smartpos.base.BaseClickhouseIT;
import uz.uzkassa.smartpos.config.JacksonConfiguration;
import uz.uzkassa.smartpos.config.clickhouse.ClickhouseConfig;
import uz.uzkassa.smartpos.dto.SalesDynamicsDto;
import uz.uzkassa.smartpos.dto.analytics.ReceiptStatDto;
import uz.uzkassa.smartpos.dto.clickhouse.SalesClickhouseInfoDto;
import uz.uzkassa.smartpos.dto.receipt.CreditPaymentDTO;
import uz.uzkassa.smartpos.dto.sales.SalesReportStatsDto;
import uz.uzkassa.smartpos.dto.sales.SalesTopBranchDto;
import uz.uzkassa.smartpos.filter.SalesFilter;
import uz.uzkassa.smartpos.repository.clickhouse.sales.impl.SalesClickhouseRepositoryImpl;

import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ActiveProfiles("test")
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@SpringBootTest(classes = {SalesClickhouseRepositoryImpl.class, ClickhouseConfig.class, JacksonConfiguration.class})
class SalesClickhouseRepositoryIT extends BaseClickhouseIT {
    @Autowired
    SalesClickhouseRepository salesClickhouseRepository;

    @Test
    void statistics() {
        var salesFilter = Instancio.of(SalesFilter.class)
                .generate(field(SalesFilter::getFrom), generators -> generators.temporal().localDateTime().past())
                .generate(field(SalesFilter::getTo), generators -> generators.temporal().localDateTime().future())
                .generate(field(SalesFilter::getOrderBy), generators -> generators.oneOf(List.of("id", "createdDateTime")))
                .create();
        ReceiptStatDto statistics = salesClickhouseRepository.statistics(salesFilter);
        assertNotNull(statistics);
    }

    @Test
    void getSalesList() {
        var salesFilter = Instancio.of(SalesFilter.class)
                .generate(field(SalesFilter::getFrom), generators -> generators.temporal().localDateTime().past())
                .generate(field(SalesFilter::getTo), generators -> generators.temporal().localDateTime().future())
                .generate(field(SalesFilter::getOrderBy), generators -> generators.oneOf(List.of("id", "createdDateTime")))
                .create();
        Page<SalesClickhouseInfoDto> salesList = salesClickhouseRepository.getSalesList(salesFilter);
        assertNotNull(salesList);
    }

    @Test
    void findById() {
        Optional<SalesClickhouseInfoDto> info = salesClickhouseRepository.findById(-1L);
        assertTrue(info.isEmpty());
    }

    @Test
    void findFirstOriginByUid() {
        Optional<SalesClickhouseInfoDto> info = salesClickhouseRepository.findFirstOriginByUid("test");
        assertTrue(info.isEmpty());
    }

    @Test
    void topBranches() {
        var salesFilter = Instancio.of(SalesFilter.class)
                .generate(field(SalesFilter::getFrom), generators -> generators.temporal().localDateTime().past())
                .generate(field(SalesFilter::getTo), generators -> generators.temporal().localDateTime().future())
                .generate(field(SalesFilter::getOrderBy), generators -> generators.oneOf(List.of("branchId")))
                .create();
        List<SalesTopBranchDto> topBranches = salesClickhouseRepository.topBranches(salesFilter);
        assertNotNull(topBranches);
    }

    @Test
    void salesDynamics() {
        var salesFilter = Instancio.of(SalesFilter.class)
                .generate(field(SalesFilter::getFrom), generators -> generators.temporal().localDateTime().past())
                .generate(field(SalesFilter::getTo), generators -> generators.temporal().localDateTime().future())
                .generate(field(SalesFilter::getOrderBy), generators -> generators.oneOf(List.of("id", "createdDateTime")))
                .create();
        List<SalesDynamicsDto> salesDynamics = salesClickhouseRepository.salesDynamics(salesFilter);
        assertNotNull(salesDynamics);
    }

    @Test
    void getSalesReportStats() {
        var salesFilter = Instancio.of(SalesFilter.class)
                .generate(field(SalesFilter::getFrom), generators -> generators.temporal().localDateTime().past())
                .generate(field(SalesFilter::getTo), generators -> generators.temporal().localDateTime().future())
                .generate(field(SalesFilter::getOrderBy), generators -> generators.oneOf(List.of("id", "createdDateTime")))
                .create();
        SalesReportStatsDto salesReportStats = salesClickhouseRepository.getSalesReportStats(salesFilter);
        assertNotNull(salesReportStats);
    }

    @Test
    void findAllCreditPayments() {
        List<CreditPaymentDTO> creditPayments = salesClickhouseRepository.findAllCreditPayments("test");
        assertNotNull(creditPayments);
    }
}