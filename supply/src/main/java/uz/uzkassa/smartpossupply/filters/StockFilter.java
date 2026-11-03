package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.TransferType;

import java.util.List;

@Getter
@Setter
public class StockFilter extends BaseFilter {

    TransferType transferType;

    String aggregationCode;

    String markCode;

    String aggregationBlockCode;

    String barcode;

    String vatBarcode;

    boolean fetchDeleted;

    String relationType;

    String entityType;

    Boolean hasFactura;

    Long productSerialId;

    Long productContractId;

    Long companyCommittentId;

    String acceptanceTransferActNumber;

    String receiptUid;

    String factura;

    Long planRouteId;

    Long planRouteTemplateId;

    List<Long> planRouteIds;

    List<Long> planRouteTemplateIds;

    List<Long> customerIds;

    List<Long> expeditorIds;

    Long cashierId;
}
