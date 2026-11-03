package uz.uzkassa.smartpossupply.dto.aslbelgi.aic;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AicDocument implements Serializable {

    /**
     * ИНН УОТ-импортера или ПИНФЛ
     * импортера
     * "inn": "810716409"
     */
    String inn;

    /**
     * ТНВЭД импортируемого товара
     * (потребительской упаковки)
     * "tnved": "1234567899",
     */
    String tnved;

    /**
     * GTIN импортируемого товара
     * (потребительской упаковки)
     * "gtin": "09909872347761",
     */
    String gtin;

    /**
     * Наименование продукта
     */
    String productName;

    /**
     * Товарная группа, к которой относится
     * маркируемый товар
     * "pg": "tobacco",
     */
    String pg;

    /**
     * Количество импортируемого товара
     * (потребительских упаковок)
     * "cisesCount": 30
     */
    int cisesCount;

    /**
     * Коды идентификации (КИ/КИГУ/
     * КИТУ) товаров, вложенных в АИК
     * "cises":
     * ["010460000000000021%OXp)?I",
     * "010460000000000021%jhu78y",
     * "010460000000000021iI987&_" ]
     */
    Set<String> cises;
}
