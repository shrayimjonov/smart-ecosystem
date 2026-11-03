package uz.uzkassa.smartpossupply.filters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpeditorFilter extends BaseFilter {

    List<Long> branchIds;

    List<Long> planRouteIds;

    List<Long> expeditorIds;
}
