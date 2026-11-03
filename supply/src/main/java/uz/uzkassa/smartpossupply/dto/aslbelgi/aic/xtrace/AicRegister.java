package uz.uzkassa.smartpossupply.dto.aslbelgi.aic.xtrace;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class AicRegister implements Serializable {

    /**
     * Агрегированный импортный код
     */
    @NotNull
    String aggregatedCustomsCode;

    /**
     * Код товара (GTIN)
     */
    @NotNull
    String gtin;

    /**
     * Идентификатор карточки товара
     */
    String productId;

    /**
     * Товарная группа
     */
    @NotNull
    String productGroup;

    /**
     * Код ТНВЭД
     */
    @NotNull
    String tnved;

    /**
     * Количество товара в потребительских упаковках
     */
    @NotNull
    Integer quantity;

    /**
     * Дополнительная единица измерения
     */
    String additionalUnit;

    /**
     * Количество товара в дополнительно единице измерения
     */
    BigDecimal additionalUnitQuantity;

    /**
     * Список КМ
     */
    @NotNull
    Set<String> codes;
}
