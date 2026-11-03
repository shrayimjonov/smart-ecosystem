package uz.uzkassa.silen.dto.filter;


import lombok.Getter;
import lombok.Setter;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FacturaInvestmentFilter {

    private String sellerTin;

    private String buyerTin;

    private String objectId;

    private String buyerAccount;

    public List<NameValuePair> convertToParams() {
        List<NameValuePair> params = new ArrayList<>();
        if (sellerTin != null) {
            params.add(new BasicNameValuePair("sellerTin", sellerTin));
        }
        if (buyerTin != null) {
            params.add(new BasicNameValuePair("buyerTin", buyerTin));
        }
        if (objectId != null) {
            params.add(new BasicNameValuePair("objectId", objectId));
        }
        if (buyerAccount != null) {
            params.add(new BasicNameValuePair("buyerAccount", buyerAccount));
        }
        return params;
    }
}
