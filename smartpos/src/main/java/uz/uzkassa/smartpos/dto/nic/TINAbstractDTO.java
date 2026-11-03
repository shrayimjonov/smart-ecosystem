package uz.uzkassa.smartpos.dto.nic;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Setter
public class TINAbstractDTO implements Serializable {
    private Long ns10Code;
    private Long ns11Code;
    private String tin;
    private String name;
    private String shortName;
    private String fullName;
    private String mfo;
    private String account;
    private String oked;
    private String director;
    private String directorTin;
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

    public String getTin() {
        return personalNum != null ? personalNum : tin;
    }

    public String getName() {
        return StringUtils.firstNonBlank(this.name, this.shortName, this.fullName);
    }
}
