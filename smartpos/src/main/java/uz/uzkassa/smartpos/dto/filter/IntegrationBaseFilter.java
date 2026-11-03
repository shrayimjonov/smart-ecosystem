package uz.uzkassa.smartpos.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.smartpos.enums.SortTypeEnum;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntegrationBaseFilter implements Serializable {
    Integer page = 0;
    Integer size = 20;
    String search = "";
    String orderBy = "id";
    SortTypeEnum sortOrder = SortTypeEnum.desc;

    public String getSearch() {
        return this.search = this.search != null ? this.search.trim().replaceAll("[.!?,'{}]", "") : null;
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

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = new LinkedMultiValueMap<String, String>();
        if (search != null) {
            map.put("search", List.of(search));
        }
        if (size != null) {
            map.put("size", List.of(String.valueOf(size)));
        }
        if (page != null) {
            map.put("page", List.of(String.valueOf(page)));
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
