package uz.uzkassa.smartpos.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.smartpos.dto.integration.enums.OrganizationApplicationFormStatus;
import uz.uzkassa.smartpos.enums.SortTypeEnum;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class OrganizationApplicationFormFilter extends PageFilter {
    private String search;
    private LocalDate from;
    private LocalDate to;
    private OrganizationApplicationFormStatus status;
    private Long regionId;
    private String tin;
    private String phone;
    private String name;
    private String orderBy = "createdDate";
    private Boolean archive;

    SortTypeEnum sortOrder = SortTypeEnum.desc;

    @JsonIgnore
    public Sort getOrderedSortBy() {
        return sortOrder.equals(SortTypeEnum.asc) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
    }

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = super.getMultiValueMap();
        if (search != null) {
            map.put("search", List.of(search));
        }
        if (from != null) {
            map.put("from", List.of(String.valueOf(from)));
        }
        if (to != null) {
            map.put("to", List.of(String.valueOf(to)));
        }
        if (regionId != null) {
            map.put("regionId", List.of(String.valueOf(regionId)));
        }
        if (tin != null) {
            map.put("tin", List.of(tin));
        }
        if (status != null) {
            map.put("status", List.of(status.name()));
        }
        if (phone != null) {
            map.put("phone", List.of(phone));
        }
        if (name != null) {
            map.put("name", List.of(name));
        }
        if (orderBy != null) {
            map.put("orderBy", List.of(orderBy));
        }
        if (sortOrder != null) {
            map.put("sortOrder", List.of(sortOrder.name()));
        }
        if (archive != null) {
            map.put("archive", List.of(String.valueOf(archive)));
        }
        return map;
    }
}
