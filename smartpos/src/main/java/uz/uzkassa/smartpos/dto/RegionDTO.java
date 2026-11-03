package uz.uzkassa.smartpos.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegionDTO implements Serializable {
    private Long id;
    private Long code;
    private String nameRu;
    private String nameUz;
}
