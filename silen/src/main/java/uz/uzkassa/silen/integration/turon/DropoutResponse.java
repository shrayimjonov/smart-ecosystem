package uz.uzkassa.silen.integration.turon;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class DropoutResponse implements Serializable {

    String reportId;

    String omsId;
}
