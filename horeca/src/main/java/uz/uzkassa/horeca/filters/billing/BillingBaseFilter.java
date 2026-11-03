package uz.uzkassa.horeca.filters.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
public class BillingBaseFilter extends BillingPageFilter {

    @ToString.Include
    private String search = "";

    @ToString.Include
    private LocalDateTime from;

    @ToString.Include
    private LocalDateTime to;

    @ToString.Include
    private Long companyId;

    @ToString.Include
    private Long ctoId;

    @ToString.Include
    private Long branchId;

    @ToString.Include
    private Long userId;

    @ToString.Include
    private Long terminalId;

    @ToString.Include
    private Long cityId;

    public LocalDateTime getFrom() {
        return from != null ? from.with(LocalTime.MIN) : null;
    }

    public LocalDateTime getTo() {
        return to != null ? to.with(LocalTime.MAX) : null;
    }

    @JsonIgnore
    
    public String getSearchForQuery() {
        return StringUtils.isNotEmpty(search) ? "%" + search.toLowerCase() + "%" : search;
    }

    @JsonIgnore
    
    public boolean isSearchNotEmpty() {
        return !(search == null || search.isEmpty());
    }

    @JsonIgnore
    
    public String getLikeSearchKey() {
        return " ilike '%".concat(this.getSearch().toLowerCase()).concat("%'\n");
    }

    @JsonIgnore
    
    public String getSearchForLookup() {
        return StringUtils.isNotEmpty(search) ? search.toLowerCase() + "%" : search;
    }

    @JsonIgnore
    
    /*public String getSearchForLookupTransliterated() {
        String id = "Russian-Latin/BGN;";
        if (StringUtils.isNotEmpty(search)) {
            //check if text is already latin - exception ё is still ё
            search = search.toLowerCase().contains("ё") ? search.toLowerCase().replace("ё", "_") : search;
            search = search.toLowerCase().contains("щ") ? search.toLowerCase().replace("щ", "%") : search; //think of proper way to replace this part
            if (Transliterator.getInstance(id).transform(search.toLowerCase()).equalsIgnoreCase(search.toLowerCase())) {
                //if latin reverse and return cyrillic version
                return Transliterator.getInstance(id).getInverse().transform(search.toLowerCase()) + "%";
            }
            //if not return latin version
            return Transliterator.getInstance(id).transform(search.toLowerCase()) + "%";
        }
        return search;
    }*/

    public String getSearch() {
        return search != null ? search.toLowerCase() : null;
    }

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = super.getMultiValueMap();
        if (StringUtils.isNotEmpty(search)) {
            map.put("search", List.of(search));
        }
        return map;
    }
}
