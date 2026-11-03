package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.RepairTicketStatus;
import uz.uzkassa.cts.enums.RepairTicketVisitType;
import uz.uzkassa.cts.enums.RepairType;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Table(name = "repair_ticket",
        indexes = {
                @Index(columnList = "type_id", name = "repair_ticket_type_id_idx"),
                @Index(columnList = "from_branch_id", name = "repair_ticket_from_branch_id_idx"),
                @Index(columnList = "status", name = "repair_ticket_customer_status_idx"),
                @Index(columnList = "created_date", name = "repair_ticket_created_date_idx"),
                @Index(columnList = "accepted_date", name = "repair_ticket_accepted_date_idx"),
                @Index(columnList = "customer_id", name = "repair_ticket_customer_id_idx"),
                @Index(columnList = "master_id", name = "repair_ticket_master_id_idx"),
                @Index(columnList = "visit_type", name = "repair_ticket_visit_type_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE repair_ticket SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairTicket extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 2012017506L;

    @Column(name = "repair_ticket_number")
    String repairTicketNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "device_id", nullable = false)
    Long deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", updatable = false, insertable = false)
    Device device;

    @Column(name = "customer_id", nullable = false)
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @Column(name = "master_id")
    Long masterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", updatable = false, insertable = false)
    User master;

    @Column(name = "phone")
    String phone;

    @Column(name = "from_branch_id", nullable = false)
    Long fromBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_branch_id", insertable = false, updatable = false)
    Branch fromBranch;

    @Column(name = "to_branch_id")
    Long toBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_branch_id", insertable = false, updatable = false)
    Branch toBranch;

    @Column(name = "current_branch_id")
    Long currentBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_branch_id", insertable = false, updatable = false)
    Branch currentBranch;

    @Column(name = "type_id", nullable = false)
    Long typeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", insertable = false, updatable = false)
    Reference type;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    RepairTicketStatus status = RepairTicketStatus.NEW;

    @Column(name = "repair_status_id")
    Long repairStatusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_status_id", insertable = false, updatable = false)
    Reference repairStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_type")
    RepairTicketVisitType visitType;

    @Enumerated(EnumType.STRING)
    @Column(name = "repair_type")
    RepairType repairType;

    @Column(name = "accepted_date")
    LocalDateTime acceptedDate;

    @ManyToMany
    @JoinTable(name = "repair_ticket_reference",
            joinColumns = {@JoinColumn(name = "repair_ticket_id")},
            inverseJoinColumns = {@JoinColumn(name = "reference_id")})
    Set<Reference> equipments = new HashSet<>();

    @OneToMany(mappedBy = "repairTicket", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    @OrderBy("id")
    List<RepairTicketItem> items = new ArrayList<>();

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "description")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String description;

    public void addItem(RepairTicketItem repairTicketItem) {
        repairTicketItem.setRepairTicket(this);
        repairTicketItem.setRepairTicketId(this.id);
        items.add(repairTicketItem);
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.REPAIR_TICKET;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    @Override
    public String getName() {
        return getRepairTicketNumber();
    }

    public void setPhone(String phone) {
        addChangeLog("phone", this.phone, phone);
        this.phone = phone;
    }

    public void setStatus(RepairTicketStatus status) {
        addChangeLog("status", this.status.getName(), status.getName());
        this.status = status;
    }

    public void setFromBranch(Branch fromBranch) {
        addChangeLog("fromBranch", this.fromBranch != null ? this.fromBranch.getName() : null, fromBranch != null ? fromBranch.getName() : null);
        this.fromBranch = fromBranch;
    }

    public void setToBranch(Branch toBranch) {
        addChangeLog("toBranch", this.toBranch != null ? this.toBranch.getName() : null, toBranch != null ? toBranch.getName() : null);
        this.toBranch = toBranch;
    }

    public void setMaster(User master) {
        addChangeLog("master", this.master != null ? this.master.getName() : null, master != null ? master.getName() : null);
        this.master = master;
    }

    public void setType(Reference type) {
        addChangeLog("type", this.type != null ? this.type.getName() : null, type != null ? type.getName() : null);
        this.type = type;
    }

    public void setRepairType(RepairType repairType) {
        addChangeLog("repairType", this.repairType != null ? this.repairType.getName() : null, repairType != null ? repairType.getName() : null);
        this.repairType = repairType;
    }

    public void setAcceptDate(LocalDateTime acceptedDate) {
        addChangeLog("acceptedDate", this.acceptedDate != null ? this.acceptedDate.toString() : null, acceptedDate != null ? acceptedDate.toString() : null);
        this.acceptedDate = acceptedDate;
    }
}
