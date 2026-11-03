package uz.uzkassa.silen.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import uz.uzkassa.silen.dto.mongo.MarkDTO;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.MarkStatus;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.utils.Utils;

import java.time.LocalDateTime;
import java.util.regex.Matcher;

@ToString
@Getter
@Setter
//@CompoundIndexes(value = {
//    @CompoundIndex(name = "org_par_idx", def = "{'organizationId': 1, 'partyId': 1}")
//})
@Document(collection = "mark")
@FieldDefaults(level = AccessLevel.PROTECTED)
public class Mark extends ValidationCode {

    String orderProductId;
    String productName;
    String utilisationId;
    String errorMessage;
    LocalDateTime utilisationTime;
    int quantity;
    MarkStatus turonStatus;
    private String barcode;
    private String reportId;
    @CreatedDate
    @JsonIgnore
    private LocalDateTime createdDate = LocalDateTime.now();
    //    @Indexed
    //    String code;
    //    String printCode;
    //    @Indexed
    //    String partyId;
    //    String productId;
    //    @Indexed
    //    String parentCode;
    //    boolean used;
    //    @CreatedDate
    //    @JsonIgnore
    //    LocalDateTime createdDate = LocalDateTime.now();
    //    @LastModifiedDate
    //    @JsonIgnore
    //    LocalDateTime lastModifiedDate = LocalDateTime.now();
    //    String login;
    //    LocalDateTime aggregationDate;
    //    String description;
    //    String reportId;
    //    LocalDateTime productionDate;
    //    LocalDateTime expirationDate;
    //    String serialNumber;
    //    String serialId;
//    PackageType unit;
//    AggregationStatus status;
//    String organizationId;
    //    boolean block;

    public boolean isBlock() {
        if (this.block) return true;
        return getUnit() != null && unit == PackageType.BLOCK;
    }

    public PackageType getPackageType() {
        return unit != null ? unit : PackageType.BOTTLE;
    }

    public AggregationStatus getStatus() {
        return status == null ? AggregationStatus.DRAFT : status;
    }

    public MarkDTO toDto() {
        MarkDTO dto = new MarkDTO();
        dto.setPackageType(getPackageType());
        dto.setId(getId());
        dto.setCode(getCode());
        dto.setPrintCode(getPrintCode());
        dto.setOrderProductId(getOrderProductId());
        dto.setPartyId(getPartyId());
        dto.setProductId(getProductId());
        dto.setProductName(getProductName());
        dto.setParentCode(getParentCode());
        dto.setUsed(isUsed());
        dto.setOrganizationId(getOrganizationId());
        dto.setCreatedDate(getCreatedDate());
        dto.setUtilisationId(getUtilisationId());
        dto.setUtilisationTime(getUtilisationTime());
        if (getStatus() != null) {
            dto.setStatus(getStatus());
            dto.setStatusName(getStatus().getText());
        }
        if (getTuronStatus() != null) {
            dto.setTuronStatus(getTuronStatus());
            dto.setTuronStatusName(getTuronStatus().getText());
        }
        Matcher matcher = Utils.parseMark(getPrintCode());
        if (matcher != null && matcher.find()) {
            dto.setPackUnit(matcher.group(1));
            dto.setGtin(matcher.group(2));
            dto.setSerialNumber(matcher.group(3));
            dto.setOther(matcher.group(4));
        }
        return dto;
    }
}
