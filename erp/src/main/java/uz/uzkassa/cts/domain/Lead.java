package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.lead.LeadUTMDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "lead")
@Entity
@SQLDelete(sql = "UPDATE lead SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lead extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 7646923530L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "company_name")
    String companyName;

    @Column(name = "tin")
    String tin;

    @Column(name = "phone")
    String phone;

    @Column(name = "source")
    String source;

    @Column(name = "call_status")
    @Enumerated(EnumType.STRING)
    CallStatus callStatus = CallStatus.NOT_PROCESSED;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    LeadState state = LeadState.NEW;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    LeadStatus status = LeadStatus.NEW;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    LeadPaymentStatus paymentStatus = LeadPaymentStatus.NOT_SPECIFIED;

    @Column(name = "resolved_date")
    LocalDateTime resolvedDate;

    @Column(name = "role")
    String role;

    @Column(name = "reason")
    String reason;

    @Column(name = "utm_source")
    String utmSource;

    @Column(name = "utm_medium")
    String utmMedium;

    @Column(name = "utm_campaign")
    String utmCampaign;

    @Column(name = "utm_term")
    String utmTerm;

    @Column(name = "utm_content")
    String utmContent;

    @Column(name = "lead_payment_type")
    @Enumerated(EnumType.STRING)
    LeadPaymentType leadPaymentType;

    public void setName(String name) {
        addChangeLog("name", this.name, name);
        this.name = name;
    }

    public void setCompanyName(String companyName) {
        addChangeLog("companyName", this.companyName, companyName);
        this.companyName = companyName;
    }

    public void setTin(String tin) {
        addChangeLog("tin", this.tin, tin);
        this.tin = tin;
    }

    public void setPhone(String phone) {
        addChangeLog("phone", this.phone, phone);
        this.phone = phone;
    }

    public void setSource(String source) {
        addChangeLog("source", this.source, source);
        this.source = source;
    }

    public void setCallStatus(CallStatus callStatus) {
        addChangeLog("callStatus", this.callStatus != null ? this.callStatus.getName() : null, callStatus != null ? callStatus.getName() : null);
        this.callStatus = callStatus;
    }

    public void setState(LeadState state) {
        addChangeLog("state", this.state != null ? this.state.getName() : null, state != null ? state.getName() : null);
        this.state = state;
    }

    public void setStatus(LeadStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setPaymentStatus(LeadPaymentStatus paymentStatus) {
        addChangeLog("paymentStatus", this.paymentStatus != null ? this.paymentStatus.getName() : null, paymentStatus != null ? paymentStatus.getName() : null);
        this.paymentStatus = paymentStatus;
    }

    public void setRole(String role) {
        addChangeLog("role", this.role, role);
        this.role = role;
    }

    public void setReason(String reason) {
        addChangeLog("reason", this.reason, reason);
        this.reason = reason;
    }

    public void setUtmSource(String utmSource) {
        addChangeLog("utmSource", this.utmSource, utmSource);
        this.utmSource = utmSource;
    }

    public void setUtmMedium(String utmMedium) {
        addChangeLog("utmMedium", this.utmMedium, utmMedium);
        this.utmMedium = utmMedium;
    }

    public void setUtmCampaign(String utmCampaign) {
        addChangeLog("utmCampaign", this.utmCampaign, utmCampaign);
        this.utmCampaign = utmCampaign;
    }

    public void setUtmTerm(String utmTerm) {
        addChangeLog("utmTerm", this.utmTerm, utmTerm);
        this.utmTerm = utmTerm;
    }

    public void setUtmContent(String utmContent) {
        addChangeLog("utmContent", this.utmContent, utmContent);
        this.utmContent = utmContent;
    }
    public void setLeadPaymentType(LeadPaymentType leadPaymentType) {
        addChangeLog("leadPaymentType", this.leadPaymentType != null ? this.leadPaymentType.getName() : null, leadPaymentType != null ? leadPaymentType.getName() : null );
    }


    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.LEAD;
    }

    @Override
    public String getEntityName() {
        return this.name;
    }

    public LeadUTMDTO getUTM() {
        LeadUTMDTO utm = new LeadUTMDTO();
        utm.setUtmTerm(this.utmTerm);
        utm.setUtmSource(this.utmSource);
        utm.setUtmMedium(this.utmMedium);
        utm.setUtmContent(this.utmContent);
        utm.setUtmCampaign(this.utmCampaign);
        return utm;
    }
}
