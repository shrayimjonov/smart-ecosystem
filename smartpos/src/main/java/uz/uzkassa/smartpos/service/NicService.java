package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.dto.nic.TINAbstractDTO;
import uz.uzkassa.smartpos.security.SecurityUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class NicService {
    private final ApplicationProperties applicationProperties;
    private final RestTemplate restTemplate;

    public TINAbstractDTO getCustomerByTin(String tin) {
        String url = applicationProperties.getSoliq().getHost() + "np1/bytin/factura-all?" + (tin.length() > 9 ? "pinfl=" : "tin=") + tin;
        ResponseEntity<TINAbstractDTO> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(getHeaders()), TINAbstractDTO.class);
        return response.getBody();
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBasicAuth(applicationProperties.getSoliq().getUsername(), applicationProperties.getSoliq().getPassword());
        return headers;
    }
}
