package uz.uzkassa.cts.interfaces;

import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.dto.payload.ChangeLogPayload;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqChangeLogProducer;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.utils.BeanUtils;

import java.time.LocalDateTime;
import java.util.Objects;

public interface Auditable {

    Long getEntityId();

    default String getEntityName() {
        return null;
    }

    EntityTypeEnum getEntityType();

    private static RabbitMqChangeLogProducer getRabbitMqChangeLogProducer() {
        return BeanUtils.getBean(RabbitMqChangeLogProducer.class);
    }

    default void addChangeLog(String field, String oldValue, String newValue) {
        if (getEntityId() != null && !Objects.equals(oldValue, newValue)) {
            ChangeLogPayload payload = new ChangeLogPayload();
            payload.setEntityId(getEntityId());
            payload.setEntityName(getEntityName());
            payload.setEntityType(getEntityType());
            payload.setField(field);
            payload.setOldValue(oldValue);
            payload.setNewValue(newValue);
            payload.setUpdaterId(SecurityUtils.getCurrentUserId());
            payload.setUpdatedDate(LocalDateTime.now());
            getRabbitMqChangeLogProducer().send(payload);
        }
    }

    default void addAddressChangeLog(BaseAddress oldAddress, BaseAddress newAddress) {
        if (!Objects.equals(oldAddress, newAddress)) {
            if (!Objects.equals(oldAddress != null && oldAddress.getHouse() != null ? oldAddress.getHouse() : null, newAddress != null && newAddress.getHouse() != null ? newAddress.getHouse() : null)) {
                addChangeLog("house", oldAddress != null && oldAddress.getHouse() != null ? oldAddress.getHouse() : null, newAddress != null && newAddress.getHouse() != null ? newAddress.getHouse() : null);
            }
            if (!Objects.equals(oldAddress != null && oldAddress.getApartment() != null ? oldAddress.getApartment() : null, newAddress != null && newAddress.getApartment() != null ? newAddress.getApartment() : null)) {
                addChangeLog("apartment", oldAddress != null && oldAddress.getApartment() != null ? oldAddress.getApartment() : null, newAddress != null && newAddress.getApartment() != null ? newAddress.getApartment() : null);
            }
            if (!Objects.equals(oldAddress != null && oldAddress.getStreet() != null ? oldAddress.getStreet() : null, newAddress != null && newAddress.getStreet() != null ? newAddress.getStreet() : null)) {
                addChangeLog("street", oldAddress != null && oldAddress.getStreet() != null ? oldAddress.getStreet() : null, newAddress != null && newAddress.getStreet() != null ? newAddress.getStreet() : null);
            }
            if (!Objects.equals(oldAddress != null && oldAddress.getDistrict() != null ? oldAddress.getDistrict() : null, newAddress != null && newAddress.getDistrict() != null ? newAddress.getDistrict() : null)) {
                addChangeLog("district", oldAddress != null && oldAddress.getDistrict() != null ? oldAddress.getDistrict().getName() : null, newAddress != null && newAddress.getDistrict() != null ? newAddress.getDistrict().getName() : null);
            }
            if (!Objects.equals(oldAddress != null && oldAddress.getRegion() != null ? oldAddress.getRegion() : null, newAddress != null && newAddress.getRegion() != null ? newAddress.getRegion() : null)) {
                addChangeLog("region", oldAddress != null && oldAddress.getRegion() != null ? oldAddress.getRegion().getName() : null, newAddress != null && newAddress.getRegion() != null ? newAddress.getRegion().getName() : null);
            }
        }
    }
}
