package uz.uzkassa.smartpossupply.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.category.EDocsGenerateDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaNewDTO;
import uz.uzkassa.smartpossupply.dto.payload.AggregationCodesPayload;
import uz.uzkassa.smartpossupply.dto.payload.AggregationOrderPayload;
import uz.uzkassa.smartpossupply.dto.payload.CategoryTransferPayload;
import uz.uzkassa.smartpossupply.dto.payload.ClickhouseLogHistoryPayload;
import uz.uzkassa.smartpossupply.dto.payload.ClickhouseSyncPayload;
import uz.uzkassa.smartpossupply.dto.payload.CommonPayload;
import uz.uzkassa.smartpossupply.dto.payload.CompanySettingsSyncPayload;
import uz.uzkassa.smartpossupply.dto.payload.CreateCatalogFromTasnifCatalogPayload;
import uz.uzkassa.smartpossupply.dto.payload.ExportReportPayload;
import uz.uzkassa.smartpossupply.dto.payload.GenerateFacturaGUIDPayload;
import uz.uzkassa.smartpossupply.dto.payload.ImportReportItemPayload;
import uz.uzkassa.smartpossupply.dto.payload.ImportReportPayload;
import uz.uzkassa.smartpossupply.dto.payload.LgotaSyncPayload;
import uz.uzkassa.smartpossupply.dto.payload.NotificationPayload;
import uz.uzkassa.smartpossupply.dto.payload.NotificationReadPayload;
import uz.uzkassa.smartpossupply.dto.payload.OneCIntegrationPayload;
import uz.uzkassa.smartpossupply.dto.payload.PartyUtilisationPayload;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.dto.payload.ProductPackageSyncPayload;
import uz.uzkassa.smartpossupply.dto.payload.ProductPhotoSyncPayload;
import uz.uzkassa.smartpossupply.dto.payload.ReceiptSendPayload;
import uz.uzkassa.smartpossupply.dto.payload.ScheduleMessagePayload;
import uz.uzkassa.smartpossupply.dto.payload.StockOptimizationPayload;
import uz.uzkassa.smartpossupply.dto.payload.SyncCustomerBranchPayload;
import uz.uzkassa.smartpossupply.dto.payload.SyncFacturaByIdPayload;
import uz.uzkassa.smartpossupply.dto.payload.SyncPayload;
import uz.uzkassa.smartpossupply.dto.payload.SyncTasnifCatalogByProductPayload;
import uz.uzkassa.smartpossupply.dto.payload.SyncTasnifCatalogPayload;
import uz.uzkassa.smartpossupply.dto.payload.UpdateCompanyEquipmentCountPayload;
import uz.uzkassa.smartpossupply.dto.payload.UserRolePermissionPayload;
import uz.uzkassa.smartpossupply.dto.receipt.SmartposReceiptDTO;
import uz.uzkassa.smartpossupply.dto.receipt.TerminalReceiptDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckAslBelgiDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillWebhookDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static uz.uzkassa.smartpossupply.constants.LoggingConstants.TRACE_ID_HEADER;
import static uz.uzkassa.smartpossupply.constants.LoggingConstants.TRACE_ID_MDC_KEY;

@Configuration
@EnableRabbit
@Lazy
public class RabbitMQConfiguration implements MqConstants {

    private final ObjectMapper objectMapper;

    public RabbitMQConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Primary
    @Bean
    @Lazy
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        return buildSimpleRabbitListenerContainerFactory(connectionFactory, 4, 8);
    }

    @Bean
    @Lazy
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryMin(ConnectionFactory connectionFactory) {
        return buildSimpleRabbitListenerContainerFactory(connectionFactory, 1, 1);
    }

    @Bean
    @Lazy
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryMax(ConnectionFactory connectionFactory) {
        return buildSimpleRabbitListenerContainerFactory(connectionFactory, 10, 20);
    }

    private SimpleRabbitListenerContainerFactory buildSimpleRabbitListenerContainerFactory(ConnectionFactory connectionFactory, Integer concurrentConsumers, Integer maxConcurrentConsumers) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(producerJackson2MessageConverter());
        factory.setConcurrentConsumers(concurrentConsumers);
        factory.setMaxConcurrentConsumers(maxConcurrentConsumers);
        factory.setAdviceChain(traceIdListenerAdvice());
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

        idClassMapping.put(SYNC_TASNIF_CATALOG_BY_PRODUCT_PAYLOAD, SyncTasnifCatalogByProductPayload.class);
        idClassMapping.put(SYNC_TASNIF_CATALOG_PAYLOAD, SyncTasnifCatalogPayload.class);

        idClassMapping.put(FACTURA_ON_UPDATE_PAYLOAD, FacturaNewDTO.class);
        idClassMapping.put(SYNC_FACTURA_BY_ID_PAYLOAD, SyncFacturaByIdPayload.class);

        idClassMapping.put(WAYBILL_ON_UPDATE_PAYLOAD, WaybillWebhookDTO.class);

        idClassMapping.put(USER_PERMISSION_CREATE_PAYLOAD, UserRolePermissionPayload.class);
        idClassMapping.put(CREATE_CATALOG_FROM_TASNIF_CATALOG_PAYLOAD, CreateCatalogFromTasnifCatalogPayload.class);
        idClassMapping.put(PLAN_ROUTE_CREATE_PAYLOAD, Payload.class);

        idClassMapping.put(COMPANY_REGISTRATION_CODE_SYNC_PAYLOAD, Payload.class);
        idClassMapping.put(GENERATE_FACTURA_GUID_PAYLOAD, GenerateFacturaGUIDPayload.class);

        idClassMapping.put(COMPANY_SYNC_PAYLOAD, SyncPayload.class);
        idClassMapping.put(COMPANY_BANK_SYNC_PAYLOAD, SyncPayload.class);
        idClassMapping.put(BRANCH_SYNC_PAYLOAD, SyncPayload.class);

        idClassMapping.put(SYNC_CLICKHOUSE_PAYLOAD, ClickhouseSyncPayload.class);
        idClassMapping.put(MARKETPLACE_APPLICATION_SEND_PAYLOAD, Payload.class);
        idClassMapping.put(CREATE_MATERIAL_REPORT_PAYLOAD, Payload.class);
        idClassMapping.put(RESYNC_MATERIAL_REPORT_PAYLOAD, CommonPayload.class);
        idClassMapping.put(CHECK_CODE_ASL_BELGI_PAYLOAD, CheckAslBelgiDTO.class);
        idClassMapping.put(CHECK_ASL_BELGI_PAYLOAD, CheckAslBelgiDTO.class);
        idClassMapping.put(AGGREGATION_ORDER_PAYLOAD, AggregationOrderPayload.class);
        idClassMapping.put(OFFLINE_ORDER_CREATE_PAYLOAD, Payload.class);
        idClassMapping.put(OFFLINE_ORDER_UPDATE_PAYLOAD, Payload.class);
        idClassMapping.put(SEND_NOTIFICATION_PAYLOAD, NotificationPayload.class);
        idClassMapping.put(NOTIFICATION_READ_PAYLOAD, NotificationReadPayload.class);
        idClassMapping.put(PARTY_UITILISATION_PAYLOAD, PartyUtilisationPayload.class);
        idClassMapping.put(EXPORT_REPORT_PAYLOAD, ExportReportPayload.class);
        idClassMapping.put(IMPORT_REPORT_PAYLOAD, ImportReportPayload.class);
        idClassMapping.put(IMPORT_REPORT_ITEM_PAYLOAD, ImportReportItemPayload.class);
        idClassMapping.put(SYNC_CUSTOMER_BRANCH_PAYLOAD, SyncCustomerBranchPayload.class);
        idClassMapping.put(LOG_HISTORY_CLICKHOUSE_PAYLOAD, ClickhouseLogHistoryPayload.class);
        idClassMapping.put(LGOTA_SYNC_PAYLOAD, LgotaSyncPayload.class);
        idClassMapping.put(STOCK_IN_ONE_C_INTEGRATION_PAYLOAD, OneCIntegrationPayload.class);
        idClassMapping.put(SYNC_PRODUCT_PACKAGE_PAYLOAD, ProductPackageSyncPayload.class);
        idClassMapping.put(AGGREGATION_CODES_PAYLOAD, AggregationCodesPayload.class);
        idClassMapping.put(DELETE_AGGREGATION_CODES_PAYLOAD, StockOptimizationPayload.class);
        idClassMapping.put(DELETE_AGGREGATION_CODES_ITEM_PAYLOAD, StockOptimizationPayload.class);
        idClassMapping.put(DELETE_STOCK_PRODUCT_PAYLOAD, StockOptimizationPayload.class);
        idClassMapping.put(COMPANY_SETTINGS_SYNC_PAYLOAD, CompanySettingsSyncPayload.class);
        idClassMapping.put(SYNC_CUSTOMER_TRADE_BRANCH_PAYLOAD, SyncCustomerBranchPayload.class);
        idClassMapping.put(CREATE_DEFAULT_CUSTOMER_BRANCH_PAYLOAD, SyncCustomerBranchPayload.class);
        idClassMapping.put(CREATE_DEFAULT_CUSTOMER_BRANCH_ITEM_PAYLOAD, SyncCustomerBranchPayload.class);
        idClassMapping.put(RECEIPT_SEND_TO_SMARTPOS_PAYLOAD, ReceiptSendPayload.class);
        idClassMapping.put(DIVIDED_RECEIPT_SEND_TO_SMARTPOS_PAYLOAD, SmartposReceiptDTO.class);
        idClassMapping.put(TERMINAL_RECEIPT_SAVE_PAYLOAD, TerminalReceiptDTO.class);
        idClassMapping.put(CATEGORY_TRANSFER_PAYLOAD, CategoryTransferPayload.class);
        idClassMapping.put(PRODUCT_PHOTO_SYNC_PAYLOAD, ProductPhotoSyncPayload.class);
        idClassMapping.put(AUTOMATIC_GENERATE_E_DOCS_PAYLOAD, EDocsGenerateDTO.class);
        idClassMapping.put(SEND_SCHEDULED_MESSAGE_PAYLOAD, ScheduleMessagePayload.class);
        idClassMapping.put(UPDATE_COMPANY_EQUIPMENT_COUNT_PAYLOAD, UpdateCompanyEquipmentCountPayload.class);
        idClassMapping.put(CUSTOMER_GROUP_UPDATE_PAYLOAD, Payload.class);
        idClassMapping.put("map", HashMap.class);
        classMapper.setIdClassMapping(idClassMapping);
        classMapper.setTrustedPackages("*");
        return classMapper;
    }

    @Bean
    public RabbitTemplateCustomizer traceIdRabbitTemplateCustomizer() {
        return rabbitTemplate ->
                rabbitTemplate.addBeforePublishPostProcessors(message -> {
                    MessageProperties properties = message.getMessageProperties();
                    String traceId = MDC.get(TRACE_ID_MDC_KEY);

                    if (StringUtils.isBlank(traceId)) {
                        traceId = UUID.randomUUID().toString();
                    }
                    properties.setHeader(TRACE_ID_HEADER, traceId);

                    return message;
                });
    }

    private MethodInterceptor traceIdListenerAdvice() {
        return invocation -> {
            Message message = null;
            for (Object argument : invocation.getArguments()) {
                if (argument instanceof Message candidate) {
                    message = candidate;
                } else if (argument instanceof List<?> list) {
                    message = list.stream()
                            .filter(Message.class::isInstance)
                            .map(Message.class::cast)
                            .findFirst()
                            .orElse(null);
                }
                if (message != null) {
                    break;
                }
            }

            String traceId = message == null ? null
                    : Objects.toString(message.getMessageProperties().getHeader(TRACE_ID_HEADER), null);

            if (StringUtils.isBlank(traceId)) {
                traceId = UUID.randomUUID().toString();
            }
            String previous = MDC.get(TRACE_ID_MDC_KEY);
            MDC.put(TRACE_ID_MDC_KEY, traceId);

            try {
                return invocation.proceed();
            } finally {
                if (previous != null) {
                    MDC.put(TRACE_ID_MDC_KEY, previous);
                } else {
                    MDC.remove(TRACE_ID_MDC_KEY);
                }
            }
        };
    }

}
