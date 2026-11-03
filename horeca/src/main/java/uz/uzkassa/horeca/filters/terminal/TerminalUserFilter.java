package uz.uzkassa.horeca.filters.terminal;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.filters.BaseFilter;

import java.util.Set;

@Getter
@Setter
public class TerminalUserFilter extends BaseFilter {

    Set<String> roles;
    boolean dismissed;
    Set<Long> branchIds;
    Set<Long> branchGroupIds;
}
