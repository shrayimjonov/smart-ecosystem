package uz.uzkassa.cts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import uz.uzkassa.cts.constants.MqConstants;
import uz.uzkassa.cts.dto.billing.BillingInvoicePayloadDTO;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceExcelDTO;
import uz.uzkassa.cts.dto.device.CustomerDeviceDTO;
import uz.uzkassa.cts.dto.factura.FacturaDTO;
import uz.uzkassa.cts.dto.fm.FiscalizationSyncDTO;
import uz.uzkassa.cts.dto.humoconnect.request.HumoConnectLastReceiptRequestDTO;
import uz.uzkassa.cts.dto.launcher.LauncherRabbitPaginationRequestDto;
import uz.uzkassa.cts.dto.ofd.CustomerBranchSendDTO;
import uz.uzkassa.cts.dto.payload.*;
import uz.uzkassa.cts.dto.sms.EtcRequestDTO;
import uz.uzkassa.cts.dto.uzcard.UzcardConnectionPayloadDTO;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRabbit
@Lazy
public class RabbitMQConfiguration implements MqConstants {

    private final ObjectMapper objectMapper;

    @Autowired
    public RabbitMQConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Primary
    @Bean
    @Lazy
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(producerJackson2MessageConverter());
        factory.setConcurrentConsumers(4);
        factory.setMaxConcurrentConsumers(4);
        factory.setDefaultRequeueRejected(true);
        return factory;
    }

    @Bean
    @Lazy
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactorySimple(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(producerJackson2MessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setDefaultRequeueRejected(true);
        return factory;
    }

    @Bean
    @Lazy
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryMax(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(producerJackson2MessageConverter());
        factory.setConcurrentConsumers(10);
        factory.setMaxConcurrentConsumers(15);
        factory.setDefaultRequeueRejected(true);
        return factory;
    }

    @Bean
    @Lazy
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter(objectMapper);
        jackson2JsonMessageConverter.setClassMapper(classMapper());
        return jackson2JsonMessageConverter;
    }

    @Bean
    @Lazy
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(objectMapper);
        return messageConverter;
    }

    @Bean
    @Lazy
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("map", HashMap.class);

        idClassMapping.put(NOTIFICATION_CREATE_PAYLOAD, NotificationPayload.class);
        idClassMapping.put(USER_PERMISSION_CREATE_PAYLOAD, UserRolePermissionPayload.class);
        idClassMapping.put(SERVICE_PAYMENT_HISTORY_PAYLOAD, String.class);
        idClassMapping.put(DEVICE_SWITCH_DELAY_PAYLOAD, String.class);
        idClassMapping.put(SERVICE_INVOICE_PAYLOAD, String.class);
        idClassMapping.put(NOTIFY_BY_SMS_PAYLOAD, String.class);
        idClassMapping.put(NOTIFY_MONTHLY_FEE_PAYLOAD, String.class);
        idClassMapping.put(NOTIFY_MONTHLY_FEE_BY_SMS_PAYLOAD, String.class);
        idClassMapping.put(INACTIVE_FISCAL_MODULE_PAYLOAD, String.class);

        idClassMapping.put(SMARTPOS_UPDATE_CUSTOMER_PAYLOAD, Long.class);
        idClassMapping.put(SMARTPOS_SYNC_CUSTOMER_BRANCH_PAYLOAD, Long.class);
        idClassMapping.put(SMARTPOS_REMOVE_DEVICE_PAYLOAD, SmartPosRemoveDevicePayload.class);
        idClassMapping.put(HUMO_CONNECT_UPDATE_LAST_RECEIPT_PAYLOAD, HumoConnectLastReceiptRequestDTO.class);
        idClassMapping.put(XFILE_ACTOFTRANSMISSION_INVOICE_PAYLOAD, Long.class);
        idClassMapping.put(XFILE_SERVICEORDER_INVOICE_PAYLOAD, Long.class);
        idClassMapping.put(XFILE_ONETIME_INVOICE_PAYLOAD, Long.class);
        idClassMapping.put(XFILE_TRADEIN_INVOICE_PAYLOAD, Long.class);
        idClassMapping.put(RENT_HISTORY_CREATE_PAYLOAD, Long.class);

        idClassMapping.put(IMPORT_REPORT_PAYLOAD, ImportReportPayload.class);

        idClassMapping.put(OFD_APPLICATION_RESYNC_PAYLOAD, OfdApplicationPayload.class);
        idClassMapping.put(OFD_APPLICATION_BATCH_SYNC_PAYLOAD, Payload.class);
        idClassMapping.put(OFD_APPLICATION_SEND_PAYLOAD, OfdApplicationSendPayload.class);
        idClassMapping.put(OFD_NEIGHBORHOOD_SYNC_PAYLOAD, OfdNeighborhoodSyncPayload.class);
        idClassMapping.put(HISTORY_PAYLOAD, HistoryPayload.class);
        idClassMapping.put(LAUNCHER_PAYLOAD, LauncherRabbitPaginationRequestDto.class);
        idClassMapping.put(NOTIFY_ALL_BY_SMS_PAYLOAD, EtcRequestDTO.class);
        idClassMapping.put(SEND_CUSTOMER_BRANCH_PAYLOAD, CustomerBranchSendDTO.class);

        idClassMapping.put(XFILE_TRADE_IN_INVOICE_PAYLOAD, FacturaDTO.class);
        idClassMapping.put(RENT_INVOICE_PAYLOAD, FacturaDTO.class);
        idClassMapping.put(XFILE_SERVICE_ORDER_INVOICE_PAYLOAD, FacturaDTO.class);
        idClassMapping.put(XFILE_ONE_TIME_ORDER_INVOICE_PAYLOAD, FacturaDTO.class);
        idClassMapping.put(XFILE_ACT_OF_TRANSMISSION_INVOICE_PAYLOAD, FacturaDTO.class);
        idClassMapping.put(CASE_TELEGRAM_BOT_PAYLOAD, CaseBotPayload.class);
        idClassMapping.put(X_FILE_SALE_INVOICE_PAYLOAD, FacturaDTO.class);

        idClassMapping.put(A_PAY_LIST_CUSTOMER_BRANCH_PAYLOAD, FilterPayload.class);
        idClassMapping.put(A_PAY_CREATE_CUSTOMER_BRANCH_PAYLOAD, Payload.class);
        idClassMapping.put(A_PAY_UPDATE_CUSTOMER_BRANCH_PAYLOAD, Payload.class);
        idClassMapping.put(A_PAY_DELETE_CUSTOMER_BRANCH_PAYLOAD, Payload.class);

        idClassMapping.put(SEND_CUSTOMER_BRANCH_DEVICE_PAYLOAD, CustomerBranchDeviceExcelDTO.class);
        idClassMapping.put(SYNC_CUSTOMER_BANK_PAYLOAD, CustomerPayload.class);
        idClassMapping.put(SYNC_CUSTOMER_SMARTPOS_PAYLOAD, FiscalizationSyncDTO.class);
        idClassMapping.put(CHANGE_LOG_PAYLOAD, ChangeLogPayload.class);

        idClassMapping.put(FISCAL_MODULE_REPORT_PAYLOAD, Payload.class);
        idClassMapping.put(RECALCULATION_CUSTOMER_PAYLOAD, Payload.class);
        idClassMapping.put(UPDATE_CUSTOMER_STATUS_BY_PAYMENT_PAYLOAD, String.class);
        idClassMapping.put(EXPORT_REPORT_PAYLOAD, ExportReportPayload.class);

        idClassMapping.put(CONTRACT_REPORT_CREATE_PAYLOAD, ContractReportPayload.class);
        idClassMapping.put(CONTRACT_REPORT_CREATE_THROUGH_REGISTER_KKM_PAYLOAD, CustomerDeviceDTO.class);
        idClassMapping.put(BANK_TARIFF_HISTORY_CREATE_PAYLOAD, Payload.class);
        idClassMapping.put(A_PAY_SYNC_CATEGORY_PAYLOAD, FilterPayload.class);
        idClassMapping.put(UZ_CARD_CONNECTION_PAYLOAD, UzcardConnectionPayloadDTO.class);
        idClassMapping.put(BILLING_INVOICE_CREATE_PAYLOAD, BillingInvoicePayloadDTO.class);
        idClassMapping.put(SEND_SCHEDULED_MESSAGE_PAYLOAD, ScheduleMessagePayload.class);
        idClassMapping.put(SEND_REMINDER_PAYLOAD, ReminderPayload.class);
        idClassMapping.put(MAILING_LIST_ITEM_PAYLOAD, MailingListItemPayload.class);

        idClassMapping.put(LEAVE_ALLOWANCE_PAYLOAD, Payload.class);
        idClassMapping.put(ATTENDANCE_PAYLOAD, AttendancePayload.class);
        idClassMapping.put(ATTENDANCE_ITEM_PAYLOAD, Payload.class);

        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }
}
