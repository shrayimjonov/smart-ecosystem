package uz.uzkassa.cts.dto.smartpos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.cts.dto.base.FullNameDTO;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmartposOwnerDTO extends FullNameDTO implements Serializable {

    private String phone;
}
