package uz.uzkassa.smartpos.repository.clickhouse.receipt;

import org.instancio.Instancio;
import org.instancio.generators.Generators;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import uz.uzkassa.smartpos.base.BaseClickhouseIT;
import uz.uzkassa.smartpos.config.JacksonConfiguration;
import uz.uzkassa.smartpos.config.clickhouse.ClickhouseConfig;
import uz.uzkassa.smartpos.dto.filter.receipt.CreditAdvanceReceiptFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.dto.receipt.LastReceiptDto;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.mappers.clickhouse.ClickhouseReceiptMapper;
import uz.uzkassa.smartpos.repository.clickhouse.impl.receipt.ClickhouseReceiptGenericRepositoryImpl;

import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@SpringBootTest(classes = {ClickhouseReceiptGenericRepositoryImpl.class, ClickhouseConfig.class, JacksonConfiguration.class, ClickhouseReceiptMapper.class})
class ClickhouseReceiptGenericRepositoryIT extends BaseClickhouseIT {
    @Autowired
    ClickhouseReceiptGenericRepositoryImpl repository;
    @Autowired
    @Qualifier("clickhouseJdbcClient")
    JdbcClient jdbcClient;
    @Value("${spring.clickhouse.receipt}")
    private String RECEIPT;

    @Test
    void findAllByFilter() {
        var receiptFilter = Instancio.of(ReceiptFilter.class)
                .generate(field(ReceiptFilter::getFrom), gen -> gen.temporal().localDateTime().past())
                .generate(field(ReceiptFilter::getTo), gen -> gen.temporal().localDateTime().future())
                .generate(field(ReceiptFilter::getOrderBy), gen -> gen.oneOf("id"))
                .create();
        assertNotNull(repository.findAllByFilter(receiptFilter, false));
    }

    @Test
    void findFirstByOriginUid() {
        Optional<ReceiptDTO> info = repository.findFirstByOriginUid("originUid");
        assertTrue(info.isEmpty());
    }

    @Test
    void findAllByOriginUid() {
        assertNull(repository.findAllByOriginUid("originUid"));
    }

    @Test
    void findFirstByFiscalUrl() {
        Optional<ReceiptDTO> info = repository.findFirstByFiscalUrl("fiscalUrl");
        assertTrue(info.isEmpty());
    }

    @Test
    void findById() {
        Long id = Instancio.gen().longs().get();
        assertNotNull(repository.findById(id, false));
    }

    @Test
    void xReport() {
        Long branchId = Instancio.gen().longs().get();
        Long shiftNumber = Instancio.gen().longs().get();
        assertNotNull(repository.xReport(branchId, shiftNumber));
    }

    @Test
    void totalReport() {
        assertNotNull(repository.totalReport(Instancio.gen().longs().get()));
    }

    @Test
    void receiptNumberInShift() {
        Long branchId = Instancio.gen().longs().get();
        Long shiftNumber = Instancio.gen().longs().get();
        assertNotNull(repository.receiptNumberInShift(branchId, shiftNumber));
    }

    @Test
    void maxShiftNumber() {
        assertNotNull(repository.maxShiftNumber(Instancio.gen().longs().get()));
    }

    @Test
    void findAllCreditAdvanceReceipts() {
        var creditAdvanceReceiptFilter = Instancio.of(CreditAdvanceReceiptFilter.class)
                .generate(field(CreditAdvanceReceiptFilter::getCompanyId), Generators::longs)
                .generate(field(CreditAdvanceReceiptFilter::getBranchId), Generators::longs)
                .generate(field(CreditAdvanceReceiptFilter::getSearch), Generators::string)
                .generate(field(CreditAdvanceReceiptFilter::getPageNumber), Generators::ints)
                .generate(field(CreditAdvanceReceiptFilter::getPageSize), Generators::ints)
                .generate(field(CreditAdvanceReceiptFilter::getOrderBy), gen -> gen.oneOf("receiptDateTime"))
                .create();
        assertNotNull(repository.findAllCreditAdvanceReceipts(creditAdvanceReceiptFilter));

    }

    @Test
    void findAllCreditPayments() {
        assertNotNull(repository.findAllCreditPayments(Instancio.gen().toString()));
    }

    @Test
    void testFindAllCreditPayments() {
        assertNotNull(repository.findAllCreditPayments(Instancio.gen().longs().get()));
    }

    @Test
    void getAdvanceOrderFromOriginUid() {
        assertNotNull(repository.getAdvanceOrderFromOriginUid(Instancio.gen().toString()));
    }

    @Test
    void findUidByFiscalUrl() {
        assertNotNull(repository.findUidByFiscalUrl(Instancio.gen().toString()));
    }

    @Test
    void findAdditionalByOriginUid() {
        assertNotNull(repository.findAdditionalByOriginUid(Instancio.gen().toString())); //sql query space after from
    }

    @Test
    void checkReceiptByOriginUid() {
        String uid = Instancio.gen().toString();
        List<ReceiptStatus> statuses = Instancio.createList(ReceiptStatus.class);
        assertNotNull(repository.checkReceiptByOriginUid(uid, statuses));
    }

    @Test
    void getLastReceipt() {
        String serialNumber = RandomStringUtils.randomAlphabetic(15);
        String tin = RandomStringUtils.randomAlphabetic(14);
        jdbcClient.sql("INSERT INTO " + RECEIPT + " (terminalSerialNumber,companyInn, payments) values ('" + serialNumber + "' ,'" + tin + "', '{''HUMO'': 15}')").update();
        Optional<LastReceiptDto> lastReceipt = repository.getLastReceipt(serialNumber, tin, PaymentType.HUMO);
        assertNotNull(lastReceipt);
    }
}