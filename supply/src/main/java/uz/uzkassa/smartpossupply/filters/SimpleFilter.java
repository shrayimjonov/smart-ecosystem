package uz.uzkassa.smartpossupply.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.enums.SortTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class SimpleFilter implements Serializable {

    Integer page = 0;

    Integer size = 20;

    String search = "";

    String orderBy = "id";

    SortTypeEnum sortOrder = SortTypeEnum.desc;

    Long companyId;

    Long branchId;

    LocalDateTime from;

    LocalDateTime to;

    public LocalDateTime getFrom() {
        return from != null ? from.with(LocalTime.MIN) : null;
    }

    public LocalDateTime getTo() {
        return to != null ? to.with(LocalTime.MAX) : null;
    }

    public String getSearch() {
        return this.search = this.search != null ? this.search.trim().replaceAll("[.!?,',{,},+]", "") : null;
    }

    @JsonIgnore
    public boolean isNotEmpty() {
        return StringUtils.hasText(this.getSearch());
    }

    @JsonIgnore
    
    public int getStart() {
        return this.getPage() * this.getSize();
    }

    @JsonIgnore
    
    public Pageable getPageable() {
        return PageRequest.of(this.getPage(), this.getSize());
    }

    
    @JsonIgnore
    public String getSearchKey() {
        return " like (:searchKey)";
    }

    @JsonIgnore
    
    public String getSearchValue() {
        return "%".concat(this.getSearch().toLowerCase()).concat("%");
    }

    @JsonIgnore
    
    public Sort getOrderedSortBy() {
        return sortOrder.equals(SortTypeEnum.asc) ? Sort.by(getDefaultOrderBy()).ascending() : Sort.by(getDefaultOrderBy()).descending();
    }

    @JsonIgnore
    
    public Pageable getSortedPageable() {
        return PageRequest.of(page, size, getOrderedSortBy());
    }

    @JsonIgnore
    
    public String getDefaultOrderBy() {
        return "id";
    }

    @JsonIgnore
    
    public SortTypeEnum getSortOrder() {
        return this.sortOrder != null ? this.sortOrder : SortTypeEnum.desc;
    }
}
