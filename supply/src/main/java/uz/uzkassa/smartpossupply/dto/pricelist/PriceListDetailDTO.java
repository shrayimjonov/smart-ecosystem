package uz.uzkassa.smartpossupply.dto.pricelist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.SaleType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.02.2023 14:23
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceListDetailDTO extends CommonDTO  {

    String priceListNumber;

    String description;

    CommonDTO branch;

    CommonStatus status;

    LocalDate expireDate;

    SaleType saleType;

    List<PriceListItemDetailDTO> items = new ArrayList<>();

    Set<PriceListCustomerDTO> customers = new HashSet<>();

    boolean applyToAllCustomers;

    List<CommonDTO> customerGroups;

    @JsonIgnore
    public void addItem(PriceListItemDetailDTO priceListItemDetailDTO) {
        items.add(priceListItemDetailDTO);
    }

    @JsonIgnore
    public void addCustomer(PriceListCustomerDTO customer) {
        customers.add(customer);
    }
}
