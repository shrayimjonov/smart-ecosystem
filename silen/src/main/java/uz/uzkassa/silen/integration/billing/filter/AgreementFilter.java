package uz.uzkassa.silen.integration.billing.filter;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.List;

@Getter
@Setter
public class AgreementFilter extends IntegrationBaseFilter {
//    Long customerId;

//    Long xizmatId;

//    Long ctoId;// this is id of the company(in cto)

    String inn;

    String serviceType;

    String status;

    Boolean recurring;

    @Override
    public List<NameValuePair> convertToParams() {
        List<NameValuePair> params = super.convertToParams();
        if (StringUtils.isNotBlank(inn))
            params.add(new BasicNameValuePair("inn", inn));
        if (StringUtils.isNotBlank(status))
            params.add(new BasicNameValuePair("status", status));
        if (StringUtils.isNotBlank(serviceType))
            params.add(new BasicNameValuePair("serviceType", serviceType));
        if (recurring != null)
            params.add(new BasicNameValuePair("recurring", Boolean.toString(recurring)));

        return params;
    }
}
