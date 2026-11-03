package uz.uzkassa.smartpos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NeighborhoodDTO implements Serializable {
    private Long id;
    private Long code;
    private Long regionCode;
    private Long cityCode;
    private String name;
}
