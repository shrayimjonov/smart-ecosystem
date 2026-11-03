package uz.uzkassa.cts.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.Arrays;
import java.util.List;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomerGroupStatus {
    ACTIVE(List.of(CustomerStatus.ACTIVE, CustomerStatus.IN_ACTIVE, CustomerStatus.PENDING, CustomerStatus.DRAFT, CustomerStatus.MISSING, CustomerStatus.DEBTOR)) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Faol";
                case uz_cyrillic -> "Фаол";
                default -> "Активный";
            };
        }
    },
    IN_ACTIVE(List.of(CustomerStatus.REFUSED, CustomerStatus.CLOSED, CustomerStatus.LIQUIDATED)) {
        @Override
        public String getName(Language language) {
            return switch (language) {
                case uz_latin -> "Faol emas";
                case uz_cyrillic -> "Фаол эмас";
                default -> "Неактивный";
            };
        }
    };

    private final String name;
    private final String code;
    private final List<CustomerStatus> statuses;

    CustomerGroupStatus(List<CustomerStatus> statuses) {
        this.code = this.name();
        this.name = this.getName();
        this.statuses = statuses;
    }

    public abstract String getName(Language language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static CustomerGroupStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown CustomerGroupStatus enum value: " + code));
    }

    @JsonCreator
    public static CustomerGroupStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for CustomerGroupStatus");
    }
}