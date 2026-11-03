package uz.uzkassa.smartpossupply.dto.aslbelgi.aic;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * <p>
 * POST <url стенда>/documents/aic/enrichment
 */
@ToString
@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AicRequest implements Serializable {

    /**
     * ИНН УОТ-импортера или ПИНФЛ
     * импортера
     * "inn": "810716409"
     */
    @NotNull String inn;

    /**
     * ТНВЭД импортируемого товара
     * (потребительской упаковки)
     * "tnved": "1234567899",
     */
    @NotNull String tnved;

    /**
     * GTIN импортируемого товара
     * (потребительской упаковки)
     * "gtin": "09909872347761",
     */
    @NotNull String gtin;

    /**
     * Наименование продукта
     */
    String productName;

    /**
     * Товарная группа, к которой относится
     * маркируемый товар
     * "pg": "tobacco",
     */
    @NotNull String pg;

    /**
     * Количество импортируемого товара
     * (потребительских упаковок)
     * "cisesCount": 30
     */
    @NotNull int cisesCount;

    /**
     * Коды идентификации (КИ/КИГУ/
     * КИТУ) товаров, вложенных в АИК
     * "cises":
     * ["010460000000000021%OXp)?I",
     * "010460000000000021%jhu78y",
     * "010460000000000021iI987&_" ]
     */
    @NotNull Set<String> cises;

    String tnvedAddUnits;

    BigDecimal tnvedAddUnitsCount;
}
