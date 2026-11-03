package uz.uzkassa.smartpos.dto.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
public class BaseFilter extends PageFilter {
    @ToString.Include
    private String search = "";
    @ToString.Include
    private LocalDateTime from;
    @ToString.Include
    private LocalDateTime to;
    @ToString.Include
    private Long companyId;
    @ToString.Include
    private String tin;
    @ToString.Include
    private Long ctoId;
    @ToString.Include
    protected Long branchId;

    @ToString.Include
    private Long userId;

    @ToString.Include
    private Long terminalId;

    @ToString.Include
    private Long cityId;

    @Schema(hidden = true)
    public LocalDateTime getFrom() {
        return from != null ? from.with(LocalTime.MIN) : null;
    }

    @Schema(hidden = true)
    public LocalDateTime getFromDateTime() {
        return this.from;
    }

    @Schema(hidden = true)
    public LocalDateTime getTo() {
        return to != null ? to.with(LocalTime.MAX) : null;
    }

    @Schema(hidden = true)
    public LocalDate getFromLocalDate() {
        return from != null ? from.toLocalDate() : null;
    }

    public LocalDate getToLocalDate() {
        return to != null ? to.toLocalDate() : null;
    }

    @Schema(hidden = true)
    public String getSearchForQuery() {
        return StringUtils.isNotEmpty(search) ? "%" + search.toLowerCase().replace("_", "\\_") + "%" : search;
    }

    @Schema(hidden = true)
    public String getSearchForLookup() {
        return StringUtils.isNotEmpty(search) ? search.toLowerCase() + "%" : search;
    }

    @Schema(hidden = true)
    public String getSearch() {
        return search != null ? search.toLowerCase() : null;
    }

    @Schema(hidden = true)
    public MultiValueMap<String, String> getMultiValueMap() {
        var map = super.getMultiValueMap();
        if (search != null) {
            map.put("search", List.of(search));
        }
        if (from != null) {
            map.put("from", List.of(String.valueOf(from)));
        }
        if (to != null) {
            map.put("to", List.of(String.valueOf(to)));
        }
        if (companyId != null) {
            map.put("companyId", List.of(String.valueOf(companyId)));
        }
        if (tin != null) {
            map.put("tin", List.of(tin));
        }
        if (ctoId != null) {
            map.put("ctoId", List.of(String.valueOf(ctoId)));
        }
        if (branchId != null) {
            map.put("branchId", List.of(String.valueOf(branchId)));
        }
        if (userId != null) {
            map.put("userId", List.of(String.valueOf(userId)));
        }
        if (terminalId != null) {
            map.put("terminalId", List.of(String.valueOf(terminalId)));
        }
        if (cityId != null) {
            map.put("city", List.of(String.valueOf(cityId)));
        }
        return map;
    }
}
