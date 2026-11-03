package uz.uzkassa.cts.dto.caseTypeAccess;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang.StringUtils;
import uz.uzkassa.cts.enums.CaseType;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CaseTypeAccessListDTO implements Serializable {

    Long userId;
    String fullName;
    @JsonIgnore
    String caseTypes;

    public List<CaseType> getCaseTypeEnums() {
        return StringUtils.isNotEmpty(this.caseTypes) ? Pattern.compile(";")
                .splitAsStream(this.caseTypes)
                .map(CaseType::valueOf)
                .collect(Collectors.toList()) : null;
    }
}
