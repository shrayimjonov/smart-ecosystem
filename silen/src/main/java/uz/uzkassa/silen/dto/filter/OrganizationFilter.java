package uz.uzkassa.silen.dto.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import uz.uzkassa.silen.enumeration.AggregationMode;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.integration.billing.enums.BillingDocumentType;

import java.util.EnumSet;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizationFilter extends Filter {

    Boolean demoAllowed;

    OrganizationType organizationType;

    EnumSet<OrganizationType> organizationTypes;

    boolean withOutManager = false;

    boolean active = true;

    Boolean xfileBinded;

    String parentId;

    @Schema(hidden = true)
    private Boolean withoutParent;

    @Schema(hidden = true)
    private Boolean withoutChild;

    String managerId;

    BillingDocumentType billingDocumentType;

    AggregationMode aggregationMode;

    public EnumSet<OrganizationType> getOrganizationTypes() {
        if (this.organizationTypes == null && this.organizationType != null) {
            this.organizationTypes = EnumSet.of(this.organizationType);
        }
        return organizationTypes;
    }

    public boolean hasOrganizationType() {
        return CollectionUtils.isNotEmpty(organizationTypes) || organizationType != null;
    }
}
