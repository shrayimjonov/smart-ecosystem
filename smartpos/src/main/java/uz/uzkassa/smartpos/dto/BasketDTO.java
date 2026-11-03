package uz.uzkassa.smartpos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasketDTO {
    private Long supplyId;
    private String supplyTin;
    private String supplyName;
    private List<BasketItemDTO> items;
}
