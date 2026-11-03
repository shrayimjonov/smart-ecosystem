package uz.uzkassa.cts.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "rent_payment_history",
    indexes = {
        @Index(columnList = "customer_id", name = "customer_id_index")
    })
@Entity
@SQLDelete(sql = "UPDATE rent_payment_history SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class RentPaymentHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 3981428346L;

    @EmbeddedId
    private RentPaymentHistoryId id;

    @Column(name = "rent_act_accessory_id")
    private Long rentActAccessoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rent_act_accessory_id", insertable = false, updatable = false)
    private RentActAccessory rentActAccessory;

    @Column(name = "offer_id")
    private Long offerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", insertable = false, updatable = false)
    private RentOffer offer;

    @Column(name = "invoice_id")
    private Long invoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", insertable = false, updatable = false)
    private RentInvoice invoice;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @MapsId("customerId")
    private Customer customer;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "amount", precision = 19, scale = 3)
    private BigDecimal amount;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    private boolean deleted = false;

    @Column(name = "bonus")
    private BigDecimal bonus = BigDecimal.ZERO;

    @CreatedBy
    @Column(name = "creator_id")
    private Long creatorId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", updatable = false, insertable = false)
    private User creator;

    @LastModifiedBy
    @Column(name = "last_modifier_id")
    private Long lastModifierId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "last_modifier_id", updatable = false, insertable = false)
    private User lastModifier;

    @CreatedDate
    @Column(name = "created_date")
    @JsonIgnore
    private LocalDateTime createdDate = LocalDateTime.now();

    @LastModifiedDate
    @Column(name = "last_modified_date")
    @JsonIgnore
    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RentPaymentHistory history = (RentPaymentHistory) o;

        return id.equals(history.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
