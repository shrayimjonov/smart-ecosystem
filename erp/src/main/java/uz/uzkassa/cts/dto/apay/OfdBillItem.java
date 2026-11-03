package uz.uzkassa.cts.dto.apay;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class OfdBillItem implements Serializable {
    String name; //* Название товара/услуги с ед.изм в конце
    String barcode; //Штрихкод
    String label; //Код маркировки
    Collection<String> labels; //Коды маркировки
    String spic; //* Код ИКПУ
    int units = 1; //* Код ед.изм
    String packageCode; //* Код упаковки
    int goodPrice; //* Цена одного товара/услуги
    long price; //* Общая сумма позиции с учётом количества и без учёта скидок
    long vat; //* НДС сумма
    int vatPercent; //* НДС %
    int amount = 1000; //* Количество
    int discount; //Скидка
    int other; //Прочая скидка (Оплата по страховки и др.)
    OfdCommitmentDTO commissionInfo; //Информация о комиссионном чеке
}
