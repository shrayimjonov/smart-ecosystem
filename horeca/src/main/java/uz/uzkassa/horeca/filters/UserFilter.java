package uz.uzkassa.horeca.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.AgeGroup;
import uz.uzkassa.horeca.enums.Gender;
import uz.uzkassa.horeca.enums.Role;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFilter extends BaseFilter {

    String role;

    @JsonIgnore
    List<Role> roles = new ArrayList<>();

    List<Long> branchIds;

    List<Long> departmentIds;

    boolean customer;

    Gender gender;

    AgeGroup ageGroup;
}
