package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum UserRole {
    USER("Пользователь", "ROLE_USER"),
    SUPERVISOR("Руководитель", "ROLE_SUPERVISOR"),
    SUPERADMIN("Супер Админ", "ROLE_SUPERADMIN"),//ROLE_ADMIN
    ADMINISTRATOR("Админ", "ROLE_ADMINISTRATOR"),
    //    DEVICE("Шкаф", "ROLE_DEVICE"),
    CABINET_ADMIN("Администратор", "ROLE_CABINET_ADMIN"),///AGENCY_ADMIN
    DIRECTOR("Директор", "ROLE_DIRECTOR"),
    ACCOUNTANT("Бухгалтер", "ROLE_ACCOUNTANT"),
    WORKER_WAREHOUSE("Сотрудник склада", "ROLE_WORKER_WAREHOUSE"),
    //    WORKER_WITHOUT_VALIDATE("Сотрудник склада", "WORKER_WITHOUT_VALIDATE"),
    MANAGER_WAREHOUSE("Заведующий склада", "ROLE_MANAGER_WAREHOUSE"),
    MANAGER("Менеджер", "ROLE_MANAGER"),
    DRIVER("Водитель", "ROLE_DRIVER"),
    OPERATOR("Оператор", "ROLE_OPERATOR"),
    LINE("Оператор", "ROLE_LINE")
    ;

    private final String text;
    private final String code;

    public static List<SelectItem> getCabinetRoles() {
        return Arrays.asList(
            UserRole.CABINET_ADMIN.toSelectItem(),
            UserRole.DIRECTOR.toSelectItem(),
            UserRole.ACCOUNTANT.toSelectItem(),
            UserRole.WORKER_WAREHOUSE.toSelectItem(),
            UserRole.OPERATOR.toSelectItem(),
            UserRole.DRIVER.toSelectItem(),
            UserRole.MANAGER_WAREHOUSE.toSelectItem()
        );
    }

    public static List<SelectItem> getAdminRoles() {
        return Arrays.asList(
            UserRole.SUPERVISOR.toSelectItem(),
            UserRole.DIRECTOR.toSelectItem(),
            UserRole.ACCOUNTANT.toSelectItem(),
            UserRole.MANAGER_WAREHOUSE.toSelectItem(),
            UserRole.WORKER_WAREHOUSE.toSelectItem(),
            UserRole.OPERATOR.toSelectItem(),
            UserRole.CABINET_ADMIN.toSelectItem(),
            UserRole.DRIVER.toSelectItem(),
            UserRole.MANAGER.toSelectItem()
        );
    }

    public static List<SelectItem> getEmployeeRoles() {
        return Arrays.asList(
            UserRole.ADMINISTRATOR.toSelectItem(),
            UserRole.SUPERVISOR.toSelectItem(),
            UserRole.MANAGER.toSelectItem()
        );
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getText());
    }

    public static UserRole fromString(String code) {
        if (code != null) {
            for (UserRole item : UserRole.values()) {
                if (code.equals(item.name()) || code.equalsIgnoreCase(item.text) || code.equalsIgnoreCase(item.code)) {
                    return item;
                }
            }
        }
        return null;
    }

    @JsonCreator
    public static UserRole forValue(String value) {
        return fromString(value);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }

    @Override
    public String toString() {
        return this.name();
    }
}
