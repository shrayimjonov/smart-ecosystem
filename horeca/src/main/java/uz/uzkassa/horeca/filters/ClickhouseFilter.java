package uz.uzkassa.horeca.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClickhouseFilter extends BaseFilter {

    Long entityId;

    String entityType;

    @JsonIgnore
    public String getSearchValueClickhouse() {
        return " like('%".concat(this.search.toLowerCase()).concat("%')");
    }
}
