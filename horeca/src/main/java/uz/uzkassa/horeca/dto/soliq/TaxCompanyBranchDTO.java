package uz.uzkassa.horeca.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaxCompanyBranchDTO implements Serializable {

    String tin;

    String name;

    String branchName;

    String branchNum;

    Integer isDeleted;

    public String getName() {
        return StringUtils.isNotEmpty(this.branchName) ? branchName : name;
    }
}
