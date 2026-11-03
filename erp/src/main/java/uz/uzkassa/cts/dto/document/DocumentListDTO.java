package uz.uzkassa.cts.dto.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class DocumentListDTO extends CommonDTO implements Serializable {

    String path;

    String fileSize;
}
