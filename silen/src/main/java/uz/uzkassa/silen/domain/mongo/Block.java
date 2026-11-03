package uz.uzkassa.silen.domain.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.index.Indexed;
import uz.uzkassa.silen.dto.mongo.BlockDTO;
import uz.uzkassa.silen.dto.partyaggregation.AggregationStatusChangedPayload;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.AggregationType;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.service.ConvertableToAggregationItem;
import uz.uzkassa.silen.service.MultiUnitCode;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
//@CompoundIndexes(value = {
//    @CompoundIndex(name = "org_par_idx", def = "{'organizationId': 1, 'partyId': 1}")
//})
@Document(collection = "mark")
public class Block extends Mark implements ConvertableToAggregationItem, MultiUnitCode {


//    private String orderProductId;
//    private String productName;

    @LastModifiedDate
    @JsonIgnore
    private LocalDateTime lastModifiedDate = LocalDateTime.now();
    private String login;
    private LocalDateTime aggregationDate;
    private String description;
    private LocalDateTime expirationDate;
    private String serialNumber;
    private String serialId;
    @Indexed
    private String partyAggregationId;
    private AggregationType aggregationType = AggregationType.AGGREGATION;
    private int checked;
    private LocalDateTime productionDate;
    private String partitionId;
    //    private String barcode;
    //    private LocalDateTime utilisationTime;
//    private int quantity;
    //    private String utilisationId;
//    private String errorMessage;
    //    private String reportId;

    public BlockDTO toBlockDto() {
        BlockDTO dto = new BlockDTO();
        dto.setId(getId());
        dto.setCode(getCode());
        dto.setPrintCode(getPrintCode());
        dto.setOrderProductId(getOrderProductId());
        dto.setPartyId(getPartyId());
        dto.setProductId(getProductId());
        dto.setProductName(getProductName());
        dto.setParentCode(getParentCode());
        dto.setUsed(isUsed());
        dto.setBarcode(getBarcode());
        dto.setOrganizationId(getOrganizationId());
        if (this.getStatus() != null) {
            dto.setStatus(getStatus());
            dto.setStatusName(this.getStatus().getText());
        }
        dto.setQuantity(getQuantity());
        dto.setUnit(PackageType.BLOCK);
        dto.setUnitName(PackageType.BLOCK.getNameRu());
        dto.setLastModifiedDate(getLastModifiedDate());
        dto.setAggregationDate(getAggregationDate());
        dto.setId(getId());
        dto.setDescription(getDescription());
        dto.setTuronAggregationId(getReportId());
        dto.setUtilisationId(getUtilisationId());
        dto.setUtilisationTime(getUtilisationTime());
        dto.setProductionDate(getProductionDate());
        dto.setExpirationDate(getExpirationDate());
        dto.setErrorMessage(getErrorMessage());
        dto.setChecked(getChecked());
        dto.setSerialNumber(getSerialNumber());
        dto.setSerialId(getSerialId());
        if (getAggregationType() != null) {
            dto.setType(getAggregationType());
            dto.setTypeName(getAggregationType().getText());
        }
        dto.setOrganizationName(getOrganizationName());
        return dto;
    }

    public BlockDTO toListBlockDto() {
        BlockDTO dto = new BlockDTO();
        dto.setId(getId());
        dto.setProductName(getProductName());
        dto.setPrintCode(getPrintCode());
        dto.setCode(getCode());
        dto.setQuantity(getQuantity());
        dto.setLastModifiedDate(getLastModifiedDate());
        dto.setLogin(getLogin());
        if (this.getStatus() != null) {
            dto.setStatus(getStatus());
            dto.setStatusName(this.getStatus().getText());
        }
        dto.setUnit(PackageType.BLOCK);
        dto.setUnitName(PackageType.BLOCK.getNameRu());
        dto.setTuronAggregationId(getReportId());
        dto.setSerialNumber(getSerialNumber());
        dto.setSerialId(getSerialId());
        dto.setBarcode(getBarcode());
        if (getAggregationType() != null) {
            dto.setType(getAggregationType());
            dto.setTypeName(getAggregationType().getText());
        }
        dto.setOrganizationName(getOrganizationName());
        return dto;
    }

    public AggregationStatusChangedPayload toPayload() {
        AggregationStatusChangedPayload payload = new AggregationStatusChangedPayload();
        payload.setPartyAggregationId(getPartyAggregationId());
        payload.setStatus(getStatus().name());
        payload.setCode(getCode());
        payload.setStatusName(getStatus().getText());
        payload.setId(getId());
        return payload;
    }

    @Override
    public AggregationStatus getStatus() {
        return super.getStatus();
    }

    @Override
    public Integer getCapacity() {
        return super.getQuantity();
    }

    @Override
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
