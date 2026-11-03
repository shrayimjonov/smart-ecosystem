package uz.uzkassa.smartpos.dto.etcsms.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EtcRequestDto implements Serializable {
    private String phone;
    private String text;
}
