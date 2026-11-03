package uz.uzkassa.cts.mappings;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import uz.uzkassa.cts.domain.IncomingInvoiceDetail;
import uz.uzkassa.cts.dto.factura.FacturaDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IncomingInvoiceDetailMapper {

    @Mapping(source = "facturaDoc.facturaNo", target = "facturaNo")
    @Mapping(source = "facturaDoc.facturaDate", target = "facturaDate")
    @Mapping(source = "contractDoc.contractNo", target = "contractNo")
    @Mapping(source = "contractDoc.contractDate", target = "contractDate")
    @Mapping(source = "facturaEmpowermentDoc.agentFacturaId", target = "agentFacturaId")
    @Mapping(source = "facturaEmpowermentDoc.empowermentNo", target = "empowermentNo")
    @Mapping(source = "facturaEmpowermentDoc.empowermentDateOfIssue", target = "empowermentDateOfIssue")
    @Mapping(source = "facturaEmpowermentDoc.agentTin", target = "agentTin")
    @Mapping(source = "facturaEmpowermentDoc.agentPinfl", target = "agentPinfl")
    @Mapping(source = "facturaEmpowermentDoc.agentFio", target = "agentFio")
    @Mapping(source = "itemReleasedDoc.itemReleasedTin", target = "itemReleasedTin")
    @Mapping(source = "itemReleasedDoc.itemReleasedPinfl", target = "itemReleasedPinfl")
    @Mapping(source = "itemReleasedDoc.itemReleasedFio", target = "itemReleasedFio")

    @Mapping(source = "foreignCompany.countryId", target = "countryId")
    @Mapping(source = "foreignCompany.name", target = "name")
    @Mapping(source = "foreignCompany.address", target = "address")
    @Mapping(source = "foreignCompany.bank", target = "bank")
    @Mapping(source = "foreignCompany.account", target = "account")

    @Mapping(source = "seller.name", target = "sellerName")
    @Mapping(source = "seller.account", target = "sellerAccount")
    @Mapping(source = "seller.bankId", target = "sellerBankid")
    @Mapping(source = "seller.address", target = "sellerAddress")
    @Mapping(source = "seller.mobile", target = "sellerMobile")
    @Mapping(source = "seller.workPhone", target = "sellerWorkphone")
    @Mapping(source = "seller.districtId", target = "sellerDistrictid")
    @Mapping(source = "seller.director", target = "sellerDirector")
    @Mapping(source = "seller.vatRegCode", target = "sellerVatregcode")

    @Mapping(source = "buyer.name", target = "buyerName")
    @Mapping(source = "buyer.account", target = "buyerAccount")
    @Mapping(source = "buyer.bankId", target = "buyerBankid")
    @Mapping(source = "buyer.address", target = "buyerAddress")
    @Mapping(source = "buyer.mobile", target = "buyerMobile")
    @Mapping(source = "buyer.workPhone", target = "buyerWorkphone")
    @Mapping(source = "buyer.districtId", target = "buyerDistrictid")
    @Mapping(source = "buyer.director", target = "buyerDirector")
    @Mapping(source = "buyer.vatRegCode", target = "buyerVatregcode")

    @Mapping(source = "productList.facturaProductId", target = "facturaProductId")
    @Mapping(source = "productList.empowermentProductId", target = "empowermentProductId")
    @Mapping(source = "productList.tin", target = "tin")
    @Mapping(source = "productList.hasVat", target = "hasVat")
    @Mapping(source = "productList.hasExcise", target = "hasExcise")
    @Mapping(source = "productList.hasComittent", target = "hasComittent")
    @Mapping(source = "productList.hasMedical", target = "hasMedical")
    @Mapping(source = "productList.deliverySumWithVat", target = "deliverySumWithVat")
    IncomingInvoiceDetail toEntity(FacturaDTO dto);
}
