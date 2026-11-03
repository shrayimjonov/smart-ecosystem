package uz.uzkassa.smartpossupply.dto.discount;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.12.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountDTO implements Serializable {

    Long id;

    @NotNull
    String name;

    LocalDateTime validFrom;

    @NotNull
    LocalDateTime validTo;

    @NotNull
    Long branchId;

    @NotEmpty
    List<DiscountItemDTO> items;
}