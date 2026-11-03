package uz.uzkassa.smartpossupply.dto.lot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LotPrefixDTO implements Serializable {
    private String lotId;
    private String prefix;
    private String typeOfDealName;
}
