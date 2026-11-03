package uz.uzkassa.smartpossupply.dto.pricelist;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.SaleType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceListListOfflineDTO extends CommonDTO {

    CommonDTO branch;

    LocalDate expireDate;

    SaleType saleType;

    List<PriceListItemDTO> items = new ArrayList<>();

    Set<Long> customers = new HashSet<>();

    boolean applyToAllCustomers;
}
