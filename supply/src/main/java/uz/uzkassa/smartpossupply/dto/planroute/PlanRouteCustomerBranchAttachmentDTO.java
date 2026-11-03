package uz.uzkassa.smartpossupply.dto.planroute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.enums.PlanRouteAttachmentType;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.02.2023 11:19
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteCustomerBranchAttachmentDTO extends FileDTO {

    PlanRouteAttachmentType attachmentType;
}
