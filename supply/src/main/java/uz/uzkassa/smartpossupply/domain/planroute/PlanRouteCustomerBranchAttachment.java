package uz.uzkassa.smartpossupply.domain.planroute;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.enums.PlanRouteAttachmentType;

import java.io.Serial;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 19:32
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "plan_route_customer_branch_attachment", indexes = {
        @Index(columnList = "plan_route_customer_branch_id", name = "plan_route_customer_branch_attachment_plan_route_customer_branch_id_idx"),
})
@Entity
@SQLDelete(sql = "UPDATE plan_route_customer_branch_attachment SET deleted = true WHERE id=?")
public class PlanRouteCustomerBranchAttachment extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 24238245343L;

    @Column(name = "plan_route_customer_branch_id")
    Long planRouteCustomerBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_route_customer_branch_id", insertable = false, updatable = false)
    PlanRouteCustomerBranch planRouteCustomerBranch;

    @Column(name = "attachment_id")
    Long attachmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", insertable = false, updatable = false)
    FileUpload attachment;

    @Enumerated(EnumType.STRING)
    @Column(name = "attachment_type")
    PlanRouteAttachmentType attachmentType;
}
