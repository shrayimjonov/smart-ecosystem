package uz.uzkassa.smartpossupply.dto.category;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDTO implements Serializable {

    Long id;

    @NotNull
    String name;

    @NotNull
    EnvironmentType environmentType;

    @NotNull
    Long branchId;

    Long parentId;

    String catalogCode;

    String catalogName;

    boolean main;
}