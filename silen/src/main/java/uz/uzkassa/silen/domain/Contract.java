package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.ContractDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.enumeration.Status;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Customer.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "contract",
    indexes = {
        @Index(columnList = "customer_id", name = "contract_customer_id_idx"),
        @Index(columnList = "number", name = "contract_number_idx")
    })
@SQLDelete(sql = "UPDATE contract SET deleted = 'true' WHERE id = ?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NoArgsConstructor
public class Contract extends BaseOrganizationEntity implements Serializable {

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @Column(name = "number")
    private String number;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "note")
    private String note;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACCEPTED;

    public ContractDTO toDto() {
        ContractDTO dto = new ContractDTO();
        dto.setId(getId());
        dto.setDate(getDate());
        dto.setNote(getNote());
        dto.setNumber(getNumber());
        dto.setStatus(getStatus());
        if (getStatus() != null) {
            dto.setStatusName(getStatus().getRuText());
        }
        if (getCustomer() != null) {
            dto.setCustomer(new SelectItem(String.valueOf(getCustomer().getId()), getCustomer().getName(), getCustomer().getTin()));
        }

        return dto;
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(this.getId(), this.getNumber(), this.getDate().toString());
    }
}
