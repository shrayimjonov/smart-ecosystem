package uz.uzkassa.smartpossupply.dto.container;

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
public class ContainerDetailDTO {

    Long id;

    String identificationNumber;

    String type;

    String material;

    String specialTag;

    Double weight;

    Double width;

    Double height;

    Double depth;

    Double diameter;

    String comment;

    List<ContainerItemDetailDTO> items;
}
