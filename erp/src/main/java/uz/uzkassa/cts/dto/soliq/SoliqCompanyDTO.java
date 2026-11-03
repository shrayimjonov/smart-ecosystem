package uz.uzkassa.cts.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class SoliqCompanyDTO implements Serializable {

    private Long ns10Code;
    private Long ns11Code;
    private Long na1Code;
    private String na1Name;
    private String tin;
    private String name;
    private String shortName;
    private String fullName;
    private String mfo;
    private String account;
    private String oked;
    private String director;
    private String directorTin;
    private String directorPinfl;
    private String branchName;
    private String branchNum;
    private String accountant;
    private String address;
    private String mobile;
    private String workPhone;
    private String passSeries;
    private String passNumber;
    private String passOrg;
    private String passIssueDate;
    private Integer statusCode;
    private String statusName;
    private String personalNum;
    private String firstName;
    private String lastName;
    private String patronymic;

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
