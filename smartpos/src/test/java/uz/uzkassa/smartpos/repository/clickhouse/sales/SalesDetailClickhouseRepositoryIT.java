package uz.uzkassa.smartpos.repository.clickhouse.sales;

import ch.qos.logback.core.testUtil.RandomUtil;
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
import uz.uzkassa.smartpos.dto.clickhouse.SalesDetailClickhouseInfoDto;
import uz.uzkassa.smartpos.dto.sales.SalesDetailDynamicsDto;
import uz.uzkassa.smartpos.dto.sales.SalesTopProductDto;
import uz.uzkassa.smartpos.filter.SalesFilter;
import uz.uzkassa.smartpos.repository.clickhouse.sales.impl.SalesDetailClickhouseRepositoryImpl;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@SpringBootTest(classes = {SalesDetailClickhouseRepositoryImpl.class, ClickhouseConfig.class, JacksonConfiguration.class})
class SalesDetailClickhouseRepositoryIT extends BaseClickhouseIT {

    @Autowired
    SalesDetailClickhouseRepository salesDetailClickhouseRepository;

    @Test
    void findAllBySalesId() {
        List<Long> ids = List.of((long) RandomUtil.getPositiveInt());
        List<SalesDetailClickhouseInfoDto> dtos = salesDetailClickhouseRepository.findAllBySalesId(ids);
        assertNotNull(dtos);
    }

    @Test
    void topProducts() {
        var salesFilter = Instancio.of(SalesFilter.class)
                .generate(field(SalesFilter::getFrom), generators -> generators.temporal().localDateTime().past())
                .generate(field(SalesFilter::getTo), generators -> generators.temporal().localDateTime().future())
                .generate(field(SalesFilter::getOrderBy), generators -> generators.oneOf(List.of("id", "createdDateTime")))
                .create();
        List<SalesTopProductDto> topProducts = salesDetailClickhouseRepository.topProducts(salesFilter);
        assertNotNull(topProducts);
    }

    @Test
    void salesDetailDynamics() {
        var salesFilter = Instancio.of(SalesFilter.class)
                .generate(field(SalesFilter::getFrom), generators -> generators.temporal().localDateTime().past())
                .generate(field(SalesFilter::getTo), generators -> generators.temporal().localDateTime().future())
                .generate(field(SalesFilter::getOrderBy), generators -> generators.oneOf(List.of("id", "createdDateTime")))
                .create();
        SalesDetailDynamicsDto salesDetailDynamics = salesDetailClickhouseRepository.salesDetailDynamics(salesFilter);
        assertNotNull(salesDetailDynamics);
    }
}