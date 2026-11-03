package uz.uzkassa.horeca.dto.eps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EpsFileDTO implements Serializable {

    private Long id;
    private String url;
}
