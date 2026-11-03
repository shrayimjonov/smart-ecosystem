package uz.uzkassa.smartpossupply.domain.productserial;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialLookUpDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.ProductSerialStatus;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.05.2023
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "product_serial", indexes = {
        @Index(columnList = "serial_number", name = "product_serial_serial_number_idx"),
        @Index(columnList = "company_id", name = "product_serial_company_id_idx"),
        @Index(columnList = "product_id", name = "product_serial_product_id_idx"),
}, uniqueConstraints = {@UniqueConstraint(columnNames = {"serial_number", "company_id", "product_id"})})
@SQLDelete(sql = "UPDATE serials SET deleted = true WHERE id=?")
public class ProductSerial extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 82394832922L;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "serial_number")
    String serialNumber;

    @Column(name = "qty", precision = 25, scale = 2)
    BigDecimal qty = BigDecimal.ZERO;

    @Column(name = "base_price", precision = 25, scale = 2)
    BigDecimal basePrice = BigDecimal.ZERO;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ProductSerialStatus status = ProductSerialStatus.CREATED;

    @Override
    public String getName() {
        return this.getSerialNumber();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.PRODUCT_SERIAL;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    @Override
    public CommonDTO toCommonDTO() {
        return new CommonDTO(getId(), getName());
    }


    public ProductSerialLookUpDTO toLookUpDTO() {
        return new ProductSerialLookUpDTO(getId(), getName(), getQty(), getBasePrice());
    }
}
