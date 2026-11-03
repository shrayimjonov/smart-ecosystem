package uz.uzkassa.smartpossupply.dto.container;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContainerListDTO {

    Long id;

    String identificationNumber;

    Double width;

    Double height;

    Double depth;

    Double diameter;

    Double weight;

    String material;

    String specialTag;

    String comment;
}
