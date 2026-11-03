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
import uz.uzkassa.smartpos.dto.filter.receipt.CustomerReceiptsFilter;
import uz.uzkassa.smartpos.repository.clickhouse.impl.receipt.ClickhouseReceiptReportByCompanyImpl;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@SpringBootTest(classes = {ClickhouseReceiptReportByCompanyImpl.class, ClickhouseConfig.class, JacksonConfiguration.class})
public class ClickhouseReceiptReportByCompanyIT extends BaseClickhouseIT {
    @Autowired
    ClickhouseReceiptReportByCompanyImpl repository;

    @Test
    void getReceiptsAmountByCompany() {
        assertNotNull(repository.getReceiptsAmountByCompany(generateCustomReceiptsFilter()));
    }

    @Test
    void getReceiptsAmountByCompanyStat() {
        assertNotNull(repository.getReceiptsAmountByCompanyStat(generateCustomReceiptsFilter()));
    }


    private CustomerReceiptsFilter generateCustomReceiptsFilter() {
        return Instancio.of(CustomerReceiptsFilter.class)
                .generate(field(CustomerReceiptsFilter::getFrom), gen -> gen.temporal().localDateTime().past())
                .generate(field(CustomerReceiptsFilter::getTo), gen -> gen.temporal().localDateTime().future())
                .generate(field(CustomerReceiptsFilter::getOrderBy), gen -> gen.oneOf("id"))
                .create();
    }
}
