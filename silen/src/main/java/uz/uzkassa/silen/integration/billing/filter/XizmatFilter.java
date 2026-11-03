package uz.uzkassa.silen.integration.billing.filter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.enumeration.ProductGroup;
import uz.uzkassa.silen.integration.billing.enums.PricingModel;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class XizmatFilter extends IntegrationBaseFilter {
    private String serviceType;
    private Boolean isReccurring;
    private PricingModel pricingModel;
    private boolean withActiveAgreementCounts = false;
    private boolean all = false;
    private Integer branchInstance = 1;
    @NotNull
    @NotEmpty
    private String companyInn;
    private Boolean grouped;
    private ProductGroup productGroup;
    private OrganizationType organizationType;
    private Boolean displayInCabinet;

    @Override
    public List<NameValuePair> convertToParams() {
        List<NameValuePair> params = super.convertToParams();
        if (serviceType != null) {
            params.add(new BasicNameValuePair("serviceType", serviceType));
        }
        if(isReccurring != null) {
            params.add(new BasicNameValuePair("isReccurring", String.valueOf(isReccurring)));
        }
        if (pricingModel != null) {
            params.add(new BasicNameValuePair("pricingModel", pricingModel.name()));
        }
        if (withActiveAgreementCounts) {
            params.add(new BasicNameValuePair("withActiveAgreementCounts", "true"));
        }
        if (all) {
            params.add(new BasicNameValuePair("all", "true"));
        }
        if (branchInstance != null) {
            params.add(new BasicNameValuePair("branchInstance", String.valueOf(branchInstance)));
        }
        if (companyInn != null) {
            params.add(new BasicNameValuePair("companyInn", companyInn));
        }
        if (grouped != null) {
            params.add(new BasicNameValuePair("grouped", String.valueOf(grouped)));
        }
        if (productGroup != null) {
            params.add(new BasicNameValuePair("productGroup", productGroup.name()));
        }
        if (organizationType != null) {
            params.add(new BasicNameValuePair("organizationType", organizationType.name()));
        }
        return params;
    }
}
