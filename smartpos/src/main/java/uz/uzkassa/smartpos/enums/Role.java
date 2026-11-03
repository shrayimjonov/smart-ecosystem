package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;

import java.util.Arrays;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum Role {
    ADMIN(1, "Супер Админ", AuthoritiesConstants.ADMIN, "Super Foydalanuvchi", "Супер Фойдаланувчи"),//WARNING Do not use for API
    SMARTPOS_ADMIN(2, "Смартпос Администратор", AuthoritiesConstants.SMARTPOS_ADMIN, "Smartpos Admin", "Смартпос Админ"),//WARNING Do not use for API
    OWNER(3, "Владелец", AuthoritiesConstants.OWNER, "Egasi", "Эгаси"),
    BRANCH_ADMIN(4, "Администратор филиала", AuthoritiesConstants.BRANCH_ADMIN, "Filial admin", "Филиал админи"),
    CASHIER(5, "Кассир", AuthoritiesConstants.CASHIER, "Kassir", "Кассир"),
    SALES_MANAGER(6, "Менеджер по продажам", AuthoritiesConstants.SALES_MANAGER, "Sotish bo'yicha menejer", "Сотиш бўйича менежер"),
    SUPPORT_MANAGER(11, "Менеджер по поддержке", AuthoritiesConstants.SUPPORT_MANAGER, "Qo'llab-quvvatlash menejeri", "Қўллаб-қувватлаш менежери"),
    REGION_MANAGER(12, "Региональный менеджер", AuthoritiesConstants.REGION_MANAGER, "Mintaqaviy menejer", "Минтақавий менежер"),
    SALES_DIRECTOR(9, "Руководитель отдела продаж", AuthoritiesConstants.SALES_DIRECTOR, "Savdo bo'limi boshlig'i", "Савдо бўлими бошлиғи"),
    SUPPORT(10, "Поддержка", AuthoritiesConstants.SUPPORT, "Qo'llab-quvvatlovchi", "Қўллаб-қувватловчи"),
    USER(13, "Пользователь", AuthoritiesConstants.USER, "Foydalanuvchi", "Фойдаланувчи");

    private final Integer priority;
    private final String nameRu;
    private final String code;
    private final String roleCode;
    private final String nameUz;
    private final String nameCyril;

    Role(Integer priority, String nameRu, String roleCode, String nameUz, String nameCyril) {
        this.priority = priority;
        this.nameRu = nameRu;
        this.roleCode = roleCode;
        this.code = name();
        this.nameUz = nameUz;
        this.nameCyril = nameCyril;
    }

    @JsonCreator
    public static Role forValue(Map<String, String> value) {
        return Role.valueOf(value.get("code"));
    }

    public static Role fromRoleCode(String roleCode) {
        return Arrays.stream(Role.values()).filter(role -> role.getRoleCode().equals(roleCode)).findAny().orElse(null);
    }

}
