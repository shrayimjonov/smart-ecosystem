package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ProductAddType {
    BY_CATEGORY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kategoriya bo‘yicha";
                case cyrillic -> "Категория бўйича";
                default -> "По категории";
            };
        }
    },

    BY_PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovar bo‘yicha";
                case cyrillic -> "Товар бўйича";
                default -> "По товарам";
            };
        }
    };

    private String name;
    private String code;

    ProductAddType() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static List<ProductAddType> getProductAddTypes() {
        return List.of(values());
    }
}
