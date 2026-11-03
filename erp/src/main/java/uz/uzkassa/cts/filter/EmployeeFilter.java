package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeFilter extends BaseFilter {

    String role;

    Set<String> roles;

    Boolean hasTimeTrackPhoto;

    Boolean hasLogin;
}
