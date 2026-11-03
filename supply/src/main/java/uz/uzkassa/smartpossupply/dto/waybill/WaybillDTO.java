package uz.uzkassa.smartpossupply.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaybillDTO implements Serializable {
    private Long id;

    private Long oldWaybillId;

    @JsonProperty("WaybillLocalId")
    private String waybillLocalId;

    @JsonProperty("DeliveryType")
    private Integer deliveryType = 2;

    @JsonProperty("WaybillLocalType")
    private Integer waybillLocalType = 0;

    @JsonProperty("WaybillDoc")
    private WaybillDocDTO waybillDoc;

    @JsonProperty("OldWaybillDoc")
    private OldWaybillDocDTO OldWaybillDoc;

    @JsonProperty("ContractDoc")
    private WaybillContractDocDTO contractDoc;

    @JsonProperty("ResponsiblePerson")
    private WaybillPersonDTO responsiblePerson;

    @JsonProperty("Consignor")
    private WaybillCompanyDTO consignor;

    @JsonProperty("Consignee")
    private WaybillCompanyDTO consignee;

    @JsonProperty("FreightForwarder")
    private WaybillCompanyDTO freightForwarder;

    @JsonProperty("Carrier")
    private WaybillCompanyDTO carrier;

    @JsonProperty("Client")
    private WaybillClientDTO client;

    @JsonProperty("Payer")
    private WaybillClientDTO payer;

    @JsonProperty("HasCommittent")
    private Boolean hasCommittent = false;

    @JsonProperty("TransportType")
    private Integer transportType = 1;// Roadway

    @JsonProperty("TotalDistance")
    private BigDecimal totalDistance;
    @JsonProperty("DeliveryCost")
    private BigDecimal deliveryCost;
    @JsonProperty("TotalDeliveryCost")
    private BigDecimal totalDeliveryCost;

    @JsonProperty("Roadway")
    private WaybillRoadWayTransportDTO roadway;

    @JsonProperty("Railway")
    private WaybillRoadWayTransportDTO railway;

    private Long shipmentId;

    private Long facturaId;

    private Long stockTransferId;

    private Long stockOutId;


    @JsonSetter("waybilllocalid")
    public void setwaybilllocalidToLowerCase(String waybilllocalid) {
        this.waybillLocalId = waybilllocalid;
    }

    @JsonSetter("deliverytype")
    public void setdeliverytypeToLowerCase(Integer deliverytype) {
        this.deliveryType = deliverytype;
    }

    @JsonSetter("waybilldoc")
    public void setwaybilldocToLowerCase(WaybillDocDTO waybilldoc) {
        this.waybillDoc = waybilldoc;
    }

    @JsonSetter("contractdoc")
    public void setcontractdocToLowerCase(WaybillContractDocDTO contractdoc) {
        this.contractDoc = contractdoc;
    }

    @JsonSetter("responsibleperson")
    public void setresponsiblepersonToLowerCase(WaybillPersonDTO responsibleperson) {
        this.responsiblePerson = responsibleperson;
    }

    @JsonSetter("consignor")
    public void setconsignorToLowerCase(WaybillCompanyDTO consignor) {
        this.consignor = consignor;
    }

    @JsonSetter("consignee")
    public void setconsigneeToLowerCase(WaybillCompanyDTO consignee) {
        this.consignee = consignee;
    }

    @JsonSetter("freightforwarder")
    public void setfreightforwarderToLowerCase(WaybillCompanyDTO freightforwarder) {
        this.freightForwarder = freightforwarder;
    }

    @JsonSetter("carrier")
    public void setcarrierToLowerCase(WaybillCompanyDTO carrier) {
        this.carrier = carrier;
    }

    @JsonSetter("client")
    public void setclientToLowerCase(WaybillClientDTO client) {
        this.client = client;
    }

    @JsonSetter("payer")
    public void setpayerToLowerCase(WaybillClientDTO payer) {
        this.payer = payer;
    }

    @JsonSetter("hascommittent")
    public void sethascommittentToLowerCase(Boolean hascommittent) {
        this.hasCommittent = hascommittent;
    }

    @JsonSetter("transporttype")
    public void settransporttypeToLowerCase(Integer transporttype) {
        this.transportType = transporttype;
    }

    @JsonSetter("totaldistance")
    public void settotaldistanceToLowerCase(BigDecimal totaldistance) {
        this.totalDistance = totaldistance;
    }

    @JsonSetter("deliverycost")
    public void setdeliverycostToLowerCase(BigDecimal deliverycost) {
        this.deliveryCost = deliverycost;
    }

    @JsonSetter("totaldeliverycost")
    public void settotaldeliverycostToLowerCase(BigDecimal totaldeliverycost) {
        this.totalDeliveryCost = totaldeliverycost;
    }

    @JsonSetter("roadway")
    public void setroadwayToLowerCase(WaybillRoadWayTransportDTO roadway) {
        this.roadway = roadway;
    }

    @JsonSetter("railway")
    public void setrailwayToLowerCase(WaybillRoadWayTransportDTO railway) {
        this.railway = railway;
    }
}
