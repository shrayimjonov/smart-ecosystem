package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCategoryDTO implements Serializable {

    List<Long> productIds;

    Long categoryId;
}
