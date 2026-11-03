package uz.uzkassa.smartpos.integration.ofd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfdFileDTO implements Serializable {
    private String name;
    private String source;
}
