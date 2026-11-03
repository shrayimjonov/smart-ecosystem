package uz.uzkassa.horeca.dto.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uz.uzkassa.horeca.enums.billing.SortTypeEnum;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntegrationBaseFilter { Integer page = 0;

    Integer size = 20;

    String search = "";

    String orderBy = "id";

    SortTypeEnum sortOrder = SortTypeEnum.desc;

    public String getSearch() {
        return this.search = this.search != null ? this.search.trim().replaceAll("[.!?,'{}]", "") : null;
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
    
    public Pageable getPageable() {
        return PageRequest.of(this.getPage(), this.getSize());
    }

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
    
    public String getSearchForQuery() {
        return StringUtils.isNotEmpty(search) ? "%" + search.toLowerCase() + "%" : search;
    }

    @JsonIgnore
    
    public Sort getOrderedSortBy() {
        return sortOrder == SortTypeEnum.asc ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
    }

    @JsonIgnore
    
    public Pageable getOrderedPageable() {
        return PageRequest.of(page, size, getOrderedSortBy());
    }
}
