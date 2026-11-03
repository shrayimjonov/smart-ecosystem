package uz.uzkassa.smartpossupply.domain.planroute;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.address.Region;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.planroutetemplate.PlanRouteTemplate;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteReportDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteReportTotalQtyDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.PlanRouteStatus;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 19:53
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SqlResultSetMappings({
        @SqlResultSetMapping(name = "PlanRouteReportByAgentMapper",
                classes = {
                        @ConstructorResult(targetClass = PlanRouteReportDTO.class,
                                columns = {
                                        @ColumnResult(name = "agentId", type = Long.class),
                                        @ColumnResult(name = "agentFirstName", type = String.class),
                                        @ColumnResult(name = "agentLastName", type = String.class),
                                        @ColumnResult(name = "customerId", type = Long.class),
                                        @ColumnResult(name = "customerName", type = String.class),
                                        @ColumnResult(name = "customerTin", type = String.class),
                                        @ColumnResult(name = "customerBrand", type = String.class),
                                        @ColumnResult(name = "branchId", type = Long.class),
                                        @ColumnResult(name = "branchName", type = String.class),
                                        @ColumnResult(name = "warehouseId", type = Long.class),
                                        @ColumnResult(name = "warehouseName", type = String.class),
                                        @ColumnResult(name = "categoryId", type = Long.class),
                                        @ColumnResult(name = "categoryName", type = String.class),
                                        @ColumnResult(name = "totalPriceWithoutVat", type = BigDecimal.class),
                                        @ColumnResult(name = "totalVatPrice", type = BigDecimal.class),
                                        @ColumnResult(name = "totalPrice", type = BigDecimal.class),
                                        @ColumnResult(name = "totalProducts", type = BigDecimal.class),
                                        @ColumnResult(name = "paymentType", type = PaymentType.class)
                                })
                }),
        @SqlResultSetMapping(name = "PlanRouteReportTotalQtyMapper",
                classes = {
                        @ConstructorResult(targetClass = PlanRouteReportTotalQtyDTO.class,
                                columns = {
                                        @ColumnResult(name = "customerCount", type = Long.class),
                                        @ColumnResult(name = "agentCount", type = Long.class),
                                        @ColumnResult(name = "totalPriceWithoutVat", type = BigDecimal.class),
                                        @ColumnResult(name = "totalVatPrice", type = BigDecimal.class),
                                        @ColumnResult(name = "totalPrice", type = BigDecimal.class),
                                        @ColumnResult(name = "totalProductCount", type = BigDecimal.class),
                                        @ColumnResult(name = "totalProducts", type = BigDecimal.class),
                                })
                }),
})
@Table(name = "plan_route", indexes = {
        @Index(columnList = "plan_route_number", name = "plan_route_plan_route_number_idx"),
        @Index(columnList = "agent_id", name = "plan_route_agent_id_idx"),
        @Index(columnList = "template_id", name = "plan_route_template_id_idx")
})
@Entity
@SQLDelete(sql = "UPDATE plan_route SET deleted = true WHERE id=?")
public class PlanRoute extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 1423L;

    @Column(name = "plan_route_number", nullable = false)
    String planRouteNumber;

    @Column(name = "increment_number", nullable = false)
    Long incrementNumber;

    @Column(name = "from_date", nullable = false)
    LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    LocalDate toDate;

    @Column(name = "agent_id", nullable = false)
    Long agentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", insertable = false, updatable = false)
    User agent;

    @Column(name = "region_id")
    Long regionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", insertable = false, updatable = false)
    Region region;

    @Column(name = "template_id", nullable = false)
    Long templateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    PlanRouteTemplate template;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    PlanRouteStatus status = PlanRouteStatus.OPEN;

    @OneToMany(mappedBy = "planRoute", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<PlanRouteItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "planRoute", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<PlanRouteExpeditor> expeditors = new ArrayList<>();

    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    Company company;

    public void addItem(PlanRouteItem planRouteItem) {
        planRouteItem.setPlanRoute(this);
        planRouteItem.setPlanRouteId(this.id);
        items.add(planRouteItem);
    }

    public void addExpeditor(PlanRouteExpeditor expeditor) {
        expeditor.setPlanRoute(this);
        expeditor.setPlanRouteId(this.id);
        expeditors.add(expeditor);
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.PLAN_ROUTE;
    }

    @Override
    public String getName() {
        return this.getPlanRouteNumber();
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    public void setPlanRouteNumber(String planRouteNumber) {
        addLogHistory("planRouteNumber", this.planRouteNumber, planRouteNumber);
        this.planRouteNumber = planRouteNumber;
    }

    public void setFromDate(LocalDate fromDate) {
        addLogHistory("fromDate", this.fromDate, fromDate);
        this.fromDate = fromDate;
    }

    public void setToDate(LocalDate toDate) {
        addLogHistory("toDate", this.toDate, toDate);
        this.toDate = toDate;
    }

    public void setAgent(User agent) {
        if (!Objects.equals(this.agent, agent)) {
            addLogHistory("agent", this.agent != null ? this.agent.getFullName() : null, agent != null ? agent.getFullName() : null);
        }
        this.agent = agent;
    }

    public void setRegion(Region region) {
        addLogHistory("region", this.region != null ? this.region.getName() : null, region != null ? region.getName() : null);
        this.region = region;
    }

    public void setStatus(PlanRouteStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }
}
