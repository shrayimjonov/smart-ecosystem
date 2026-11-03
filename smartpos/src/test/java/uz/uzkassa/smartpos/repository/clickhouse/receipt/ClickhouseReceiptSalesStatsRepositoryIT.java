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
import uz.uzkassa.smartpos.dto.analytics.EmployeeSalesStatDTO;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesDynamicsFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;
import uz.uzkassa.smartpos.repository.clickhouse.impl.receipt.ClickhouseReceiptSalesStatsRepositoryImpl;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@SpringBootTest(classes = {ClickhouseReceiptSalesStatsRepositoryImpl.class, ClickhouseConfig.class, JacksonConfiguration.class})
class ClickhouseReceiptSalesStatsRepositoryIT extends BaseClickhouseIT {
    @Autowired
    ClickhouseReceiptSalesStatsRepositoryImpl repository;

    @Test
    void getEmployeeSalesStat() {
        EmployeeSalesStatDTO employeeSalesStat = repository.getEmployeeSalesStat(generateSalesStatsFilter());
        assertNotNull(employeeSalesStat);
    }

    @Test
    void getSalesDynamic() {
        var salesDynamicsFilter = Instancio.of(SalesDynamicsFilter.class)
                .generate(field(SalesDynamicsFilter::getFrom), generators -> generators.temporal().localDateTime().past())
                .generate(field(SalesDynamicsFilter::getTo), generators -> generators.temporal().localDateTime().future())
                .generate(field(SalesDynamicsFilter::getOrderBy), generators -> generators.oneOf(List.of("id", "createdDateTime")))
                .create();
        assertNotNull(repository.getSalesDynamic(salesDynamicsFilter));
    }

    @Test
    void getReceiptStat() {
        assertNotNull(repository.getReceiptStat(generateReceiptsFilter()));
    }

    @Test
    void getReceiptStatisticByStatuses() {
        assertNotNull(repository.getReceiptStatisticByStatuses(generateReceiptsFilter()));
    }

    @Test
    void getSalesStats() {
        assertNotNull(repository.getSalesStats(generateSalesStatsFilter()));
    }

    @Test
    void getUserSalesStats() {
        assertNotNull(repository.getUserSalesStats(generateSalesStatsFilter(), false));
    }

    private SalesStatsFilter generateSalesStatsFilter() {
        return Instancio.of(SalesStatsFilter.class)
                .generate(field(SalesStatsFilter::getFrom), gen -> gen.temporal().localDateTime().past())
                .generate(field(SalesStatsFilter::getTo), gen -> gen.temporal().localDateTime().future())
                .generate(field(SalesStatsFilter::getOrderBy), gen -> gen.oneOf("id"))
                .create();
    }

    private ReceiptFilter generateReceiptsFilter() {
        return Instancio.of(ReceiptFilter.class)
                .generate(field(ReceiptFilter::getFrom), gen -> gen.temporal().localDateTime().past())
                .generate(field(ReceiptFilter::getTo), gen -> gen.temporal().localDateTime().future())
                .generate(field(ReceiptFilter::getOrderBy), gen -> gen.oneOf("id"))
                .create();
    }
}