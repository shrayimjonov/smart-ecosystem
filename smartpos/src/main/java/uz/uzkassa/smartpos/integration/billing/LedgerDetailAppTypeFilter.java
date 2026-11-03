package uz.uzkassa.smartpos.integration.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.smartpos.dto.filter.PageFilter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LedgerDetailAppTypeFilter extends PageFilter implements Serializable {
    private LocalDate from;
    private LocalDate to;
    private String search = "";
    private List<String> tins;
    private String appType;

    public String getSearch() {
        return this.search = this.search != null ? this.search.trim().replaceAll("[.!?,'{}]", "") : null;
    }

    @JsonIgnore
    public String getSearchForQuery() {
        return StringUtils.isNotEmpty(search) ? "%" + search.toLowerCase() + "%" : search;
    }

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = super.getMultiValueMap();
        if (tins != null) {
            map.put("tins", List.of(String.valueOf(tins)));
        }
        if (appType != null) {
            map.put("appType", List.of(appType));
        }
        if (from != null) {
            map.put("from", List.of(String.valueOf(from)));
        }
        if (to != null) {
            map.put("to", List.of(String.valueOf(to)));
        }
        if (search != null) {
            map.put("search", List.of(search));
        }
        return map;
    }
}
