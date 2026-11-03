package uz.uzkassa.horeca.domain.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.uzkassa.horeca.domain.address.Address;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.dto.payload.ClickhouseLogHistoryPayload;
import uz.uzkassa.horeca.interfaces.Identifier;
import uz.uzkassa.horeca.rabbitmq.producer.RabbitMqClickhouseLogHistoryProducer;
import uz.uzkassa.horeca.utils.BeanUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditingEntity extends BaseEntity implements Serializable, Identifier {

    @Serial
    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column(name = "creator_id")
    Long creatorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", updatable = false, insertable = false)
    User creator;

    @LastModifiedBy
    @Column(name = "updater_id")
    Long updaterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updater_id", updatable = false, insertable = false)
    User updater;

    @CreatedDate
    @Column(name = "created_date")
    @JsonIgnore
    LocalDateTime createdDate = LocalDateTime.now();

    @LastModifiedDate
    @Column(name = "updated_date")
    @JsonIgnore
    LocalDateTime updatedDate = LocalDateTime.now();

    protected static RabbitMqClickhouseLogHistoryProducer getRabbitMqLogHistoryProducer() {
        return BeanUtils.getBean(RabbitMqClickhouseLogHistoryProducer.class);
    }

    public void addLogHistory(String field, Object oldValue, Object newValue) {
        if (getId() != null && !Objects.equals(oldValue, newValue)) {
            ClickhouseLogHistoryPayload payload = new ClickhouseLogHistoryPayload();
            payload.setEntityId(getEntityId());
            payload.setEntityType(getEntityType().getCode());
            payload.setField(field);
            payload.setOldValue(oldValue);
            payload.setNewValue(newValue);
            payload.setUpdaterId(HeaderUtils.getCurrentUserId());
            payload.setUpdaterLogin(HeaderUtils.getCurrentUserLogin().orElse(null));
            payload.setCompanyId(HeaderUtils.getCurrentCompanyId());
            getRabbitMqLogHistoryProducer().sendToQueue(payload);
        }
    }

    protected void addAddressLogHistory(Address oldAddress, Address newAddress) {
        if (!Objects.equals(oldAddress, newAddress)) {
            if (
                !Objects.equals(
                    oldAddress != null && oldAddress.getHouse() != null ? oldAddress.getHouse() : null,
                    newAddress != null && newAddress.getHouse() != null ? newAddress.getHouse() : null
                )
            ) {
                addLogHistory(
                    "house",
                    oldAddress != null && oldAddress.getHouse() != null ? oldAddress.getHouse() : null,
                    newAddress != null && newAddress.getHouse() != null ? newAddress.getHouse() : null
                );
            }
            if (
                !Objects.equals(
                    oldAddress != null && oldAddress.getApartment() != null ? oldAddress.getApartment() : null,
                    newAddress != null && newAddress.getApartment() != null ? newAddress.getApartment() : null
                )
            ) {
                addLogHistory(
                    "apartment",
                    oldAddress != null && oldAddress.getApartment() != null ? oldAddress.getApartment() : null,
                    newAddress != null && newAddress.getApartment() != null ? newAddress.getApartment() : null
                );
            }
            if (
                !Objects.equals(
                    oldAddress != null && oldAddress.getStreet() != null ? oldAddress.getStreet() : null,
                    newAddress != null && newAddress.getStreet() != null ? newAddress.getStreet() : null
                )
            ) {
                addLogHistory(
                    "street",
                    oldAddress != null && oldAddress.getStreet() != null ? oldAddress.getStreet() : null,
                    newAddress != null && newAddress.getStreet() != null ? newAddress.getStreet() : null
                );
            }
            if (
                !Objects.equals(
                    oldAddress != null && oldAddress.getDistrict() != null ? oldAddress.getDistrict() : null,
                    newAddress != null && newAddress.getDistrict() != null ? newAddress.getDistrict() : null
                )
            ) {
                addLogHistory(
                    "district",
                    oldAddress != null && oldAddress.getDistrict() != null ? oldAddress.getDistrict().getName() : null,
                    newAddress != null && newAddress.getDistrict() != null ? newAddress.getDistrict().getName() : null
                );
            }
            if (
                !Objects.equals(
                    oldAddress != null && oldAddress.getRegion() != null ? oldAddress.getRegion() : null,
                    newAddress != null && newAddress.getRegion() != null ? newAddress.getRegion() : null
                )
            ) {
                addLogHistory(
                    "region",
                    oldAddress != null && oldAddress.getRegion() != null ? oldAddress.getRegion().getName() : null,
                    newAddress != null && newAddress.getRegion() != null ? newAddress.getRegion().getName() : null
                );
            }
        }
    }
}
