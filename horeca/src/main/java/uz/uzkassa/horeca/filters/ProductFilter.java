package uz.uzkassa.horeca.filters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductFilter extends BaseFilter {

    String categoryType;

    List<String> categoryTypes;

    Boolean menuIncluded;
}
