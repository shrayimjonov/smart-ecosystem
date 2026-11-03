package uz.uzkassa.smartpossupply.interfaces;


import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.dto.payload.ClickhouseLogHistoryPayload;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqClickhouseLogHistoryProducer;
import uz.uzkassa.smartpossupply.utils.BeanUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.Objects;

public interface Auditable {

    Long getEntityId();

    default String getEntityName() {
        return null;
    }

    EntityTypeEnum getEntityType();

    private static RabbitMqClickhouseLogHistoryProducer getRabbitMqChangeLogProducer() {
        return BeanUtils.getBean(RabbitMqClickhouseLogHistoryProducer.class);
    }

    default void addLogHistory(String field, Object oldValue, Object newValue) {
        if (getEntityId() != null && !Objects.equals(oldValue, newValue)) {
            ClickhouseLogHistoryPayload payload = new ClickhouseLogHistoryPayload();
            payload.setEntityId(getEntityId());
            payload.setEntityType(getEntityType().getCode());
            payload.setEntityName(getEntityName());
            payload.setField(field);
            payload.setOldValue(oldValue);
            payload.setNewValue(newValue);
            payload.setUpdaterId(SecurityUtils.getCurrentUserId());
            payload.setUpdaterLogin(SecurityUtils.getCurrentUserLogin().orElse(null));
            payload.setCompanyId(SecurityUtils.getCurrentCompanyId());
            payload.setBranchId(SecurityUtils.getCurrentBranchId());
            getRabbitMqChangeLogProducer().sendToQueue(payload);
        }
    }

    default void addAddressLogHistory(Address oldAddress, Address newAddress) {
        if (!Objects.equals(oldAddress, newAddress)) {
            if (!Objects.equals(oldAddress != null && oldAddress.getHouse() != null ? oldAddress.getHouse() : null, newAddress != null && newAddress.getHouse() != null ? newAddress.getHouse() : null)) {
                addLogHistory("house", oldAddress != null && oldAddress.getHouse() != null ? oldAddress.getHouse() : null, newAddress != null && newAddress.getHouse() != null ? newAddress.getHouse() : null);
            }
            if (!Objects.equals(oldAddress != null && oldAddress.getApartment() != null ? oldAddress.getApartment() : null, newAddress != null && newAddress.getApartment() != null ? newAddress.getApartment() : null)) {
                addLogHistory("apartment", oldAddress != null && oldAddress.getApartment() != null ? oldAddress.getApartment() : null, newAddress != null && newAddress.getApartment() != null ? newAddress.getApartment() : null);
            }
            if (!Objects.equals(oldAddress != null && oldAddress.getStreet() != null ? oldAddress.getStreet() : null, newAddress != null && newAddress.getStreet() != null ? newAddress.getStreet() : null)) {
                addLogHistory("street", oldAddress != null && oldAddress.getStreet() != null ? oldAddress.getStreet() : null, newAddress != null && newAddress.getStreet() != null ? newAddress.getStreet() : null);
            }
            if (!Objects.equals(oldAddress != null && oldAddress.getDistrict() != null ? oldAddress.getDistrict() : null, newAddress != null && newAddress.getDistrict() != null ? newAddress.getDistrict() : null)) {
                addLogHistory("district", oldAddress != null && oldAddress.getDistrict() != null ? oldAddress.getDistrict().getName() : null, newAddress != null && newAddress.getDistrict() != null ? newAddress.getDistrict().getName() : null);
            }
            if (!Objects.equals(oldAddress != null && oldAddress.getRegion() != null ? oldAddress.getRegion() : null, newAddress != null && newAddress.getRegion() != null ? newAddress.getRegion() : null)) {
                addLogHistory("region", oldAddress != null && oldAddress.getRegion() != null ? oldAddress.getRegion().getName() : null, newAddress != null && newAddress.getRegion() != null ? newAddress.getRegion().getName() : null);
            }
        }
    }
}
