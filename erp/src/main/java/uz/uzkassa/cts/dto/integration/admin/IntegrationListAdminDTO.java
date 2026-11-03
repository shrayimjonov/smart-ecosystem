package uz.uzkassa.cts.dto.integration.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.file.FileListDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.IntegrationType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntegrationListAdminDTO extends CommonDTO implements Serializable {

    String description;

    FileListDTO logo;

    IntegrationType type;

    CommonStatus status;

    String errorMessage;

    LocalDateTime errorDate;
}
