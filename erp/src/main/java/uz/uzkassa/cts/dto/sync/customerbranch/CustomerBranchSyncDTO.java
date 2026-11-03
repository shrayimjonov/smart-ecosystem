package uz.uzkassa.cts.dto.sync.customerbranch;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.file.FileListDTO;
import uz.uzkassa.cts.dto.sync.ActivityTypeSyncDTO;
import uz.uzkassa.cts.dto.sync.AddressSyncDTO;
import uz.uzkassa.cts.enums.RelationType;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class CustomerBranchSyncDTO extends CommonDTO implements Serializable {

    String relationId;

    RelationType relationType;

    CompanyCommonDTO company;

    String brand;

    String phone;

    ActivityTypeSyncDTO activityType;

    AddressSyncDTO address;

    String cadastreNumber;

    List<FileListDTO> documents;
}
