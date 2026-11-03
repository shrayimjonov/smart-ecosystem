package uz.uzkassa.smartpos.dto.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashboxProductFilter extends PrimaryFilter {
    private Long categoryId;
    private boolean onlyFavorites;
    private boolean creditEnabled;
    private boolean advanceEnabled;
    private Long branchId;
    private Long cityId;
    private boolean available;
}
