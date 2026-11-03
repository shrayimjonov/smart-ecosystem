package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ActDocFlowStatus;
import uz.uzkassa.cts.enums.ContractStatus;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "rent_device_act_return")
@Entity
@SQLDelete(sql = "UPDATE rent_device_act_return SET deleted = 'true' WHERE id=?")
@SQLRestriction("deleted = 'false'")
public class RentDeviceActOfReturn extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 6500809792L;

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @OneToOne(mappedBy = "rentDeviceActOfReturn", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RentDeviceActDetail rentDeviceActDetail;

    @Column(name = "price_for_1_pc")
    private BigDecimal priceFor1pc;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ContractStatus status = ContractStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_flow_status")
    private ActDocFlowStatus docFlowStatus;

    @Column(name = "accept_date")
    private LocalDateTime acceptDate;

    @Column(name = "doc_flow_reason")
    private String docFlowReason;

    @Column(name = "file_uri")
    private String fileUri;

    @Column(name = "file_name")
    private String fileName;

    public void addChild(RentDeviceActDetail rentDeviceActDetail) {
        this.rentDeviceActDetail = rentDeviceActDetail;
        rentDeviceActDetail.setRentDeviceActOfReturn(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RentDeviceActOfReturn rentActOfReturn = (RentDeviceActOfReturn) o;

        return getId() != null ? getId().equals(rentActOfReturn.getId()) : rentActOfReturn.getId() == null;
    }
}
