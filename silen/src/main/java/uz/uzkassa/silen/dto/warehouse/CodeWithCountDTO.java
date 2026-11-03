package uz.uzkassa.silen.dto.warehouse;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CodeWithCountDTO {
    String code;
    int qty;
}
