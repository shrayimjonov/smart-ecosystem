package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserFilter extends BaseFilter {

    String role;

    List<String> roles = new ArrayList<>();

    Boolean validatePinfl;

    Long supervisorId;

    Long managerId;

    Boolean onlyHasSupervisor;
}
