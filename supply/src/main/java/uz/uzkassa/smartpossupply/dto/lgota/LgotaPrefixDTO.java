package uz.uzkassa.smartpossupply.dto.lgota;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LgotaPrefixDTO implements Serializable {
    private String prefix;
    private String name;
}
