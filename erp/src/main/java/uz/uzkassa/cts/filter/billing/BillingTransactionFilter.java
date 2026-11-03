package uz.uzkassa.cts.filter.billing;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillingTransactionFilter extends PageFilter {
    String appType;
    String companyType;
    String lastId;
    @NotNull
    String tin;
    String debitOrCredit;
    LocalDateTime createdDate;

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = new LinkedMultiValueMap<String, String>();
        if (StringUtils.isNotEmpty(this.lastId)) {
            map.put("lastId", List.of(this.lastId));
        }
        if (StringUtils.isNotEmpty(this.companyType)) {
            map.put("companyType", List.of(this.companyType));
        }
        if (StringUtils.isNotEmpty(this.appType)) {
            map.put("appType", List.of(this.appType));
        }
        if (StringUtils.isNotEmpty(this.debitOrCredit)) {
            map.put("debitOrCredit", List.of(this.debitOrCredit));
        }
        return map;
    }
}

