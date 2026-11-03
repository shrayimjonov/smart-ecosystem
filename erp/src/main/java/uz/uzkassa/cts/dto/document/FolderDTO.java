package uz.uzkassa.cts.dto.document;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
public class FolderDTO extends CommonDTO implements Serializable {

    Long parentId;

    String permissionCode;
}
