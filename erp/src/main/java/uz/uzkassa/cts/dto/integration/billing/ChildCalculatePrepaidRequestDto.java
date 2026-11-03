package uz.uzkassa.cts.dto.integration.billing;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import uz.uzkassa.cts.enums.billing.AgreementStatus;

import java.util.List;

@Getter
@Setter
public class ChildCalculatePrepaidRequestDto {
    @NotNull
    @NotEmpty
    private String tin;
    private String childTin;
    private AgreementStatus status;

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = new LinkedMultiValueMap<String, String>();
        if (StringUtils.hasText(tin)) {
            map.put("tin", List.of(tin));
        }
        if (StringUtils.hasText(childTin)) {
            map.put("childTin", List.of(childTin));
        }
        if (status != null) {
            map.put("status", List.of(status.name()));
        }
        return map;
    }
}
