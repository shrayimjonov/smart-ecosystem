package uz.uzkassa.smartpossupply.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaxCompanyBranchDTO implements Serializable {

    String tin;

    Long ns10Code;

    Long ns11Code;

    String name;

    String branchName;

    String branchNum;

    Integer isDeleted;

    String longitude;

    String latitude;

    public String getName() {
        return StringUtils.isNotEmpty(this.branchName) ? branchName : name;
    }
}
