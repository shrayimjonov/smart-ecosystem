package uz.uzkassa.horeca.enums.terminal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
@Getter
public enum TerminalRole {
    OWNER(3, "ROLE_OWNER", "Владелец", "Egasi", "Эгаси"),
    BRANCH_ADMIN(4, "ROLE_BRANCH_ADMIN", "Администратор филиала", "Filial admin", "Филиал админи"),
    CASHIER(5, "ROLE_CASHIER", "Кассир", "Kassir", "Кассир");

    private final Integer priority;
    private final String nameRu;
    private final String code;
    private final String roleCode;
    private final String nameUz;
    private final String nameCyril;

    TerminalRole(Integer priority, String roleCode, String nameRu, String nameUz, String nameCyril) {
        this.priority = priority;
        this.nameRu = nameRu;
        this.roleCode = roleCode;
        this.code = name();
        this.nameUz = nameUz;
        this.nameCyril = nameCyril;
    }

    @JsonCreator
    public static TerminalRole forCodeValue(Map<String, String> value) {
        return TerminalRole.valueOf(value.get("code"));
    }
}
