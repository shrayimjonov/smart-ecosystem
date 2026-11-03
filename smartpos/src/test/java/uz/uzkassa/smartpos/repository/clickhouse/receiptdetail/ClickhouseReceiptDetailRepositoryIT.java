package uz.uzkassa.smartpos.repository.clickhouse.receiptdetail;

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
import uz.uzkassa.smartpos.dto.filter.receipt.SalesDynamicsFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;
import uz.uzkassa.smartpos.repository.clickhouse.impl.receiptdetail.ClickhouseReceiptDetailRepositoryImpl;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@SpringBootTest(classes = {ClickhouseReceiptDetailRepositoryImpl.class, ClickhouseConfig.class, JacksonConfiguration.class})
class ClickhouseReceiptDetailRepositoryIT extends BaseClickhouseIT {
    @Autowired
    ClickhouseReceiptDetailRepositoryImpl repository;

    @Test
    void getProductSalesStatsByBranch() {
        assertNotNull(repository.getProductSalesStatsByBranch(generateSalesStatsFilter(), false));
    }

    @Test
    void getProductSalesStatsByCompany() {
        assertNotNull(repository.getProductSalesStatsByCompany(generateSalesStatsFilter(), false));
    }

    @Test
    void getProductSalesStatsHeader() {
        assertNotNull(repository.getProductSalesStatsHeader(generateSalesStatsFilter()));
    }

    @Test
    void getProductSalesDynamics() {
        assertNotNull(repository.getProductSalesDynamics(generateSalesDynamicsFilter()));
    }

    @Test
    void getTopProducts() {
        assertNotNull(repository.getTopProducts(generateSalesStatsFilter()));
    }

    @Test
    void getCategorySalesStats() {
        assertNotNull(repository.getCategorySalesStats(generateSalesStatsFilter()));
    }

    @Test
    void findCreditAndAdvanceDetails() {
        assertNotNull(repository.findCreditDetails(-1L));
        assertNotNull(repository.findAdvanceDetails(-1L));
    }

    private SalesStatsFilter generateSalesStatsFilter() {
        return Instancio.of(SalesStatsFilter.class)
                .generate(field(SalesStatsFilter::getFrom), gen -> gen.temporal().localDateTime().past())
                .generate(field(SalesStatsFilter::getTo), gen -> gen.temporal().localDateTime().future())
                .generate(field(SalesStatsFilter::getOrderBy), gen -> gen.oneOf("id"))
                .create();
    }

    private SalesDynamicsFilter generateSalesDynamicsFilter() {
        return Instancio.of(SalesDynamicsFilter.class)
                .generate(field(SalesDynamicsFilter::getFrom), gen -> gen.temporal().localDateTime().past())
                .generate(field(SalesDynamicsFilter::getTo), gen -> gen.temporal().localDateTime().future())
                .generate(field(SalesDynamicsFilter::getOrderBy), gen -> gen.oneOf("id"))
                .create();
    }
}