package uz.uzkassa.cts.integrations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.lead.LeadRequestDTO;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.security.SecurityUtils;

@Component
@Slf4j
public class ErpMarketingClient extends BaseClient {

    @Override
    public String authenticate() {
        return null;
    }

    @Override
    public String getToken() {
        return null;
    }

    @Override
    public HttpHeaders getHeaders() {
        return SecurityUtils.getHeader();
    }

    @Override
    public IntegrationType getIntegrationType() {
        return null;
    }

    public void createGoogleSheet(LeadRequestDTO leadDTO) {
        restTemplate.exchange(
                applicationProperties.getErpMarketingProperty().getGoogleSheetSiteUrl(),
                HttpMethod.POST,
                new HttpEntity<>(leadDTO, getHeaders()),
                Void.class
        );
    }
}
