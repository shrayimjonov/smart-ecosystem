package uz.uzkassa.cts.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClickhouseFilter extends BaseFilter {

    Long entityId;

    List<Long> entityList;

    EntityTypeEnum entityType;

    @JsonIgnore
    public String getSearchValueClickhouse() {
        return " like('%".concat(this.search.toLowerCase().replaceAll("'", "\\\\'")).concat("%')");
    }

    public String getLimit() {
        return getStart() + ", " + getSize();
    }
}
