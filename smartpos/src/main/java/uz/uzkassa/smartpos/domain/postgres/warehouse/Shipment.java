package uz.uzkassa.smartpos.domain.postgres.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.uzkassa.smartpos.domain.postgres.base.SimpleEntity;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.edocs.invoice.Invoice;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.enums.ShipmentStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A Shipment.
 */
@Getter
@Setter
@Entity
@Table(name = "shipment")
@EntityListeners(AuditingEntityListener.class)
public class Shipment extends SimpleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 65411L;
    @NotNull
    @Column(name = "number")
    private String number;

    @Column(name = "int_number", columnDefinition = "int4 default 0", nullable = false)
    private int intNumber;

    @Column(name = "shipment_date")
    private LocalDateTime shipmentDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status = ShipmentStatus.NEW;

    @NotNull
    @Column(name = "company_id")
    private Long companyId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    private Company company;
    @NotBlank
    private String customerTin;
    @NotBlank
    private String customerName;

    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "invoice_id")
    private Long invoiceId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", insertable = false, updatable = false)
    private Invoice invoice;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ShipmentItem> items = new ArrayList<>();

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(getId(), getNumber(), String.valueOf(getShipmentDate()));
    }
}
