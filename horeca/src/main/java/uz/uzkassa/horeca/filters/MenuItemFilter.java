package uz.uzkassa.horeca.filters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuItemFilter extends BaseFilter {

    Long menuId;

    List<Long> menuIdList;

    List<Long> departmentIdList;

    boolean onlyParent;
}
