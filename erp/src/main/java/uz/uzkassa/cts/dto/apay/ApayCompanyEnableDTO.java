package uz.uzkassa.cts.dto.apay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApayCompanyEnableDTO implements Serializable {

    String id;

    String tin;
}
