package uz.uzkassa.cts.filter.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.cts.enums.SortType;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
public class PageFilter implements Serializable {
    @Min(value = 0, message = "Page number cannot be smaller than 0")
    @ToString.Include
    private Integer page = 0;
    @Min(value = 0, message = "Page size cannot be smaller than 0")
    @ToString.Include
    private Integer size = 200;

    @ToString.Include
    private String orderBy = "id";

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
        if (page != null) {
            map.put("page", List.of(String.valueOf(page)));
        }
        if (size != null) {
            map.put("size", List.of(String.valueOf(size)));
        }
        if (orderBy != null) {
            map.put("orderBy", List.of(orderBy));
        }
        if (sortOrder != null) {
            map.put("sortOrder", List.of(sortOrder.name()));
        }
        return map;
    }
}

