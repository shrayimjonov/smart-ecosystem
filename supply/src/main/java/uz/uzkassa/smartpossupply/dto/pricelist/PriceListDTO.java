package uz.uzkassa.smartpossupply.dto.pricelist;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.SaleType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.02.2023 13:54
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceListDTO {

    @NotNull
    String name;

    String description;

    @NotNull
    Long branchId;

    LocalDate expireDate;

    SaleType saleType;

    @NotEmpty
    List<PriceListItemDTO> items = new ArrayList<>();

    Set<Long> customers = new HashSet<>();

    List<Long> customerGroupIds;

    boolean applyToAllCustomers;
}
