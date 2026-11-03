package uz.uzkassa.smartpos.integration.ofd;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.config.OfdClientConfiguration;
import uz.uzkassa.smartpos.dto.NeighborhoodDTO;
import uz.uzkassa.smartpos.dto.nic.OfdResponseDataDTO;
import uz.uzkassa.smartpos.integration.cto.Credential;
import uz.uzkassa.smartpos.web.rest.vm.TokenDTO;

import java.util.List;
import java.util.Objects;

@Component
@FeignClient(name = "Ofd", url = "${application.ofd.host}", configuration = OfdClientConfiguration.class)
public interface OfdClient {
    @PostMapping("/auth/login")
    ResponseEntity<TokenDTO> login(@RequestBody Credential credential);

    @PostMapping(value = "/ccm-api/office/add")
    ResponseEntity<OfdCustomerBranchResponseDTO> addBranch(@RequestHeader(value = "Authorization") String token,
                                                           @RequestBody OfdCustomerBranchAddUpdateDTO dto);


    @GetMapping("/ccm-api/info/neighborhood/by-region-district")
    ResponseEntity<OfdResponseDataDTO<List<NeighborhoodDTO>>> neighborhoodList(@RequestHeader(value = "Authorization") String token,
                                                                               @RequestParam("region_code") Long regionCode, @RequestParam("district_code") Long districtCode);

    default String getToken(String username, String password) {
        ResponseEntity<TokenDTO> responseEntity = login(new Credential(username, password));
        if (responseEntity != null) {
            if (responseEntity.getBody() != null && StringUtils.isNotEmpty(responseEntity.getBody().getAccessToken())) {
                return "Bearer " + responseEntity.getBody().getAccessToken();
            }
            if (!CollectionUtils.isEmpty(responseEntity.getHeaders()) && !CollectionUtils.isEmpty(responseEntity.getHeaders().get("authorization"))) {
                return Objects.requireNonNull(responseEntity.getHeaders().get("authorization")).getFirst();
            }
        }
        return null;
    }
}
