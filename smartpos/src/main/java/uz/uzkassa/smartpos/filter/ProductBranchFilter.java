package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Setter
public class ProductBranchFilter {
    private Long branchId;
    private Long cityId;
    private Long categoryId;
    private List<Long> categoryIds;
    private boolean creditEnabled;
    private boolean advanceEnabled;
    private boolean onlyFavorites;
    private String search;
    private List<String> attributes;
    private Integer page = 0;
    private Integer size = 20;

    public Pageable getPageable() {
        return PageRequest.of(page, size);
    }

    public String getSearchKey() {
        return "%" + search.toLowerCase() + "%";
    }
}
