package uz.uzkassa.silen.integration.billing.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uz.uzkassa.silen.enumeration.SortTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntegrationBaseFilter  implements Serializable {
    Integer page = 0;

    Integer size = 20;

    String search = "";

    String orderBy = "id";

    SortTypeEnum sortOrder = SortTypeEnum.desc;

    private LocalDateTime from;

    private LocalDateTime to;

    public String getSearch() {
        return this.search = this.search != null ? this.search.trim().replaceAll("[.!?,'{}]", "") : null;
    }

    @JsonIgnore
    @Schema(hidden = true)
    public String getLikeSearchKey() {
        return " ilike '%".concat(this.getSearch().toLowerCase()).concat("%'\n");
    }

    @JsonIgnore
    @Schema(hidden = true)
    public boolean isSearchNotEmpty() {
        return !(search == null || search.length() == 0);
    }

    @JsonIgnore
    @Schema(hidden = true)
    public Pageable getPageable() {
        return PageRequest.of(this.getPage(), this.getSize());
    }

    @JsonIgnore
    @Schema(hidden = true)
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
    @Schema(hidden = true)
    public String getSearchForQuery() {
        return StringUtils.isNotEmpty(search) ? "%" + search.toLowerCase() + "%" : search;
    }

    @JsonIgnore
    @Schema(hidden = true)
    public Sort getOrderedSortBy() {
        return sortOrder.equals(SortTypeEnum.asc) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
    }

    @JsonIgnore
    @Schema(hidden = true)
    public Pageable getOrderedPageable() {
        return PageRequest.of(page, size, getOrderedSortBy());
    }

    public List<NameValuePair> convertToParams() {
        List<NameValuePair> params = new ArrayList<>();
        if (getPage() >= 0)
            params.add(new BasicNameValuePair("page", String.valueOf(this.getPage())));
        if (getSize() > 0)
            params.add(new BasicNameValuePair("size", String.valueOf(this.getSize())));
        if (StringUtils.isNotEmpty(this.getSearch()))
            params.add(new BasicNameValuePair("search", this.getSearch()));
        if (StringUtils.isNotEmpty(this.getOrderBy()))
            params.add(new BasicNameValuePair("orderBy", this.getOrderBy()));
        if (getFrom() != null)
            params.add(new BasicNameValuePair("from", this.getFrom().toString()));
        if (getTo() != null)
            params.add(new BasicNameValuePair("to", this.getTo().toString()));
        return params;
    }
}
