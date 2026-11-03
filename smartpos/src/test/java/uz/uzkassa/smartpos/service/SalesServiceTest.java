package uz.uzkassa.smartpos.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.domain.postgres.sales.Sales;
import uz.uzkassa.smartpos.dto.sales.SalesRabbitMQPayload;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.enums.sales.SalesStatus;
import uz.uzkassa.smartpos.enums.sales.SalesType;
import uz.uzkassa.smartpos.mappers.SalesMapper;
import uz.uzkassa.smartpos.mappers.SalesMapperImpl;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.receipt.ReceiptRepository;
import uz.uzkassa.smartpos.repository.postgres.sales.SalesRepository;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesServiceTest {
    @Mock
    LocalizationService localizationService;
    @Mock
    ReceiptRepository receiptRepository;
    @Mock
    SalesRepository salesRepository;
    @Spy
    SalesMapper salesMapper = new SalesMapperImpl();
    @Mock
    RabbitMqProducer rabbitMqProducer;
    @InjectMocks
    SalesService salesService;

    @ParameterizedTest
    @ValueSource(strings = {"PAID", "RETURNED", "INCOME", "WITHDRAW", "FLOW", "RETURN_FLOW", "INCASSATION", "ADVANCE", "CREDIT"})
    void createSalesFromReceipt_When_Sales_DoesntExist(ReceiptStatus status) throws NoSuchFieldException, IllegalAccessException {
        Field localizationServiceField = BaseService.class.getDeclaredField("localizationService");
        localizationServiceField.setAccessible(true);
        localizationServiceField.set(salesService, localizationService);

        Receipt receipt = Instancio.create(Receipt.class);
        receipt.setStatus(status);

        when(receiptRepository.findOneByUid(anyString())).thenReturn(Optional.of(receipt));

        //manually init synchronizations
        TransactionSynchronizationManager.initSynchronization();

        salesService.createSalesFromReceipt(receipt.getUid());

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        TransactionSynchronizationManager.clear();

        verify(salesMapper, times(1)).toSalesFromReceipt(any());
        verify(salesRepository, times(1)).save(any());
        if (List.of(SalesType.SALE, SalesType.RETURNED).contains(receipt.getStatus().getSalesType())) {
            verify(rabbitMqProducer).send(argThat(argument -> receipt.getUid().equals(argument)), eq(MqConstants.SALES_WAREHOUSE_QUEUE));
        }
        verify(rabbitMqProducer).send(argThat(argument -> new SalesRabbitMQPayload(receipt.getUid()).equals(argument)), eq(MqConstants.SALES_CLICKHOUSE_QUEUE));
    }

    @Test
    void createSalesFromReceipt_When_Sales_Exist() throws NoSuchFieldException, IllegalAccessException {
        Field localizationServiceField = BaseService.class.getDeclaredField("localizationService");
        localizationServiceField.setAccessible(true);
        localizationServiceField.set(salesService, localizationService);

        Receipt receipt = Instancio.create(Receipt.class);
        List<ReceiptStatus> statuses = EnumSet.complementOf(EnumSet.of(ReceiptStatus.DRAFT)).stream().toList();
        receipt.setStatus(statuses.get(new Random().nextInt(statuses.size())));
        when(receiptRepository.findOneByUid(anyString())).thenReturn(Optional.of(receipt));
        Sales sales = Instancio.create(Sales.class);
        sales.setStatus(SalesStatus.DRAFT);
        when(salesRepository.findFirstByUid(anyString())).thenReturn(Optional.of(sales));
        //manually init synchronizations
        TransactionSynchronizationManager.initSynchronization();

        salesService.createSalesFromReceipt(receipt.getUid());

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        TransactionSynchronizationManager.clear();

        verify(salesMapper, times(1)).updateCompletedSaleFromReceipt(any(), any());
        verify(salesRepository, times(1)).save(any());
        if (List.of(SalesType.SALE, SalesType.RETURNED).contains(sales.getType())) {
            verify(rabbitMqProducer).send(argThat(argument -> sales.getUid().equals(argument)), eq(MqConstants.SALES_WAREHOUSE_QUEUE));
        }
        verify(rabbitMqProducer).send(argThat(argument -> new SalesRabbitMQPayload(sales.getUid()).equals(argument)), eq(MqConstants.SALES_CLICKHOUSE_QUEUE));
    }
}