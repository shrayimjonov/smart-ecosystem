package uz.uzkassa.smartpos.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import uz.uzkassa.smartpos.config.DatabaseConfiguration;
import uz.uzkassa.smartpos.config.JacksonConfiguration;
import uz.uzkassa.smartpos.config.TestAuditorAwareConfig;
import uz.uzkassa.smartpos.containers.CustomPostgreSQLContainer;
import uz.uzkassa.smartpos.domain.postgres.company.BillingPermission;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyPermission;
import uz.uzkassa.smartpos.dto.BillingPermissionRequestDto;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.dto.mq.CompanyUnblockPayload;
import uz.uzkassa.smartpos.integration.billing.enums.XizmatType;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.company.BillingPermissionRepository;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyPermissionRepository;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@ActiveProfiles("test")
@EntityScan(basePackages = {"uz.uzkassa.smartpos.domain.postgres"})
@Import({TestAuditorAwareConfig.class, BillingPermissionService.class})
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@ContextConfiguration(classes = {DatabaseConfiguration.class, JacksonConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(excludeAutoConfiguration = {LiquibaseAutoConfiguration.class})
class BillingPermissionServiceIT {
    @Autowired
    BillingPermissionService billingPermissionService;
    @Autowired
    CompanyPermissionRepository companyPermissionRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    BillingPermissionRepository billingPermissionRepository;
    @MockBean
    RabbitMqProducer rabbitMqProducer;

    @Test
    void syncWithBilling() {
        CompanyPermission smsPermission = new CompanyPermission();
        smsPermission.setServiceType(ServiceType.SMS);
        smsPermission.setCompanyId(2L);
        smsPermission.setXizmatType(XizmatType.SINGLE);
        smsPermission.setEndDate(LocalDateTime.now().minusDays(1));
        companyPermissionRepository.save(smsPermission);
        CompanyPermission warehousePermission = new CompanyPermission();
        warehousePermission.setServiceType(ServiceType.WAREHOUSE);
        warehousePermission.setCompanyId(2L);
        warehousePermission.setXizmatType(XizmatType.GROUPED);
        warehousePermission.setEndDate(LocalDateTime.now().minusDays(1));
        companyPermissionRepository.save(warehousePermission);
        CompanyPermission receiptFromInvoicePermission = new CompanyPermission();
        receiptFromInvoicePermission.setServiceType(ServiceType.RECEIPT_FROM_INVOICE);
        receiptFromInvoicePermission.setCompanyId(2L);
        receiptFromInvoicePermission.setXizmatType(XizmatType.GROUPED);
        receiptFromInvoicePermission.setEndDate(LocalDateTime.now().minusDays(1));
        companyPermissionRepository.save(receiptFromInvoicePermission);
        var dtos = new ArrayList<BillingPermissionRequestDto>();
        var sms = new BillingPermissionRequestDto();
        sms.setUnitCount(10);
        sms.setAmount(BigDecimal.valueOf(100));
        sms.setStartDate(LocalDateTime.now());
        sms.setEndDate(LocalDateTime.now().plusMonths(1));
        sms.setTin("1234567891011");
        sms.setServiceType(ServiceType.SMS);
        sms.setXizmatType(XizmatType.SINGLE);
        dtos.add(sms);
        var warehouse = new BillingPermissionRequestDto();
        warehouse.setUnitCount(10);
        warehouse.setAmount(BigDecimal.valueOf(100));
        warehouse.setStartDate(LocalDateTime.now());
        warehouse.setEndDate(LocalDateTime.now().plusMonths(1));
        warehouse.setTin("1234567891011");
        warehouse.setServiceType(ServiceType.WAREHOUSE);
        warehouse.setXizmatType(XizmatType.GROUPED);
        dtos.add(warehouse);
        var receiptFromInvoice = new BillingPermissionRequestDto();
        receiptFromInvoice.setUnitCount(10);
        receiptFromInvoice.setAmount(BigDecimal.valueOf(100));
        receiptFromInvoice.setStartDate(LocalDateTime.now());
        receiptFromInvoice.setEndDate(LocalDateTime.now().plusMonths(1));
        receiptFromInvoice.setTin("1234567891011");
        receiptFromInvoice.setServiceType(ServiceType.RECEIPT_FROM_INVOICE);
        receiptFromInvoice.setXizmatType(XizmatType.GROUPED);
        dtos.add(receiptFromInvoice);
        billingPermissionService.syncWithBilling(dtos);
        Mockito.verify(rabbitMqProducer, Mockito.times(1))
                .sendWithDelayTime(argThat((ArgumentMatcher<CompanyUnblockPayload>) argument -> argument.getCompanyIds().size() == 1 && argument.getCompanyIds().contains(2L)), eq(MqConstants.UNBLOCK_TERMINALS_QUEUE), eq(60_000L));
        var companyOptional = companyRepository.findById(2L);
        assertTrue(companyOptional.isPresent());
        var company = companyOptional.get();
        assertEquals(1, company.getBalances().size());
        Integer balance = company.getBalances().get(ServiceType.SMS);
        assertEquals(10, balance);
        List<CompanyPermission> permissions = companyPermissionRepository.findAll().stream().filter(p -> p.getCompanyId().equals(2L)).toList();
        assertEquals(3, permissions.size());
        assertEquals(2, permissions.stream().filter(p -> p.getXizmatType().equals(XizmatType.GROUPED)).count());
        List<BillingPermission> billingPermissions = billingPermissionRepository.findAll().stream().filter(p -> p.getCompanyId() == 2).toList();
        assertEquals(3, billingPermissions.size());
        billingPermissions.forEach(billingPermission -> {
            if (Objects.equals(billingPermission.getServiceType(), ServiceType.SMS)) {
                assertEquals(sms.getEndDate(), billingPermission.getEndDate());
            }
            if (Objects.equals(billingPermission.getServiceType(), ServiceType.WAREHOUSE)) {
                assertEquals(warehouse.getEndDate(), billingPermission.getEndDate());
            }
            if (Objects.equals(billingPermission.getServiceType(), ServiceType.RECEIPT_FROM_INVOICE)) {
                assertEquals(receiptFromInvoice.getEndDate(), billingPermission.getEndDate());
            }
        });
    }


    @BeforeAll
    static void setUp() {
        postgreSQL.start();
    }

    @AfterAll
    static void tearDown() {
        postgreSQL.stop();
    }

    public static final CustomPostgreSQLContainer postgreSQL = CustomPostgreSQLContainer.getInstance();
}