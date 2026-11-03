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
public class TaxCompanyDTO implements Serializable {

    Long ns10Code;

    Long ns11Code;

    String tin;

    String fullName;

    String name;

    String mfo;

    String account;

    String address;

    String oked;

    String director;

    String directorTin;

    String accountant;

    public String getName() {
        return StringUtils.isNotEmpty(name) ? name : fullName;
    }
}
