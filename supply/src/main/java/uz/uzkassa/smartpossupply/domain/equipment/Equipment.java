package uz.uzkassa.smartpossupply.domain.equipment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.EquipmentType;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@ToString
@Table(name = "equipment")
@SQLDelete(sql = "UPDATE equipment SET deleted = true WHERE id=?")
public class Equipment extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 85938455334L;

    @Column(name = "name")
    String name;

    @Column(name = "brand")
    String brand;

    @Column(name = "inventory_number")
    String inventoryNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    EquipmentType type;

    @Column(name = "customer_branch_id")
    Long customerBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    CustomerBranch customerBranch;

    @Column(name = "photo_id")
    Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", updatable = false, insertable = false)
    FileUpload photo;

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.EQUIPMENT;
    }

    @Override
    public String getEntityName() {
        return getName();
    }
}