package uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.publicinfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.privateinfo.IssuerInfo;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CisPublicInfoResponse implements Serializable {

    String code;

    Boolean isHadExtendedCode;

    String packageType;

    String status;

    String extendedStatus;

    IssuerInfo issuerShortInfo;

    String template;

    String productId;

    Integer productGroupId;

    String categoryId;

    LocalDateTime emissionDate;

    LocalDateTime issueDate;

    LocalDateTime productionDate;

    LocalDateTime expirationDate;

    String productSeries;

    Boolean mixedProductGroups;

    Boolean mixedCategories;
}
