package uz.uzkassa.horeca.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import uz.uzkassa.horeca.constants.MqConstants;
import uz.uzkassa.horeca.dto.factura.FacturaNewDTO;
import uz.uzkassa.horeca.dto.payload.*;
import uz.uzkassa.horeca.dto.terminal.TerminalReceiptDTO;

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
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(producerJackson2MessageConverter());
        factory.setConcurrentConsumers(4);
        factory.setMaxConcurrentConsumers(8);
        return factory;
    }

    @Bean
    @Lazy
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryMin(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(producerJackson2MessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        return factory;
    }

    @Bean
    @Lazy
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryMax(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(producerJackson2MessageConverter());
        factory.setConcurrentConsumers(10);
        factory.setMaxConcurrentConsumers(20);
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

        idClassMapping.put(USER_PERMISSION_CREATE_PAYLOAD, UserRolePermissionPayload.class);
        idClassMapping.put(SYNC_TASNIF_BASKET_PRODUCTS_PAYLOAD, TasnifBasketProductPayload.class);
        idClassMapping.put(WAREHOUSE_ITEM_PAYLOAD, WarehouseItemPayload.class);
        idClassMapping.put(SYNC_FACTURA_BY_ID_PAYLOAD, SyncFacturaByIdPayload.class);
        idClassMapping.put(FACTURA_ON_UPDATE_PAYLOAD, FacturaNewDTO.class);
        idClassMapping.put(TERMINAL_RECEIPT_SAVE_PAYLOAD, TerminalReceiptDTO.class);
        idClassMapping.put(LOG_HISTORY_CLICKHOUSE_PAYLOAD, ClickhouseLogHistoryPayload.class);
        idClassMapping.put(ORDER_EVENT_PAYLOAD, OrderEventPayload.class);

        classMapper.setIdClassMapping(idClassMapping);
        classMapper.setTrustedPackages("*");
        return classMapper;
    }
}
