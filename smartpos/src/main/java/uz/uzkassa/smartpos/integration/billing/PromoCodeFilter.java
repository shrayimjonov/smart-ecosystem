package uz.uzkassa.smartpos.integration.billing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.util.MultiValueMap;
import uz.uzkassa.smartpos.dto.filter.IntegrationBaseFilter;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.integration.enums.PromocodeType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PromoCodeFilter extends IntegrationBaseFilter {

    private String promotionId;

    private String code;

    private String tag;

    private PromocodeType promocodeType;

    private LocalDate from;

    private LocalDate to;

    private String orderBy = "createdDate";

    SortTypeEnum sortOrder = SortTypeEnum.desc;

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = super.getMultiValueMap();
        if (promotionId != null) {
            map.put("promotionId", List.of(promotionId));
        }
        if (code != null) {
            map.put("code", List.of(code));
        }
        if (tag != null) {
            map.put("tag", List.of(tag));
        }
        if (promocodeType != null) {
            map.put("promocodeType", List.of(promocodeType.getCode()));
        }
        if (from != null) {
            map.put("from", List.of(String.valueOf(from)));
        }
        if (to != null) {
            map.put("to", List.of(String.valueOf(to)));
        }

        return map;
    }
}
