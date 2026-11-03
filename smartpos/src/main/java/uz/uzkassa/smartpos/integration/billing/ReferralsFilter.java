package uz.uzkassa.smartpos.integration.billing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.smartpos.dto.filter.IntegrationBaseFilter;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ReferralsFilter extends IntegrationBaseFilter {

    private String senderTin;

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = super.getMultiValueMap();
        if (senderTin != null) {
            map.put("senderTin", List.of(senderTin));
        }
        return map;
    }
}
