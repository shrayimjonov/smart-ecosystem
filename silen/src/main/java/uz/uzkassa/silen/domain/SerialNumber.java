package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.SerialNumberListDTO;
import uz.uzkassa.silen.enumeration.SerialNumberStatus;

import java.io.Serializable;
import java.time.LocalDate;


@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "serial_number")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE serial_number SET deleted = 'true' WHERE id=?")
public class SerialNumber extends BaseOrganizationEntity implements Serializable {

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "description")
    private String description;

    @Column(name = "start_quantity")
    private Integer startQuantity;

    @Column(name = "quantity", columnDefinition = "int4 default 0")
    private Integer quantity;

    @Column(name = "for_expertise")
    private Integer forExpertise;

    @Column(name = "production_date")
    private LocalDate productionDate;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;


    @Column(name = "status", length = 10)
    @Enumerated(EnumType.STRING)
    private SerialNumberStatus status;

    public SerialNumberListDTO toDto() {
        SerialNumberListDTO serialNumberListDTO = new SerialNumberListDTO();
        serialNumberListDTO.setSerialNumber(getSerialNumber());
        serialNumberListDTO.setProductId(getProductId());
        serialNumberListDTO.setStatus(getStatus());
        if (getStatus() != null) {
            serialNumberListDTO.setStatusName(getStatus().getNameRu());
        }
        serialNumberListDTO.setProductionDate(getProductionDate());
        if (getProduct() != null) {
            serialNumberListDTO.setProductName(getProduct().getVisibleName());
            serialNumberListDTO.setBarcode(getProduct().getBarcode());
        }
        serialNumberListDTO.setId(getId());
        serialNumberListDTO.setCreatedDate(getCreatedDate());
        serialNumberListDTO.setLastModifiedDate(getLastModifiedDate());
        serialNumberListDTO.setStartQuantity(getStartQuantity());
        serialNumberListDTO.setQuantity(getQuantity());
        serialNumberListDTO.setForExpertise(this.getForExpertise());
        serialNumberListDTO.setDescription(getDescription());
        return serialNumberListDTO;
    }

    public String getNameWithDate() {
        return String.format(" %s %s.%s", getSerialNumber(), getProductionDate().getMonthValue(), getProductionDate().getYear());
    }

    public static String getNameWithDate(String serialNumber, LocalDate productionDate) {
        return String.format(" %s %s.%s", serialNumber, productionDate.getMonthValue(), productionDate.getYear());
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(this.getId(), this.getNameWithDate(), this.getDescription());
    }
}
