package uz.uzkassa.cts.filter.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.cts.enums.SortType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LedgerDetailAppTypeFilter implements Serializable {

    private LocalDate from;
    private LocalDate to;
    private String search = "";
    private List<String> tins;
    private String appType;

    public String getSearch() {
        return this.search = this.search != null ? this.search.trim().replaceAll("[.!?,',{,}]", "") : null;
    }

    @JsonIgnore
    public String getLikeSearchKey() {
        return " ilike '%".concat(this.getSearch().toLowerCase()).concat("%'\n");
    }

    @JsonIgnore
    public boolean isSearchNotEmpty() {
        return !(search == null || search.isEmpty());
    }

    @JsonIgnore
    public String getSearchForQuery() {
        return StringUtils.isNotEmpty(search) ? "%" + search.toLowerCase() + "%" : search;
    }

    @Min(value = 0, message = "Page number cannot be smaller than 0")
    @ToString.Include
    private Integer page = 0;

    @Min(value = 0, message = "Page size cannot be smaller than 0")
    @ToString.Include
    private Integer size = 200;

    @ToString.Include
    private String orderBy = "credit";

    @ToString.Include
    private SortType sortOrder = SortType.desc;

    @JsonIgnore
    public int getStart() {
        return this.getPage() * this.getSize();
    }

    public int getPage() {
        return Math.abs(this.page);
    }

    public int getSize() {
        return Math.abs(this.size);
    }

    @JsonIgnore
    public Sort getOrderedSortBy() {
        return sortOrder.equals(SortType.asc) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
    }

    @JsonIgnore
    public Pageable getPageable() {
        return PageRequest.of(this.getPage(), this.getSize());
    }

    @JsonIgnore
    public Pageable getSortedPageable() {
        return PageRequest.of(this.getPage(), this.getSize(), getOrderedSortBy());
    }

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = new LinkedMultiValueMap<String, String>();
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

