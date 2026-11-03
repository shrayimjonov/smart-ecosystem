package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.ProductAttributesDTO;

import java.io.Serializable;

@ToString
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "product_attributes",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "attribute_id"})
    },
    indexes = {
        @Index(name = "product_attributes_product_id_idx", columnList = "product_id"),
        @Index(name = "product_attributes_attribute_id_idx", columnList = "attribute_id")
    })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProductAttributes extends BaseOrganizationEntity implements Serializable {

    @Column(name = "product_id", insertable = false, updatable = false)
    private String productId;

    @Column(name = "attribute_id")
    private Long attributeId;

    @Column(name = "attribute_value")
    private String attributeValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", insertable = false, updatable = false)
    private Attributes attribute;

    public ProductAttributesDTO toDto() {
        ProductAttributesDTO dto = new ProductAttributesDTO();
        dto.setAttributeId(getAttributeId());
        dto.setProductId(getProductId());
        dto.setAttributeValue(getAttributeValue());
        if(this.getAttribute() != null){
            dto.setAttributes(this.getAttribute().toDto());
        }
        return dto;
    }
}
