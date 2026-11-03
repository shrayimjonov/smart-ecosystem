package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.WhiteListAction;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WhiteListFilter extends BaseFilter {

    WhiteListAction action;
}
