package uz.uzkassa.smartpossupply.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClickhouseFilter extends BaseFilter {

    String transferType;

    String entityType;

    Long entityId;
    
    @JsonIgnore
    public String getSearchValueClickhouse() {
        return " like('%".concat(this.search.toLowerCase().replaceAll("'", "\\\\'")).concat("%')");
    }
}
