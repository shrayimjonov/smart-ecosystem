package uz.uzkassa.horeca.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class SoliqCompanyDTO implements Serializable {

    Long ns10Code;
    Long ns11Code;
    Long na1Code;
    String na1Name;
    String tin;
    String name;
    String shortName;
    String fullName;
    String mfo;
    String account;
    String oked;
    String director;
    String directorTin;
    String branchName;
    String branchNum;
    String accountant;
    String address;
    String mobile;
    String workPhone;
    String passSeries;
    String passNumber;
    String passOrg;
    String passIssueDate;
    Integer statusCode;
    String statusName;
    String personalNum;
    String firstName;
    String lastName;
    String patronymic;

    public String getName() {
        StringBuilder name = new StringBuilder();
        if (this.name != null) {
            name.append(this.name);
        } else if (this.shortName != null) {
            name.append(this.shortName);
        } else if (this.fullName != null) {
            name.append(this.fullName);
        }
        return name.toString();
    }
}
