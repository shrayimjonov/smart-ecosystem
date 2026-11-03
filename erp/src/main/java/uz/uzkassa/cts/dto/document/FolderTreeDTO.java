package uz.uzkassa.cts.dto.document;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FolderTreeDTO extends CommonDTO implements Serializable {

    CommonDTO parent;

    List<FolderTreeDTO> children = new ArrayList<>();
}
