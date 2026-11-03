package uz.uzkassa.horeca.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum SmartposReceiptStatus {
    DRAFT("Черновик"),
    PAID("Оплачен"),
    RETURNED("Возврат"),
    //Terminal operation statuses
    INCOME("Внесение наличности"),
    WITHDRAW("Возврат внесение"),
    FLOW("Расход"),
    RETURN_FLOW("Возврат расхода"),
    INCASSATION("Инкассация"),
    //Additional Operations
    ADVANCE("Аванс"),
    CREDIT("Кредит"),

    UNKNOWN("Тестовый");

    private final String nameRu;
    private final String code;

    SmartposReceiptStatus(String nameRu) {
        this.nameRu = nameRu;
        this.code = name();
    }

    public static SmartposReceiptStatus get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return Arrays.stream(values())
                .filter(status -> status.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown SmartposReceiptStatus enum value: " + code));
    }

    @JsonCreator
    public static SmartposReceiptStatus fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for SmartposReceiptStatus");
    }

    @Override
    public String toString() {
        return code;
    }

    public boolean isFiscalizable(final boolean fromInvoiceOrVoucher) {
        return RETURNED == this || (EnumSet.of(PAID, CREDIT, ADVANCE).contains(this) && fromInvoiceOrVoucher);
    }

    public boolean isFiscalized() {
        return EnumSet.of(PAID, RETURNED, CREDIT, ADVANCE).contains(this);
    }

    public int getPaymentType() {
        switch (this) {
            case ADVANCE:
                return 1;
            case CREDIT:
                return 2;
            default:
                return 0;
        }
    }
}
