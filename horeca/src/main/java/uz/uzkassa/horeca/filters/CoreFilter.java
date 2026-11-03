package uz.uzkassa.horeca.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uz.uzkassa.horeca.enums.SortTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class CoreFilter implements Serializable {

    Integer page = 0;

    Integer size = 20;

    String search = "";

    String orderBy = "id";

    SortTypeEnum sortOrder = SortTypeEnum.desc;

    LocalDateTime from;

    LocalDateTime to;

    public LocalDateTime getFrom() {
        return from != null ? from.with(LocalTime.MIN) : null;
    }

    public LocalDateTime getTo() {
        return to != null ? to.with(LocalTime.MAX) : null;
    }

    public String getSearch() {
        return this.search = this.search != null ? this.search.trim().replaceAll("[.!?,',{,}]", "").toLowerCase() : null;
    }

    @JsonIgnore
    public boolean hasSearch() {
        return !(search == null || search.length() == 0);
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
    public String getSearchForQuery() {
        return "%".concat(this.getSearch().toLowerCase()).concat("%");
    }

    @JsonIgnore
    public String getLikeSearchKey() {
        return " like (:searchKey)";
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
