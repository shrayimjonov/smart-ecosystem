package uz.uzkassa.smartpossupply.dto.discount;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.12.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountDetailDTO extends CommonDTO {

    String description;

    LocalDateTime validFrom;

    LocalDateTime validTo;

    CommonDTO branch;

    CommonStatus status;

    CommonDTO creator;

    LocalDateTime createdDate;

    List<DiscountItemDetailDTO> items = new ArrayList<>();

    public void add(DiscountItemDetailDTO item) {
        items.add(item);
    }
}