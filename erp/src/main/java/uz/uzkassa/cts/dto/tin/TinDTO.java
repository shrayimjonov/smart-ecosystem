package uz.uzkassa.cts.dto.tin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TinDTO implements Serializable {

    private String tin;

    private String name;

    private String address;

}
