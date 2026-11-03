package uz.uzkassa.smartpossupply.dto.category;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategorySortDTO implements Serializable {

    List<CategorySortItemDTO> items;
}
