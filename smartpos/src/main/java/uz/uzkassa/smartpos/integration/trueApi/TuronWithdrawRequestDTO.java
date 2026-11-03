package uz.uzkassa.smartpos.integration.trueApi;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TuronWithdrawRequestDTO implements Serializable {
    private String document;
    private String signature;
}
