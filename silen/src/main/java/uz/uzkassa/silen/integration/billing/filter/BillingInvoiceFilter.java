package uz.uzkassa.silen.integration.billing.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import uz.uzkassa.silen.enumeration.AppType;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BillingInvoiceFilter extends IntegrationBaseFilter {
    private String status;
    private Long docNumber;
    private AppType appType;
    private Long xizmatId;
    private String xfileStatus;

    @Override
    public List<NameValuePair> convertToParams() {
        List<NameValuePair> params = super.convertToParams();
        if (StringUtils.isNotEmpty(getXfileStatus())) {
            params.add(new BasicNameValuePair("xfileStatus", getXfileStatus()));
        }
        if (StringUtils.isNotEmpty(getStatus())) {
            params.add(new BasicNameValuePair("status", getStatus()));
        }
        params.add(new BasicNameValuePair("orderBy", "createdDate"));
        params.add(new BasicNameValuePair("appType", AppType.SILEN.name()));
        return params;
    }
}
