package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
public class UnitDTO implements Serializable {
    @NotNull
    private Long id;
    private String name;
    private String nameRu;
    private String nameUz;
    private String description;
    private Long code;
    private boolean countable;

}
