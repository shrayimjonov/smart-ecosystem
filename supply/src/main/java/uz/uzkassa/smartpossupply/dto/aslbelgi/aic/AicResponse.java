package uz.uzkassa.smartpossupply.dto.aslbelgi.aic;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * <p>
 * POST <url стенда>/documents/aic/enrichment response content
 * <p>
 * {
 * "aik": "1234567890123456789012345",
 * "inn": "810716409",
 * "productName": "Сигареты Camel",
 * "gtin": "09909872347761",
 * "pg": "3"
 * "tnved": "1234567899",
 * "cisesCount": 4500,
 * "cises":
 * ["010460000000000021%OXp)?I",
 * "010460000000000021%jhu78y",
 * "010460000000000021iI987&_" ]
 * }
 */
@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AicResponse implements Serializable {

    /**
     * Сгенерированный уникальный агрегированный импортный код
     */
    String aic;

    /**
     * ИНН УОТ-импортера или ПИНФЛ импортера
     * "inn": "810716409"
     */
    @NotNull
    String inn;

    /**
     * ТНВЭД импортируемого товара (потребительской упаковки)
     * "tnved": "1234567899",
     */
    @NotNull
    String tnved;

    /**
     * GTIN импортируемого товара (потребительской упаковки)
     * "gtin": "09909872347761",
     */
    @NotNull
    String gtin;

    /**
     * Наименование продукта, определяемое системой маркировки
     */
    String productName;

    /**
     * Товарная группа, к которой относится маркируемый товар
     * "pg": "tobacco",
     */
    @NotNull
    String pg;

    /**
     * Количество импортируемого товара (потребительских упаковок)
     * "cisesCount": 30
     */
    @NotNull
    int cisesCount;

    /**
     * Коды идентификации (КИ/КИГУ/КИТУ) товаров, вложенных в АИК
     * "cises":
     * ["010460000000000021%OXp)?I",
     * "010460000000000021%jhu78y",
     * "010460000000000021iI987&_" ]
     */
    @NotNull
    Set<String> cises;

    BigDecimal tnvedAddUnits;

    BigDecimal tnvedAddUnitsCount;
}
