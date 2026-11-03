package uz.uzkassa.silen.dto.invoice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class VillageDTO implements Serializable {
    private Long id;
    private String soato;
    private Long code;
    private String nameUzCyrl;
    private String nameUzLatn;
    private String nameRu;
    private String name;
}
