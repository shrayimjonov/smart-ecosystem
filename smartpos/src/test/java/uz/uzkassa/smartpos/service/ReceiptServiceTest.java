package uz.uzkassa.smartpos.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.receipt.Additional;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.dto.mq.Payload;
import uz.uzkassa.smartpos.dto.mq.PaymentPayload;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptSalesStatsRepository;
import uz.uzkassa.smartpos.repository.postgres.company.BranchRepository;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.utils.TestUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {
    @Mock
    BranchRepository branchRepository;
    @Mock
    ClickhouseReceiptSalesStatsRepository clickhouseReceiptSalesStatsRepository;
    @Mock
    RabbitMqProducer rabbitMqProducer;
    @Mock
    PushkassaNotificationService notificationService;
    @Spy
    @InjectMocks
    ReceiptService receiptService;

    @Test
    void getReceiptStat_When_ForAdmin() {
        ReceiptFilter filter = new ReceiptFilter();
        filter.setBranchIds(Set.of(1L));
        receiptService.getReceiptStat(filter, true);
        verify(clickhouseReceiptSalesStatsRepository, times(1)).getReceiptStat(Mockito.argThat(argument -> argument.getCompanyId() == null &&
                                                                                                           argument.getBranchIdsWithBranchId().size() == 1 &&
                                                                                                           argument.getBranchIdsWithBranchId().contains(1L)));
    }

    @Test
    void getReceiptStat_When_ForOwner() {
        ReceiptFilter filter = new ReceiptFilter();
        filter.setBranchIds(Set.of(1L));
        when(receiptService.isOwner()).thenReturn(true);
        when(receiptService.isBranchAdmin()).thenReturn(false);
        when(receiptService.getCurrentCompanyId()).thenReturn(1L);
        assertTrue(receiptService.isOwner());
        assertFalse(receiptService.isBranchAdmin());
        receiptService.getReceiptStat(filter, false);
        verify(clickhouseReceiptSalesStatsRepository, times(1)).getReceiptStat(Mockito.argThat(argument -> argument.getCompanyId() == 1L &&
                                                                                                           argument.getBranchIdsWithBranchId().size() == 1 &&
                                                                                                           argument.getBranchIdsWithBranchId().contains(1L)));

    }

    @Test
    void getReceiptStat_When_ForAdminBranch() throws NoSuchFieldException, IllegalAccessException {
        Field branchRepositoryField = SmartposService.class.getDeclaredField("branchRepository");
        branchRepositoryField.setAccessible(true);
        branchRepositoryField.set(receiptService, branchRepository);

        ReceiptFilter filter = new ReceiptFilter();
        when(branchRepository.findIdsByGroups(Set.of(1L))).thenReturn(Set.of(2L));
        try (var securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentCompanyId).thenReturn(1L);
            securityUtils.when(SecurityUtils::getCurrentBranchGroupId).thenReturn(Optional.of(1L));
            securityUtils.when(() -> SecurityUtils.isCurrentUserInRoles(AuthoritiesConstants.OWNER)).thenReturn(false);
            securityUtils.when(() -> SecurityUtils.isCurrentUserInRoles(AuthoritiesConstants.BRANCH_ADMIN)).thenReturn(true);
            assertFalse(receiptService.isOwner());
            assertTrue(receiptService.isBranchAdmin());
            receiptService.getReceiptStat(filter, false);
            verify(clickhouseReceiptSalesStatsRepository, times(1)).getReceiptStat(Mockito.argThat(argument -> argument.getCompanyId() == 1L &&
                                                                                                               argument.getBranchIdsWithBranchId().size() == 1 &&
                                                                                                               argument.getBranchIdsWithBranchId().contains(2L)));
        }
    }

    @Test
    public void receiptAfterCommit() {
        receiptService.receiptAfterCommit(Instancio.create(Receipt.class), Instancio.create(ReceiptDTO.class), Instancio.create(Company.class));
        verify(receiptService, times(1)).sendToReceiptKafkaQueue(Mockito.any(Receipt.class));
        verify(receiptService, times(1)).sendToSalesFromReceiptQueue(Mockito.any(Receipt.class));
        verify(receiptService, times(1)).sendSms(Mockito.any(Receipt.class), Mockito.any(Company.class), Mockito.anyString(), Mockito.anyString());
        verify(receiptService, times(1)).sendHomeService(Mockito.any(Receipt.class));
        verify(receiptService, times(1)).sendToPushkassa(Mockito.any(ReceiptDTO.class));
        verify(receiptService, times(1)).sendNonFiscalToTelegram(Mockito.any(Receipt.class));
        verify(receiptService, times(1)).sendWrongPaymentsToTelegram(Mockito.any(Receipt.class));
    }

    @Test
    public void sendToReceiptKafkaQueue_When_Status_Draft() {
        Receipt receipt = new Receipt();
        receipt.setStatus(uz.uzkassa.smartpos.enums.ReceiptStatus.DRAFT);
        receiptService.sendToReceiptKafkaQueue(receipt);
        verify(rabbitMqProducer, times(0)).send(Mockito.any(), Mockito.anyString());
    }

    @Test
    public void sendToReceiptKafkaQueue_When_Status_Not_Draft() {
        Receipt receipt = new Receipt();
        receipt.setStatus(TestUtils.option(EnumSet.complementOf(EnumSet.of(ReceiptStatus.DRAFT))));
        receipt.setId(RandomUtils.nextLong());
        receiptService.sendToReceiptKafkaQueue(receipt);
        verify(rabbitMqProducer, times(1)).send(argThat(argument -> {
            Payload payload = (Payload) argument;
            assertEquals(payload.getId(), receipt.getId());
            return true;
        }), eq(MqConstants.RECEIPT_KAFKA_QUEUE));
    }


    @Test
    public void sendToSalesFromReceiptQueue_When_Status_Draft() {
        Receipt receipt = new Receipt();
        receipt.setStatus(uz.uzkassa.smartpos.enums.ReceiptStatus.DRAFT);
        receiptService.sendToSalesFromReceiptQueue(receipt);
        verify(rabbitMqProducer, times(0)).send(Mockito.any(), Mockito.anyString());
    }

    @Test
    public void sendToSalesFromReceiptQueue_When_Status_Not_Draft() {
        Receipt receipt = new Receipt();
        receipt.setStatus(TestUtils.option(EnumSet.complementOf(EnumSet.of(ReceiptStatus.DRAFT))));
        receipt.setUid(RandomStringUtils.randomAlphanumeric(16));
        receiptService.sendToSalesFromReceiptQueue(receipt);
        verify(rabbitMqProducer, times(1)).send(argThat(argument -> {
            String uid = (String) argument;
            assertEquals(uid, receipt.getUid());
            return true;
        }), eq(MqConstants.SALES_FROM_RECEIPT_QUEUE));
    }

    @Test
    void sendHomeService_When_Status_PAID_And_HomeServiceAccountNotExist() {
        Receipt receipt = new Receipt();
        receipt.setUid(RandomStringUtils.randomAlphanumeric(16));
        receipt.setCompanyId(RandomUtils.nextLong());
        receipt.setTotalPaid(Instancio.create(BigDecimal.class));
        receipt.setStatus(TestUtils.option(EnumSet.complementOf(EnumSet.of(ReceiptStatus.PAID))));
        Additional additional = new Additional();
        receipt.setAdditional(additional);
        receiptService.sendHomeService(receipt);
        verify(rabbitMqProducer, times(0)).send(any(), anyString());

    }


    @Test
    void sendHomeService_When_Status_Not_PAID_And_HomeServiceAccountExist() {
        Receipt receipt = new Receipt();
        receipt.setUid(RandomStringUtils.randomAlphanumeric(16));
        receipt.setCompanyId(RandomUtils.nextLong());
        receipt.setTotalPaid(Instancio.create(BigDecimal.class));
        receipt.setStatus(TestUtils.option(EnumSet.complementOf(EnumSet.of(ReceiptStatus.PAID))));
        Additional additional = new Additional();
        additional.setHomeServiceAccount(RandomStringUtils.randomNumeric(10));
        receipt.setAdditional(additional);
        receiptService.sendHomeService(receipt);
        verify(rabbitMqProducer, times(0)).send(any(), anyString());

    }

    @Test
    void sendHomeService_When_Status_PAID_And_HomeServiceAccountExist() {
        Receipt receipt = new Receipt();
        receipt.setUid(RandomStringUtils.randomAlphanumeric(16));
        receipt.setCompanyId(RandomUtils.nextLong());
        receipt.setTotalPaid(Instancio.create(BigDecimal.class));
        receipt.setStatus(ReceiptStatus.PAID);
        Additional additional = new Additional();
        additional.setHomeServiceAccount(RandomStringUtils.randomNumeric(10));
        receipt.setAdditional(additional);
        receiptService.sendHomeService(receipt);
        verify(rabbitMqProducer, times(1)).send(argThat(argument -> {
            PaymentPayload payload = (PaymentPayload) argument;
            assertEquals(payload.getAccountCode(), additional.getHomeServiceAccount());
            assertEquals(payload.getReceiptUid(), receipt.getUid());
            assertEquals(payload.getCompanyId(), receipt.getCompanyId());
            assertEquals(payload.getAmount(), receipt.getTotalPaid());
            return true;
        }), eq(MqConstants.CREATE_PAYMENT));
    }

    @Test
    void sendToPushkassa_When_ForceToPrintFalse() {
        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setForceToPrint(false);
        receiptService.sendToPushkassa(receiptDTO);
        verify(notificationService, times(0)).sendNotification(Mockito.any(ReceiptDTO.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void sendToPushkassa_When_ForceToPrintTrueAndStatusCorrect() {
        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setForceToPrint(true);
        receiptDTO.setStatus(TestUtils.option(EnumSet.of(ReceiptStatus.DRAFT, ReceiptStatus.ADVANCE, ReceiptStatus.CREDIT)));
        receiptDTO.setTerminalSN(RandomStringUtils.randomAlphanumeric(15));
        receiptService.sendToPushkassa(receiptDTO);
        verify(notificationService, times(1)).sendNotification(argThat(receiptDTO::equals), eq("smartpos"), eq("receipt"), eq(receiptDTO.getTerminalSN()));
    }

    @Test
    void sendNonFiscalToTelegram_When_StatusPaid_Or_ReturnedAndNotFiscal() {
        Receipt receipt = new Receipt();
        receipt.setId(RandomUtils.nextLong());
        receipt.setStatus(TestUtils.option(EnumSet.of(ReceiptStatus.PAID, ReceiptStatus.RETURNED)));
        receiptService.sendNonFiscalToTelegram(receipt);
        verify(rabbitMqProducer, times(1)).send(eq(receipt.getId()), eq(MqConstants.NON_FISCAL_RECEIPT_QUEUE));
    }
}