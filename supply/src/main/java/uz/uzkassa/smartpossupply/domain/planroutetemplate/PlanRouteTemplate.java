package uz.uzkassa.smartpossupply.domain.planroutetemplate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.address.Region;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "plan_route_template",indexes = {
        @Index(columnList = "agent_id", name = "plan_route_template_agent_id_idx"),

})
@Entity
@SQLDelete(sql = "UPDATE plan_route_template SET deleted = true WHERE id=?")
//@SQLRestriction(value = "deleted = false")
public class PlanRouteTemplate extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 45481212L;

    @Column(name = "template_number")
    String templateNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "name")
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    CommonStatus status = CommonStatus.ACTIVE;

    @Column(name = "region_id")
    Long regionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", updatable = false, insertable = false)
    Region region;

    @Column(name = "agent_id")
    Long agentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", updatable = false, insertable = false)
    User agent;

    @OneToMany(mappedBy = "planRouteTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<PlanRouteTemplateItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "planRouteTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<PlanRouteTemplateExpeditor> expeditors = new ArrayList<>();

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @Column(name = "customer_branch_count", columnDefinition = "int4 default 0")
    int customerBranchCount = 0;

    public void addItem(PlanRouteTemplateItem planRouteTemplateItem) {
        planRouteTemplateItem.setPlanRouteTemplate(this);
        planRouteTemplateItem.setPlanRouteTemplateId(this.id);
        items.add(planRouteTemplateItem);
    }

    public void addExpeditor(PlanRouteTemplateExpeditor expeditor) {
        expeditor.setPlanRouteTemplate(this);
        expeditor.setPlanRouteTemplateId(this.id);
        expeditors.add(expeditor);
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.PLAN_ROUTE_TEMPLATE;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    public void setTemplateNumber(String templateNumber) {
        addLogHistory("templateNumber", this.templateNumber, templateNumber);
        this.templateNumber = templateNumber;
    }

    public void setName(String name) {
        addLogHistory("name", this.name, name);
        this.name = name;
    }

    public void setStatus(CommonStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setRegion(Region region) {
        addLogHistory("region", this.region != null ? this.region.getName() : null, region != null ? region.getName() : null);
        this.region = region;
    }

    public void setAgent(User agent) {
        if (!Objects.equals(this.agent, agent)) {
            addLogHistory("agent", this.agent != null ? this.agent.getFullName() : null, agent != null ? agent.getFullName() : null);
        }
        this.agent = agent;
    }

}
