package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@AllArgsConstructor
public enum FieldValidation {
    REQUIRED_FIELD_ERROR("Обязательное поле", "Majburiy maydon", "Required field"),
    NOT_VALID_FORMAT_ERROR("Не корректный формат поля", "Noto'g'ri maydon formati", "Incorrect field format"),
    MXIK_ERROR("ИКПУ неверна", "MXIK xato ", "MXIK is wrong"),
    UNIT_ERROR("Единица измерения неверна", "O'lchov birligi xato", "The unit of measurement is wrong"),
    CATEGORY_NOT_FOUND_ERROR("Категория товара не была найдена", "Mahsulot toifasi topilmadi", "The product category was not found"),
    VALID("Продукт валиден", "Mahsulot haqiqiydir", "The product is valid"),
    PRODUCT_DUPLICATE("В вашем каталоге есть несколько товаров с таким названием. Исправьте название продукта","Katalogingizda bunday nomli tovar bir nechta. Tovar nomini to'g'rilang", "There are several products with this name in your catalog. Correct the product name"),
    PRODUCT_NOT_FOUND("Товар не найден с таким названием","Bunday nomli tovar topilmadi", "Product not found with this name");

    private final String nameRu;
    private final String nameUz;
    private final String nameEn;

}
