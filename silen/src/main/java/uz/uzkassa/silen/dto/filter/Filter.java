package uz.uzkassa.silen.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uz.uzkassa.silen.enumeration.SortTypeEnum;
import uz.uzkassa.silen.security.SecurityUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Filter implements Serializable {

    private int page = 0;

    private int size = 50;

    private String search = "";

    private String orderBy;

    private SortTypeEnum sortOrder = SortTypeEnum.desc;

    private LocalDateTime from;

    private LocalDateTime to;

    private String branchId;

    private String organizationId;

    private String organizationTin;

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

    public String getOrganizationId() {
        return SecurityUtils.getCurrentOrganizationId() != null ? SecurityUtils.getCurrentOrganizationId() : this.organizationId;
    }

    public String getBranchId() {
        return this.branchId != null ? this.branchId : SecurityUtils.getCurrentBranchId();
    }

    public String getOrganizationTin() {
        return SecurityUtils.getCurrentOrganizationTin() != null ? SecurityUtils.getCurrentOrganizationTin() : this.organizationTin;
    }

    @JsonIgnore
    @Schema(hidden = true)
    public Pageable getPageable() {
        return PageRequest.of(this.getPage(), this.getSize());
    }

    @JsonIgnore
    @Schema(hidden = true)
    public String getSearchForQuery() {
        return search == null ? null : "%" + search.toLowerCase() + "%";
    }

    @JsonIgnore
    @Schema(hidden = true)
    public boolean checkSearchIsEmpty() {
        return StringUtils.isNotEmpty(getSearch());
    }
}
