package uz.uzkassa.smartpossupply.domain.acceptancetransferact;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActLinkDTO;
import uz.uzkassa.smartpossupply.enums.AcceptanceTransferActStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "acceptance_transfer_act",
    indexes = {
        @Index(columnList = "act_id", name = "acceptance_transfer_act_id_idx"),
        @Index(columnList = "act_no", name = "acceptance_transfer_act_no_idx"),
        @Index(columnList = "seller_pinfl", name = "acceptance_transfer_act_seller_pinfl_idx"),
        @Index(columnList = "buyer_tin_or_pinfl", name = "acceptance_transfer_act_buyer_tin_or_pinfl_idx"),
        @Index(columnList = "buyer_id", name = "acceptance_transfer_act_buyer_id_idx"),
        @Index(columnList = "seller_id", name = "acceptance_transfer_act_seller_id_idx"),
        @Index(columnList = "company_id", name = "acceptance_transfer_act_company_id_idx"),
        @Index(columnList = "silen_id", name = "acceptance_transfer_act_silen_id_idx"),
        @Index(columnList = "stock_out_id", name = "acceptance_transfer_act_stock_out_id_idx"),
        @Index(columnList = "deleted", name = "acceptance_transfer_act_deleted_idx")
    }
)
@SQLDelete(sql = "UPDATE acceptance_transfer_act SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
public class AcceptanceTransferAct extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 723783223L;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "outgoing", columnDefinition = "boolean default false")
    boolean outgoing = false;

    @Column(name = "silen_id")
    String silenId;

    @Column(name = "smartpos_id")
    Long smartposId;

    @Column(name = "act_id")
    String actId;

    @Column(name = "act_no")
    String actNo;

    @Column(name = "act_date")
    LocalDate actDate;

    @Column(name = "seller_pinfl")
    String sellerPinfl;

    @Column(name = "buyer_tin_or_pinfl")
    String buyerTinOrPinfl;

    @Column(name = "total_price", precision = 25, scale = 2)
    BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    AcceptanceTransferActStatus status = AcceptanceTransferActStatus.DRAFT;

    @Column(name = "acceptance_transfer_act_detail_id")
    Long acceptanceTransferActDetailId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "acceptance_transfer_act_detail_id", updatable = false, insertable = false)
    AcceptanceTransferActDetail acceptanceTransferActDetail;

    @Column(name = "buyer_id")
    Long buyerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", updatable = false, insertable = false)
    Company buyer;

    @Column(name = "seller_id")
    Long sellerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", updatable = false, insertable = false)
    Company seller;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "stock_out_id")
    Long stockOutId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_out_id", updatable = false, insertable = false)
    Stock stockOut;

    public AcceptanceTransferActLinkDTO toLinkDTO() {
        return new AcceptanceTransferActLinkDTO(getId(), getActNo(), getActDate());
    }

    @Override
    public String getName() {
        return getActNo();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.ACCEPTANCE_TRANSFER_ACT;
    }

    @Override
    public String getEntityName() {
        return this.getName();
    }
}
