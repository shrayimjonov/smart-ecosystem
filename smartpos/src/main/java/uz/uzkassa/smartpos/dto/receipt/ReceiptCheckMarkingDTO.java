package uz.uzkassa.smartpos.dto.receipt;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ReceiptCheckMarkingDTO {
    private Set<String> markings;
    private String code;
}
