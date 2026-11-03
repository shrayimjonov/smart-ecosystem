package uz.uzkassa.smartpossupply.dto.container;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContainerDTO {

    Long id;

    @NotBlank
    String identificationNumber;

    Long companyId;

    String type;

    Double width;

    Double height;

    Double depth;

    Double diameter;

    Double capacity;

    Double weight;

    String material;

    String specialTag;

    String comment;

    @NotEmpty
    List<ContainerItemDTO> items;
}
