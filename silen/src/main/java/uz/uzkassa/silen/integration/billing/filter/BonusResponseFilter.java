package uz.uzkassa.silen.integration.billing.filter;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BonusResponseFilter extends IntegrationBaseFilter {
    private String appType;

    private String invoiceStatus;

    private List<String> managerId;

    @Override
    public List<NameValuePair> convertToParams() {
        List<NameValuePair> params = super.convertToParams();

        if (StringUtils.isNotBlank(appType)) {
            params.add(new BasicNameValuePair("appType", appType));
        }
        if (StringUtils.isNotBlank(invoiceStatus)) {
            params.add(new BasicNameValuePair("invoiceStatus", invoiceStatus));
        }
        if (CollectionUtils.isNotEmpty(managerId)) {
            params.add(new BasicNameValuePair("managerId", StringUtils.join(managerId, ",")));
        }
        return params;
    }
}
