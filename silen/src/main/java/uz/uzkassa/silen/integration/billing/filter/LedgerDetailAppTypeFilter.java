package uz.uzkassa.silen.integration.billing.filter;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LedgerDetailAppTypeFilter extends IntegrationBaseFilter {

    private List<String> tins;
    private String appType;

    @Override
    public List<NameValuePair> convertToParams() {
        List<NameValuePair> params = super.convertToParams();

        if (CollectionUtils.isNotEmpty(tins)) {
            params.add(new BasicNameValuePair("tins", String.join(",", tins)));
        }
        if (StringUtils.isNotBlank(appType)) {
            params.add(new BasicNameValuePair("appType", appType));
        }
        return params;
    }
}
