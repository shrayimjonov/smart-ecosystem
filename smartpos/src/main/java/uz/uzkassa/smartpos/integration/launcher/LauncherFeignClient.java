package uz.uzkassa.smartpos.integration.launcher;

import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uz.uzkassa.smartpos.config.FeignPropagateBadRequestsConfiguration;
import uz.uzkassa.smartpos.dto.integration.LoginRequestDto;
import uz.uzkassa.smartpos.dto.integration.launcher.CtoActionDTO;
import uz.uzkassa.smartpos.dto.integration.launcher.JWTToken;
import uz.uzkassa.smartpos.dto.launcher.GetLastVersionRequestDTO;
import uz.uzkassa.smartpos.dto.launcher.GetLastVersionResponseDTO;

import java.util.Objects;

@Component
@FeignClient(name = "launcherFeignClient", url = "${application.launcher.host}", configuration = FeignPropagateBadRequestsConfiguration.class)
public interface LauncherFeignClient {

    @PostMapping(value = "/api/authenticate")
    ResponseEntity<JWTToken> login(@RequestBody LoginRequestDto dto);

    @PostMapping(value = "/api/v1/admin/terminals/action")
    ResponseEntity<?> terminalAction(@RequestHeader(value = "Authorization") String token, @Valid @RequestBody CtoActionDTO actionDTO);

    @PostMapping(value = "/api/v2/terminals/application/get-active-version")
    ResponseEntity<GetLastVersionResponseDTO> getActiveVersion(@RequestHeader(value = "Authorization") String token, @Valid @RequestBody GetLastVersionRequestDTO requestDTO);

    default String getToken(String username, String password) {
        ResponseEntity<JWTToken> responseEntity = login(new LoginRequestDto(username, password));
        if (responseEntity != null) {
            if (responseEntity.getBody() != null && StringUtils.isNotEmpty(responseEntity.getBody().getIdToken())) {
                return "Bearer " + responseEntity.getBody().getIdToken();
            }
            if (!CollectionUtils.isEmpty(responseEntity.getHeaders()) && !CollectionUtils.isEmpty(responseEntity.getHeaders().get("authorization"))) {
                return Objects.requireNonNull(responseEntity.getHeaders().get("authorization")).getFirst();
            }
        }
        return null;
    }
}
