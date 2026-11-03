package uz.uzkassa.silen.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.DeliveryType;
import uz.uzkassa.silen.enumeration.TransportType;
import uz.uzkassa.silen.enumeration.WaybillStatus;
import uz.uzkassa.silen.enumeration.WaybillType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaybillDTO implements Serializable {
    private String id;

    @JsonProperty("WaybillLocalType")
    private int waybillLocalType = 0;

    @JsonProperty("OldWaybillDoc")
    private WaybillDocOld oldWaybillDoc = null;

    @JsonProperty("WaybillLocalId")
    private String waybillLocalId;

    @JsonProperty("DeliveryType")
    private Integer deliveryType = 2;

    @JsonProperty("WaybillDoc")
    private WaybillDoc waybillDoc;

    @JsonProperty("ContractDoc")
    private ContractDoc contractDoc;

    @JsonProperty("ResponsiblePerson")
    private PersonDTO responsiblePerson;

    @JsonProperty("Consignor")
    private WaybillCompany consignor;

    @JsonProperty("Consignee")
    private WaybillCompany consignee;

    @JsonProperty("FreightForwarder")
    private WaybillCompany freightForwarder;

    @JsonProperty("Carrier")
    private WaybillCompany carrier;

    @JsonProperty("Client")
    private WaybillClient client;

    @JsonProperty("Payer")
    private WaybillClient payer;

    @JsonProperty("HasCommittent")
    private Boolean hasCommittent = false;

    /**
     * {@link TransportType}
     */
    @JsonProperty("TransportType")
    private Integer transportType = 1;// Roadway

    @JsonProperty("TotalDistance")
    private BigDecimal totalDistance;
    @JsonProperty("DeliveryCost")
    private BigDecimal deliveryCost;
    @JsonProperty("TotalDeliveryCost")
    private BigDecimal totalDeliveryCost;

    @JsonProperty("Roadway")
    private RoadwayDTO roadway;

    @JsonProperty("Railway")
    private RailwayDTO railway;

    private WaybillStatus status;
    private String statusName;
    private WaybillType waybillType;
    private String waybillTypeName;
    private DeliveryType deliveryTypeEnum;
    private String deliveryTypeEnumName;
    private String note;
    private List<WaybillExecutorDTO> executors = new ArrayList<>();
    private String waybillLocalTypeName;
    private String comment;
    @JsonSetter("waybilllocaltype")
    public void setwaybilllocaltypeLowerCase(int waybillLocalType) {
        this.waybillLocalType = waybillLocalType;
    }

    @JsonSetter("oldwaybilldoc")
    public void setoldwaybilldocLowerCase(WaybillDocOld oldWaybillDoc) {
        this.oldWaybillDoc = oldWaybillDoc;
    }

    @JsonSetter("waybilllocalid")
    public void setwaybilllocalidLowerCase(String waybilllocalid) {
        this.waybillLocalId = waybilllocalid;
    }

    @JsonSetter("deliverytype")
    public void setdeliverytypeLowerCase(Integer deliverytype) {
        this.deliveryType = deliverytype;
    }

    @JsonSetter("waybilldoc")
    public void setwaybilldocLowerCase(WaybillDoc waybilldoc) {
        this.waybillDoc = waybilldoc;
    }

    @JsonSetter("contractdoc")
    public void setcontractdocLowerCase(ContractDoc contractdoc) {
        this.contractDoc = contractdoc;
    }

    @JsonSetter("responsibleperson")
    public void setresponsiblepersonLowerCase(PersonDTO responsibleperson) {
        this.responsiblePerson = responsibleperson;
    }

    @JsonSetter("consignor")
    public void setconsignorLowerCase(WaybillCompany consignor) {
        this.consignor = consignor;
    }

    @JsonSetter("consignee")
    public void setconsigneeLowerCase(WaybillCompany consignee) {
        this.consignee = consignee;
    }

    @JsonSetter("freightforwarder")
    public void setfreightforwarderLowerCase(WaybillCompany freightforwarder) {
        this.freightForwarder = freightforwarder;
    }

    @JsonSetter("carrier")
    public void setcarrierLowerCase(WaybillCompany carrier) {
        this.carrier = carrier;
    }

    @JsonSetter("client")
    public void setclientLowerCase(WaybillClient client) {
        this.client = client;
    }

    @JsonSetter("payer")
    public void setpayerLowerCase(WaybillClient payer) {
        this.payer = payer;
    }

    @JsonSetter("hascommittent")
    public void sethascommittentLowerCase(Boolean hascommittent) {
        this.hasCommittent = hascommittent;
    }

    @JsonSetter("transporttype")
    public void settransporttypeLowerCase(Integer transporttype) {
        this.transportType = transporttype;
    }

    @JsonSetter("totaldistance")
    public void settotaldistanceLowerCase(BigDecimal totaldistance) {
        this.totalDistance = totaldistance;
    }

    @JsonSetter("deliverycost")
    public void setdeliverycostLowerCase(BigDecimal deliverycost) {
        this.deliveryCost = deliverycost;
    }

    @JsonSetter("totaldeliverycost")
    public void settotaldeliverycostLowerCase(BigDecimal totaldeliverycost) {
        this.totalDeliveryCost = totaldeliverycost;
    }

    @JsonSetter("roadway")
    public void setroadwayLowerCase(RoadwayDTO roadway) {
        this.roadway = roadway;
    }


    @JsonSetter("railway")
    public void setRailwayLowerCase(RailwayDTO railway) {
        this.railway = railway;
    }
}
