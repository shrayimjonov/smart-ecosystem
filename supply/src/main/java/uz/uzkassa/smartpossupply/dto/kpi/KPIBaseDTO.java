package uz.uzkassa.smartpossupply.dto.kpi;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.KPIType;
import uz.uzkassa.smartpossupply.enums.Role;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY, // use `type` field to decide
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = KPISalesPlanDTO.class, name = "SALES_PLAN"),
        @JsonSubTypes.Type(value = KPIPlanRouteDTO.class, name = "PLAN_ROUTE"),
        @JsonSubTypes.Type(value = KPINewClientDTO.class, name = "NEW_CLIENT")
})
@FieldDefaults(level = AccessLevel.PROTECTED)
public class KPIBaseDTO {

    @NotNull(message = "{role.required}")
    Role role;

    @NotNull(message = "{kpiType.required}")
    KPIType type;

    @NotBlank(message = "{name.required}")
    String name;

    Long regionId;
}
