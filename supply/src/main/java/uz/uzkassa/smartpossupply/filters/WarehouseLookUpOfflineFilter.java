package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class WarehouseLookUpOfflineFilter extends BaseFilter {

    Set<Long> branchIds;
}
