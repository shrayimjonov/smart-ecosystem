package uz.uzkassa.smartpossupply.dto.factura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacturaInvestmentObjectDoc {
    @JsonProperty("ObjectId")
    String objectId;
    @JsonProperty("ObjectName")
    String objectName;

    @JsonProperty("objectid")
    public void setObjectIdLowerCase(String objectId) {
        this.objectId = objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @JsonProperty("objectname")
    public void setObjectNameLowerCase(String objectName) {
        this.objectName = objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

}
