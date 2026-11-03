package uz.uzkassa.cts.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uz.uzkassa.cts.enums.SortType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class SimpleFilter implements Serializable {

    Integer page = 0;

    Integer size = 20;

    String search = "";

    String orderBy = "id";

    SortType sortOrder = SortType.desc;

    public String getSearch() {
        return this.search = this.search != null ? this.search.trim().replaceAll("[!?,'{}`]", "").toLowerCase() : null;
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

    @JsonIgnore
    public String getSearchFor() {
        return StringUtils.join("%", this.getSearch(), "%");
    }

    @JsonIgnore
    public String getLikeSearch() {
        return " like (:searchKey)";
    }

    @JsonIgnore
    public Sort getOrderedSortBy() {
        return sortOrder.equals(SortType.asc) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
    }

    @JsonIgnore
    public Pageable getPageable() {
        return PageRequest.of(page, size);
    }

    @JsonIgnore
    public Pageable getOrderedPageable() {
        return PageRequest.of(page, size, getOrderedSortBy());
    }

    @JsonIgnore
    public int getStart() {
        return this.getPage() * this.getSize();
    }

    public String getOrderBy() {
        return StringUtils.isNotEmpty(orderBy) ? orderBy : "id";
    }
}
