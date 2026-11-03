package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.acceptancetransferact.AcceptanceTransferAct;
import uz.uzkassa.smartpossupply.domain.acceptancetransferact.AcceptanceTransferActDetail;
import uz.uzkassa.smartpossupply.domain.acceptancetransferact.AcceptanceTransferActProduct;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.billing.UsagePlan;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.factura.Factura;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransfer;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransferProduct;
import uz.uzkassa.smartpossupply.domain.waybill.Carriages;
import uz.uzkassa.smartpossupply.domain.waybill.Waybill;
import uz.uzkassa.smartpossupply.domain.waybill.WaybillExecutor;
import uz.uzkassa.smartpossupply.domain.waybill.WaybillProduct;
import uz.uzkassa.smartpossupply.domain.waybill.WaybillProductGroup;
import uz.uzkassa.smartpossupply.domain.waybill.WaybillTransport;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.companyfieldhistory.CompanyFieldHistoryDTO;
import uz.uzkassa.smartpossupply.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.smartpossupply.dto.eimzo.SignatureCertificateDto;
import uz.uzkassa.smartpossupply.dto.factura.FacturaDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaProductDTO;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.dto.factura.supply.SupplyFacturaDTO;
import uz.uzkassa.smartpossupply.dto.waybill.SupplyWaybillDTO;
import uz.uzkassa.smartpossupply.dto.waybill.TransportCheckRequestDTO;
import uz.uzkassa.smartpossupply.dto.waybill.TransportCheckResponseDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillClientDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillCompanyDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillContractDocDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillDocDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillDriverTransportDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillEmpowermentDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillListDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillPersonDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillPointDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillProductDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillProductGroupDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillProductInfoDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillRoadWayTransportDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillSelectItem;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillSendDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillTruckDTO;
import uz.uzkassa.smartpossupply.enums.CountableServiceTypes;
import uz.uzkassa.smartpossupply.enums.FieldHistoryType;
import uz.uzkassa.smartpossupply.enums.GuidType;
import uz.uzkassa.smartpossupply.enums.StockTransferType;
import uz.uzkassa.smartpossupply.enums.TransportType;
import uz.uzkassa.smartpossupply.enums.WaybillDeliveryType;
import uz.uzkassa.smartpossupply.enums.WaybillLocalType;
import uz.uzkassa.smartpossupply.enums.WaybillStatus;
import uz.uzkassa.smartpossupply.enums.WaybillType;
import uz.uzkassa.smartpossupply.filters.WaybillFilter;
import uz.uzkassa.smartpossupply.integration.EimzoClient;
import uz.uzkassa.smartpossupply.integration.FacturaRestClient;
import uz.uzkassa.smartpossupply.repository.AcceptanceTransferActRepository;
import uz.uzkassa.smartpossupply.repository.FacturaRepository;
import uz.uzkassa.smartpossupply.repository.OrderRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.repository.StockOutRepository;
import uz.uzkassa.smartpossupply.repository.StockTransferRepository;
import uz.uzkassa.smartpossupply.repository.WaybillExecutorRepository;
import uz.uzkassa.smartpossupply.repository.WaybillProductGroupRepository;
import uz.uzkassa.smartpossupply.repository.WaybillProductRepository;
import uz.uzkassa.smartpossupply.repository.WaybillRepository;
import uz.uzkassa.smartpossupply.repository.WaybillTransportRepository;
import uz.uzkassa.smartpossupply.utils.DateUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class WaybillService extends BaseService {

    WaybillRepository waybillRepository;
    FacturaRestClient facturaClient;
    EimzoClient eimzoClient;
    WaybillExecutorRepository waybillExecutorRepository;
    FacturaRepository facturaRepository;
    AcceptanceTransferActRepository acceptanceTransferActRepository;
    WaybillTransportRepository waybillTransportRepository;
    FacturaService facturaService;
    WaybillProductGroupRepository waybillProductGroupRepository;
    WaybillProductRepository waybillProductRepository;
    StockTransferRepository stockTransferRepository;
    ProductRepository productRepository;
    OrderRepository orderRepository;
    WaybillWebhookService waybillWebhookService;
    StockOutRepository stockOutRepository;
    UsagePlanService usagePlanService;
    CompanyFieldHistoryService companyFieldHistoryService;

    @Transactional
    public Long create(WaybillDTO waybillDTO) {
        return this.save(new Waybill(), waybillDTO);
    }

    @Transactional
    public Long update(Long id, WaybillDTO waybillDTO) {
        Waybill waybill = waybillRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Waybill.class.getSimpleName(), "id", id));
        if (!isFromAdmin() && !(waybill.getCompanyId().equals(getCurrentCompanyId()) || waybill.getClientId().equals(getCurrentCompanyId()))) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ACCESS_DENIED), localizationService.localize(MessageConstants.CANNOT_UPDATE_OTHER_COMPANY_DATA)).get();
        }
        return this.save(waybill, waybillDTO);
    }

    private Long save(Waybill waybill, WaybillDTO waybillDTO) {

        Waybill oldWaybill = null;
        if (WaybillLocalType.EDITED.equals(WaybillLocalType.fromCode(waybillDTO.getWaybillLocalType()))) {
            if (waybillDTO.getOldWaybillId() == null) {
                throw validationService.badRequestExceptionThrow("Old waybillId is required if waybill type EDITED").get();
            }
            oldWaybill = waybillRepository.findById(waybillDTO.getOldWaybillId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Waybill.class.getSimpleName(), "id", waybillDTO.getOldWaybillId()));
        }
        boolean isNew = waybill.getId() == null;
        waybill.setWaybillType(WaybillType.OUTGOING);
        waybill.setCompanyId(getCurrentCompanyId());
        waybill.setFacturaId(waybillDTO.getFacturaId());
        waybill.setShipmentId(waybillDTO.getShipmentId());
        waybill.setStockTransferId(waybillDTO.getStockTransferId());
        if (!StringUtils.hasText(waybill.getWaybillLocalId())) {
            waybill.setWaybillLocalId(facturaService.getGuid(GuidType.FACTURA));
        }
        waybill.setDeliveryType(WaybillDeliveryType.fromCode(waybillDTO.getDeliveryType()));
        if (waybillDTO.getWaybillDoc() != null) {
            waybill.setWaybillNo(waybillDTO.getWaybillDoc().getWaybillNo());
            waybill.setWaybillDate(waybillDTO.getWaybillDoc().getWaybillDate());
        }
        if (oldWaybill != null) {
            waybill.setOldWaybillId(oldWaybill.getWaybillLocalId());
            waybill.setOldWaybillNo(oldWaybill.getWaybillNo());
            waybill.setOldWaybillDate(oldWaybill.getWaybillDate());

            // get contract from old waybill
            waybill.setContractNo(oldWaybill.getContractNo());
            waybill.setContractDate(oldWaybill.getContractDate());
        } else if (waybillDTO.getContractDoc() != null) {
            waybill.setContractNo(waybillDTO.getContractDoc().getContractNo());
            waybill.setContractDate(waybillDTO.getContractDoc().getContractDate());
        } else {
            waybill.setContractNo(null);
            waybill.setContractDate(null);
        }

        if (waybillDTO.getConsignor() != null && StringUtils.hasText(waybillDTO.getConsignor().getTinOrPinfl())) {
            Company seller = companyRepository.findByTinAndDeletedIsFalse(waybillDTO.getConsignor().getTinOrPinfl()).orElse(companyRepository.findFirstByTinAndDeletedIsFalse(waybillDTO.getConsignor().getTinOrPinfl()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", waybillDTO.getConsignor().getTinOrPinfl())));
            waybill.setConsignorTinOrPinfl(seller.getTin());
            waybill.setConsignorName(seller.getName());
            if (StringUtils.hasText(waybillDTO.getConsignor().getBranchCode())) {
                waybill.setConsignorBranchCode(waybillDTO.getConsignor().getBranchCode());
                waybill.setConsignorBranchName(waybillDTO.getConsignor().getBranchName());
            }
        }

        if (waybillDTO.getConsignee() != null) {
            waybill.setConsigneeTinOrPinfl(waybillDTO.getConsignee().getTinOrPinfl());
            waybill.setConsigneeName(waybillDTO.getConsignee().getName());
            if (StringUtils.hasText(waybillDTO.getConsignee().getBranchCode())) {
                waybill.setConsigneeBranchCode(waybillDTO.getConsignee().getBranchCode());
                waybill.setConsigneeBranchName(waybillDTO.getConsignee().getBranchName());
            }
        }

        if (waybillDTO.getFreightForwarder() != null) {
            waybill.setFreightForwarderTinOrPinfl(waybillDTO.getFreightForwarder().getTinOrPinfl());
            waybill.setFreightForwarderName(waybillDTO.getFreightForwarder().getName());
            waybill.setFreightForwarderBranchCode(waybillDTO.getFreightForwarder().getBranchCode());
            waybill.setFreightForwarderBranchName(waybillDTO.getFreightForwarder().getBranchName());
        }

        if (waybillDTO.getCarrier() != null) {
            waybill.setCarrierTinOrPinfl(waybillDTO.getCarrier().getTinOrPinfl());
            waybill.setCarrierName(waybillDTO.getCarrier().getName());
            waybill.setCarrierBranchCode(waybillDTO.getCarrier().getBranchCode());
            waybill.setCarrierBranchName(waybillDTO.getCarrier().getBranchName());
        }

        if (waybillDTO.getClient() != null && StringUtils.hasText(waybillDTO.getClient().getTinOrPinfl())) {
            Company client = companyRepository.findByTinAndDeletedIsFalse(waybillDTO.getClient().getTinOrPinfl()).orElse(companyRepository.findFirstByTinAndDeletedIsFalse(waybillDTO.getClient().getTinOrPinfl()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", waybillDTO.getClient().getTinOrPinfl())));
            waybill.setClientId(client.getId());

            waybill.setClientBranchId(waybillDTO.getClient().getBranchId());
            waybill.setClientBranchCode(waybillDTO.getClient().getBranchCode());
            waybill.setClientBranchName(waybillDTO.getClient().getBranchName());

            waybill.setClientContractNo(waybillDTO.getClient().getContractNo());
            waybill.setClientContractDate(waybillDTO.getClient().getContractDate());
        }

        if (waybillDTO.getPayer() != null) {
            waybill.setPayerTinOrPinfl(waybillDTO.getPayer().getTinOrPinfl());
            waybill.setPayerName(waybillDTO.getPayer().getName());
            waybill.setPayerBranchCode(waybillDTO.getPayer().getBranchCode());
            waybill.setPayerBranchName(waybillDTO.getPayer().getBranchName());
            waybill.setPayerContractNo(waybillDTO.getPayer().getContractNo());
            waybill.setPayerContractDate(waybillDTO.getPayer().getContractDate());
        }
        if (waybillDTO.getResponsiblePerson() != null) {
            waybill.setResponsiblePersonPinfl(waybillDTO.getResponsiblePerson().getPinfl());
            waybill.setResponsiblePersonFullName(waybillDTO.getResponsiblePerson().getFullName());
        }

        waybill.setHasCommittent(waybillDTO.getHasCommittent());
        waybill.setStatus(WaybillStatus.DRAFT);
        waybill.setTotalDistance(waybillDTO.getTotalDistance());
        waybill.setDeliveryCost(waybillDTO.getDeliveryCost());
        waybill.setTotalDeliveryCost(waybillDTO.getTotalDeliveryCost());
        waybill.setTransportType(TransportType.fromCode(waybillDTO.getTransportType()));

        waybillRepository.save(waybill);
        WaybillRoadWayTransportDTO wayBillTransportDTO = null;
        if (waybillDTO.getRoadway() != null) {
            wayBillTransportDTO = waybillDTO.getRoadway();
        } else if (waybillDTO.getRailway() != null) {
            wayBillTransportDTO = waybillDTO.getRailway();
        }
        if (wayBillTransportDTO != null) {
            WaybillTransport waybillTransport;
            if (isNew) {
                waybillTransport = new WaybillTransport();
            } else {
                waybillTransport = waybillTransportRepository.findFirstByWaybillId(waybill.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(WaybillTransport.class.getSimpleName(), "waybillId", waybill.getId()));
            }
            if (wayBillTransportDTO.getDriver() != null) {
                waybillTransport.setDriverId(wayBillTransportDTO.getDriver().getId());
                waybillTransport.setDriverPinfl(wayBillTransportDTO.getDriver().getPinfl());
                waybillTransport.setDriverName(wayBillTransportDTO.getDriver().getFullName());
            }
            if (wayBillTransportDTO.getTruckDTO() != null) {
                waybillTransport.setTruckRegNo(wayBillTransportDTO.getTruckDTO().getRegNo());
                waybillTransport.setTruckModel(wayBillTransportDTO.getTruckDTO().getModel());
            } else {
                waybillTransport.setTruckRegNo(null);
                waybillTransport.setTruckModel(null);
            }
            if (wayBillTransportDTO.getTrailer() != null) {
                waybillTransport.setTrailerRegNo(wayBillTransportDTO.getTrailer().getRegNo());
                waybillTransport.setTrailerModel(wayBillTransportDTO.getTrailer().getModel());
            } else {
                waybillTransport.setTrailerRegNo(null);
                waybillTransport.setTrailerModel(null);
            }
            waybillTransport.setTrailerType(wayBillTransportDTO.getTrailerType());
            waybillTransport.setTransportType(TransportType.fromCode(waybillDTO.getTransportType()));

            if (!CollectionUtils.isEmpty(wayBillTransportDTO.getCarriages())) {
                for (WaybillTruckDTO truckDTO : wayBillTransportDTO.getCarriages()) {
                    Carriages carriages = new Carriages();
                    carriages.setModel(truckDTO.getModel());
                    carriages.setRegNo(truckDTO.getRegNo());
                    waybillTransport.addCarriages(carriages);
                }
            } else {
                waybillTransport.getCarriages().clear();
            }
            waybillTransport.setOtherCarOwners(wayBillTransportDTO.getOtherCarOwners());

            // rail-way properties

            waybillTransport.setTrainDirection(wayBillTransportDTO.getTrainDirection());
            waybillTransport.setTrainCarriageNumber(wayBillTransportDTO.getTrainCarriageNumber());
            if (waybillTransport.getId() == null) {
                waybillTransport.setWaybillId(waybill.getId());
                waybillTransport.setWaybill(waybill);
            }
            waybillTransportRepository.save(waybillTransport);

            for (WaybillProductGroupDTO groupDTO : wayBillTransportDTO.getProductGroups()) {
                if (!isNew) {
                    waybillProductGroupRepository.deleteAllByWaybillTransportId(waybillTransport.getId());
                }
                WaybillProductGroup waybillProductGroup = new WaybillProductGroup();
                if (groupDTO.getUnloadingTrustee() != null) {
                    waybillProductGroup.setUnloadingTrusteePinfl(groupDTO.getUnloadingTrustee().getPinfl());
                    waybillProductGroup.setUnloadingTrusteeName(groupDTO.getUnloadingTrustee().getFullName());
                }
                if (groupDTO.getLoadingPoint() != null) {
                    waybillProductGroup.setLoadingRegionId(groupDTO.getLoadingPoint().getRegionId());
                    waybillProductGroup.setLoadingRegionName(groupDTO.getLoadingPoint().getRegionName());
                    waybillProductGroup.setLoadingDistrictId(groupDTO.getLoadingPoint().getDistrictCode());
                    waybillProductGroup.setLoadingDistrictName(groupDTO.getLoadingPoint().getDistrictName());
                    waybillProductGroup.setLoadingMahallaId(groupDTO.getLoadingPoint().getMahallaId());
                    waybillProductGroup.setLoadingMahallaName(groupDTO.getLoadingPoint().getMahallaName());
                    waybillProductGroup.setLoadingAdress(groupDTO.getLoadingPoint().getAddress());
                    waybillProductGroup.setLoadingLongitude(groupDTO.getLoadingPoint().getLongitude());
                    waybillProductGroup.setLoadingLatitude(groupDTO.getLoadingPoint().getLatitude());
                }

                if (groupDTO.getLoadingTrustee() != null) {
                    waybillProductGroup.setLoadingTrusteePinfl(groupDTO.getLoadingTrustee().getPinfl());
                    waybillProductGroup.setLoadingTrusteeName(groupDTO.getLoadingTrustee().getFullName());
                }

                if (groupDTO.getUnloadingTrustee() != null) {
                    waybillProductGroup.setUnloadingTrusteePinfl(groupDTO.getUnloadingTrustee().getPinfl());
                    waybillProductGroup.setUnloadingTrusteeName(groupDTO.getUnloadingTrustee().getFullName());
                }
                if (groupDTO.getUnloadingPoint() != null) {
                    waybillProductGroup.setUnloadingRegionId(groupDTO.getUnloadingPoint().getRegionId());
                    waybillProductGroup.setUnloadingRegionName(groupDTO.getUnloadingPoint().getRegionName());
                    waybillProductGroup.setUnloadingDistrictId(groupDTO.getUnloadingPoint().getDistrictCode());
                    waybillProductGroup.setUnloadingDistrictName(groupDTO.getUnloadingPoint().getDistrictName());
                    waybillProductGroup.setUnloadingMahallaId(groupDTO.getUnloadingPoint().getMahallaId());
                    waybillProductGroup.setUnloadingMahallaName(groupDTO.getUnloadingPoint().getMahallaName());
                    waybillProductGroup.setUnloadingAddress(groupDTO.getUnloadingPoint().getAddress());
                    waybillProductGroup.setUnloadingLongitude(groupDTO.getUnloadingPoint().getLongitude());
                    waybillProductGroup.setUnloadingLatitude(groupDTO.getUnloadingPoint().getLatitude());
                }

                if (groupDTO.getUnloadingEmpowermentDTO() != null) {
                    waybillProductGroup.setUnloadingEmpowermentId(groupDTO.getUnloadingEmpowermentDTO().getEmpowermentId());
                    waybillProductGroup.setUnloadingEmpowermentNo(groupDTO.getUnloadingEmpowermentDTO().getEmpowermentNo());
                    waybillProductGroup.setUnloadingEmpowermentDateOfExpire(groupDTO.getUnloadingEmpowermentDTO().getEmpowermentDateOfExpire());
                    waybillProductGroup.setUnloadingEmpowermentDateOfIssue(groupDTO.getUnloadingEmpowermentDTO().getEmpowermentDateOfIssue());
                }

                if (groupDTO.getProductInfoDTO() != null) {
                    waybillProductGroup.setTotalDeliverySum(groupDTO.getProductInfoDTO().getTotalDeliverySum());
                    waybillProductGroup.setTotalWeightBrutto(groupDTO.getProductInfoDTO().getTotalWeightBrutto());
                }

                waybillProductGroup.setWaybillTransportId(waybillTransport.getId());
                waybillProductGroup.setWaybillTransport(waybillTransport);
                waybillProductGroupRepository.save(waybillProductGroup);

                if (groupDTO.getProductInfoDTO() != null && !CollectionUtils.isEmpty(groupDTO.getProductInfoDTO().getProducts())) {
                    List<WaybillProduct> waybillProducts = new ArrayList<>();
                    for (WaybillProductDTO waybillProductDTO : groupDTO.getProductInfoDTO().getProducts()) {
                        WaybillProduct waybillProduct = new WaybillProduct();
                        if (waybillProductDTO.getProductId() != null) {
                            waybillProduct.setProductId(waybillProductDTO.getProductId());
                            productRepository.findById(waybillProductDTO.getProductId()).ifPresent(product -> {
                                if (!Objects.equals(waybillProductDTO.getNetto(), product.getWeightNetto()) || !Objects.equals(waybillProductDTO.getBrutto(), product.getWeightBrutto())) {
                                    product.setWeightNetto(waybillProductDTO.getNetto());
                                    product.setWeightNetto(waybillProductDTO.getBrutto());
                                    productRepository.save(product);
                                }
                            });
                        }
                        waybillProduct.setOrdNo(waybillProductDTO.getOrdNo());
                        waybillProduct.setCommittentTinOrPinfl(waybillProductDTO.getCommittentTinOrPinfl());
                        waybillProduct.setCommittentName(waybillProductDTO.getCommittentName());
                        waybillProduct.setProductName(waybillProductDTO.getProductName());
                        waybillProduct.setCatalogCode(waybillProductDTO.getCatalogCode());
                        waybillProduct.setCatalogName(waybillProductDTO.getCatalogName());
                        waybillProduct.setPackageCode(waybillProductDTO.getPackageCode());
                        waybillProduct.setPackageName(waybillProductDTO.getPackageName());
                        waybillProduct.setQty(waybillProductDTO.getAmount());
                        waybillProduct.setPrice(waybillProductDTO.getPrice());
                        waybillProduct.setDeliverySum(waybillProductDTO.getDeliverySum());
                        waybillProduct.setWeightNetto(Optional.ofNullable(waybillProductDTO.getWeightNetto()).orElse(BigDecimal.ZERO));
                        waybillProduct.setWeightBrutto(Optional.ofNullable(waybillProductDTO.getWeightBrutto()).orElse(BigDecimal.ZERO));

                        waybillProduct.setProductGroupId(waybillProductGroup.getId());
                        waybillProduct.setProductGroup(waybillProductGroup);

                        waybillProducts.add(waybillProduct);
                    }
                    waybillProductRepository.saveAll(waybillProducts);
                }
            }
        }
        this.recordFieldHistory(waybill, wayBillTransportDTO != null ? wayBillTransportDTO.getDriver() : null);
        return waybill.getId();
    }

    public Page<WaybillListDTO> getList(WaybillFilter filter) {
        if (filter.isOutgoing()) {
            filter.setConsignorTin(Optional.ofNullable(filter.getConsignorTin()).orElse(getCurrentCompanyTin()));
        } else {
            filter.setConsigneeTin(Optional.ofNullable(filter.getConsigneeTin()).orElse(getCurrentCompanyTin()));
        }
        ResultList<Waybill> resultList = waybillRepository.getResultList(filter);
        List<WaybillListDTO> result = resultList.getList().stream().map(waybill -> {
            WaybillListDTO waybillListDTO = new WaybillListDTO();
            waybillListDTO.setId(waybill.getId());
            if (waybill.getStockTransfer() != null) {
                waybillListDTO.setStockTransfer(waybill.getStockTransfer().toCommonDTO());
            }
            waybillListDTO.setWaybillDoc(new WaybillSelectItem(null, waybill.getWaybillNo(), waybill.getWaybillDate()));
            waybillListDTO.setContractDoc(new WaybillSelectItem(null, waybill.getContractNo(), waybill.getContractDate()));
            waybillListDTO.setConsignee(new WaybillSelectItem(waybill.getConsigneeTinOrPinfl(), waybill.getConsigneeName()));
            waybillListDTO.setTotalDeliveryCost(waybill.getTotalDeliveryCost());
            if (waybill.getStatus() != null) {
                waybillListDTO.setStatus(waybill.getStatus().toListDto());
            }
            if (waybill.getFactura() != null) {
                waybillListDTO.setFactura(waybill.getFactura().toLink());
                Branch branch = waybill.getFactura().getBranch();
                if (branch != null) {
                    waybillListDTO.setBranch(branch.toCommonDTO());
                }
            }
            if (waybill.getShipment() != null) {
                waybillListDTO.setShipment(waybill.getShipment().toLinkDTO());
            }
            return waybillListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public SupplyWaybillDTO get(Long id) {
        return waybillRepository.getDetail(id).map(waybill -> {
            if (StringUtils.hasText(waybill.getSignatureContent()) && WaybillStatus.DRAFT != waybill.getStatus()) {
                SupplyWaybillDTO supplyWaybillDTO = new SupplyWaybillDTO();
                supplyWaybillDTO.setWaybillDTO(waybillWebhookService.convertFromSign(waybill.getSignatureContent()));
                supplyWaybillDTO.setId(waybill.getId());
                supplyWaybillDTO.setStatus(waybill.getStatus().toDto());
                supplyWaybillDTO.setWaybillType(waybill.getWaybillType());
                supplyWaybillDTO.setDeliveryTypeEnum(waybill.getDeliveryType());
                supplyWaybillDTO.setNote(waybill.getNotes());
                for (WaybillExecutor waybillExecutor : waybill.getExecutors()) {
                    supplyWaybillDTO.addExecutor(waybillExecutor.toDTO());
                }
                if (waybill.getFactura() != null) {
                    supplyWaybillDTO.setFactura(waybill.getFactura().toLink());
                }
                if (waybill.getShipment() != null) {
                    supplyWaybillDTO.setShipment(waybill.getShipment().toLinkDTO());
                }
                if (waybill.getStockTransfer() != null) {
                    supplyWaybillDTO.setStockTransfer(waybill.getStockTransfer().toCommonDTO());
                }
                return supplyWaybillDTO;
            }
            WaybillTransport waybillTransport = waybillTransportRepository.findFirstByWaybillId(waybill.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(WaybillTransport.class.getSimpleName(), "waybillId", waybill.getId()));
            return waybill.toDTO(waybillTransport);
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Waybill.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void send(Long id, WaybillSendDTO sendDTO) {
        Waybill waybill = waybillRepository.findById(id)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Waybill.class.getSimpleName(), "id", id));
        this.validateCompany(waybill.getCompanyId());
        Company company = getCurrentCompany();
        UsagePlan usagePlan = usagePlanService.getUsagePlan(CountableServiceTypes.INVOICE.getCode());
        if (!company.isFreeTrial()) {
            if (usagePlan.getQty() <= 0) {
                throw validationService.badRequestExceptionThrow("Недостаточно баланса для отправки TTH").get();
            }
        }

        Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(sendDTO.getSign());

        if (pkcs7Dto.isSuccess() && !CollectionUtils.isEmpty(pkcs7Dto.getTimestampedSignerList())) {
            String sign = pkcs7Dto.getPkcs7b64();
            SignatureCertificateDto certificateDto = pkcs7Dto.getTimestampedSignerList().getFirst();
            sendDTO.setSign(sign);
            facturaClient.sendWaybill(
                    SecurityUtils.getCurrentRequestLanguageEnum().name(),
                    getCurrentCompanyTin(),
                    sendDTO, id
            );

            waybill.setSignatureContent(sendDTO.getSign());
            waybill.setStatus(WaybillStatus.ConsignorSent);
            waybillRepository.save(waybill);

            WaybillExecutor waybillExecutor = new WaybillExecutor();
            waybillExecutor.setWaybillId(id);
            waybillExecutor.setStatus(WaybillStatus.ConsignorSent);
            waybillExecutor.setOperationNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
            waybillExecutor.setOperationBy(certificateDto.getSubjectName().getFullName());
            waybillExecutor.setOperationDate(LocalDateTime.now());
            waybillExecutorRepository.save(waybillExecutor);
            usagePlan.setQty(usagePlan.getQty() - 1);
            usagePlanRepository.save(usagePlan);

            WaybillPersonDTO driver = waybillTransportRepository.findFirstByWaybillId(waybill.getId())
                    .map(waybillTransport -> new WaybillPersonDTO(waybillTransport.getDriverPinfl(), waybillTransport.getDriverName()))
                    .orElse(null);
            this.recordFieldHistory(waybill, driver);
        } else {
            log.debug("JSON: {}", pkcs7Dto);
            validationService.badRequestExceptionThrow("Not verified, reason: " + pkcs7Dto.getMessage()).get();
        }
    }

    @Transactional
    public void cancel(WaybillSendDTO waybillSendDTO, Long id) {
        waybillRepository.findById(id).map(waybill -> {
                    if (WaybillStatus.ConsigneeAccepted != waybill.getStatus()) {
                        validationService.badRequestExceptionThrow("Невозможно отменить ТТЮ со статусом " + waybill.getStatus()).get();
                    }
                    Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(waybillSendDTO.getSign());
                    if (pkcs7Dto.isSuccess() && !CollectionUtils.isEmpty(pkcs7Dto.getTimestampedSignerList())) {
                        SignatureCertificateDto certificateDto = pkcs7Dto.getTimestampedSignerList().size() > 1 ?
                                pkcs7Dto.getTimestampedSignerList().get(1) : pkcs7Dto.getTimestampedSignerList().get(0);

                        waybillSendDTO.setSign(pkcs7Dto.getPkcs7b64());
                        facturaClient.cancelWaybill(SecurityUtils.getCurrentRequestLanguageEnum().name(), getCurrentCompanyTin(), waybillSendDTO);

                        waybill.setStatus(WaybillStatus.ConsignorCanceled);
                        waybill.setNotes(waybillSendDTO.getNotes());
                        waybillRepository.save(waybill);
                        WaybillExecutor waybillExecutor = new WaybillExecutor();
                        waybillExecutor.setWaybillId(waybill.getId());
                        waybillExecutor.setStatus(WaybillStatus.ConsignorCanceled);
                        waybillExecutor.setOperationNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
                        waybillExecutor.setOperationBy(certificateDto.getSubjectName().getFullName());
                        waybillExecutor.setOperationDate(LocalDateTime.now());
                        waybillExecutorRepository.save(waybillExecutor);
                    } else {
                        validationService.badRequestExceptionThrow("Not verified, reason: " + pkcs7Dto.getMessage()).get();
                    }
                    return waybill;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Waybill.class.getSimpleName(), "id", waybillSendDTO.getWaybillLocalId()));

    }

    @Transactional
    public void acceptOrReject(WaybillSendDTO waybillSendDTO, WaybillStatus status, Long id) {
        waybillRepository.findById(id)
                .map(waybill -> {
                    this.validateCompany(waybill.getCompanyId());
                    if (WaybillStatus.ConsignorSent != waybill.getStatus()) {
                        validationService.badRequestExceptionThrow("Невозможно отменить ТТЮ со статусом " + waybill.getStatus()).get();
                    }
                    Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(waybillSendDTO.getSign());
                    if (pkcs7Dto.isSuccess() && !CollectionUtils.isEmpty(pkcs7Dto.getTimestampedSignerList())) {
                        SignatureCertificateDto certificateDto = pkcs7Dto.getTimestampedSignerList().size() > 1 ?
                                pkcs7Dto.getTimestampedSignerList().get(1) : pkcs7Dto.getTimestampedSignerList().get(0);

                        waybillSendDTO.setSign(pkcs7Dto.getPkcs7b64());
                        if (WaybillStatus.ConsigneeAccepted == status) {
                            facturaClient.acceptWaybill(SecurityUtils.getCurrentRequestLanguageEnum().name(), getCurrentCompanyTin(), waybillSendDTO);
                            waybill.setStatus(WaybillStatus.ConsigneeAccepted);
                        } else {
                            facturaClient.rejectWaybill(SecurityUtils.getCurrentRequestLanguageEnum().name(), getCurrentCompanyTin(), waybillSendDTO);
                            waybill.setStatus(WaybillStatus.ConsigneeRejected);
                        }

                        waybill.setNotes(waybillSendDTO.getNotes());
                        waybillRepository.save(waybill);
                        WaybillExecutor waybillExecutor = new WaybillExecutor();
                        waybillExecutor.setWaybillId(waybill.getId());
                        waybillExecutor.setStatus(waybill.getStatus());
                        waybillExecutor.setOperationNumber(String.valueOf(Integer.parseInt(certificateDto.getSerialNumber(), 16)));
                        waybillExecutor.setOperationBy(certificateDto.getSubjectName().getFullName());
                        waybillExecutor.setOperationDate(LocalDateTime.now());
                        waybillExecutorRepository.save(waybillExecutor);
                    } else {
                        validationService.badRequestExceptionThrow("Not verified, reason: " + pkcs7Dto.getMessage()).get();
                    }
                    return waybill;
                })
                .orElseThrow(() -> validationService.badRequestExceptionThrow("ТТЮ не найдена").get());

    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        waybillRepository.findById(id).map(waybill -> {
            this.validateCompany(waybill.getCompanyId());
            if (WaybillStatus.DRAFT != waybill.getStatus()) {
                throw validationService.badRequestExceptionThrow("Вы можете удалить ТТЮ с статусом Черновик").get();
            }
            waybill.setDeleted(true);
            waybillRepository.save(waybill);
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Waybill.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long convertFromFactura(Long id) {
        return facturaRepository.findById(id).map(factura -> {
            if (!SecurityUtils.isFromAdmin() &&
                    !(
                            factura.getSellerId().equals(getCurrentCompanyId()) ||
                                    factura.getBuyerId().equals(getCurrentCompanyId())
                    )
            ) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ACCESS_DENIED), localizationService.localize(MessageConstants.CANNOT_UPDATE_OTHER_COMPANY_DATA)).get();
            }
            SupplyFacturaDTO supplyFacturaDTO = factura.toDTO();
            if (Status.DRAFT.equals(factura.getStatus())) {
                supplyFacturaDTO.setFacturaDTO(factura.toFacturaDTO(true));
            } else if (StringUtils.hasText(factura.getSign())) {
                supplyFacturaDTO.setFacturaDTO(facturaService.convertFromSign(factura.getSign()));
            }
            if (supplyFacturaDTO.getFacturaDTO() == null || supplyFacturaDTO.getFacturaDTO().getFacturaDoc() == null) {
                supplyFacturaDTO.setFacturaDTO(factura.toFacturaDTO());
            }

            //waybill
            FacturaDTO facturaDTO = supplyFacturaDTO.getFacturaDTO();
            if (facturaDTO != null) {
                WaybillDTO waybillDTO = new WaybillDTO();
                waybillDTO.setFacturaId(supplyFacturaDTO.getId());
                waybillDTO.setWaybillDoc(new WaybillDocDTO(facturaDTO.getFacturaDoc().getFacturaNo(), facturaDTO.getFacturaDoc().getFacturaDate()));
                if (facturaDTO.getContractDoc() != null) {
                    waybillDTO.setContractDoc(new WaybillContractDocDTO(facturaDTO.getContractDoc().getContractNo(), LocalDate.parse(facturaDTO.getContractDoc().getContractDate())));
                }

                //consignor
                WaybillCompanyDTO consignor = new WaybillCompanyDTO();
                consignor.setName(facturaDTO.getSeller().getName());
                consignor.setTinOrPinfl(StringUtils.hasText(facturaDTO.getSellerTin()) ? facturaDTO.getSellerTin() : facturaDTO.getSeller().getTin());
                if (factura.getBranch() != null) {
                    consignor.setBranchName(factura.getBranch().getName());
                    consignor.setBranchCode(factura.getBranch().getBranchNumber());
                }
                waybillDTO.setConsignor(consignor);

                //consignee
                WaybillCompanyDTO consignee = new WaybillCompanyDTO();
                consignee.setTinOrPinfl(StringUtils.hasText(facturaDTO.getBuyerTin()) ? facturaDTO.getBuyerTin() : facturaDTO.getBuyer().getTin());
                consignee.setName(facturaDTO.getBuyer().getName());
                consignee.setBranchCode(facturaDTO.getBuyer().getBranchCode());
                consignee.setBranchName(facturaDTO.getBuyer().getBranchName());
                waybillDTO.setConsignee(consignee);

                if (facturaDTO.getProductList() != null && !CollectionUtils.isEmpty(facturaDTO.getProductList().getProducts())) {
                    WaybillRoadWayTransportDTO waybillRoadWayTransportDTO = new WaybillRoadWayTransportDTO();
                    WaybillProductGroupDTO productGroupDTO = new WaybillProductGroupDTO();
                    BigDecimal totalWeightBrutto = BigDecimal.ZERO;
                    if (factura.getBranch() != null && factura.getBranch().getAddress() != null) {
                        WaybillPointDTO loadingPoint = new WaybillPointDTO();
                        if (factura.getBranch().getAddress().getRegion() != null) {
                            loadingPoint.setRegionId(factura.getBranch().getAddress().getRegion().getCode().intValue());
                            loadingPoint.setRegionName(factura.getBranch().getAddress().getRegion().getName());
                        }
                        if (factura.getBranch().getAddress().getDistrict() != null) {
                            loadingPoint.setDistrictCode(factura.getBranch().getAddress().getDistrict().getCode().intValue());
                            loadingPoint.setDistrictName(factura.getBranch().getAddress().getDistrict().getName());
                        }
                        if (factura.getBranch().getAddress().getLongitude() != null && factura.getBranch().getAddress().getLatitude() != null) {
                            loadingPoint.setLongitude(Double.valueOf(factura.getBranch().getAddress().getLongitude()));
                            loadingPoint.setLatitude(Double.valueOf(factura.getBranch().getAddress().getLatitude()));
                        }
                        loadingPoint.setAddress(factura.getBranch().getAddress().getFullAddress());
                        productGroupDTO.setLoadingPoint(loadingPoint);
                    }
                    WaybillProductInfoDTO productInfoDTO = new WaybillProductInfoDTO();
                    productInfoDTO.setTotalDeliverySum(facturaDTO.getProductList().getDeliverySumWithVat());
                    for (FacturaProductDTO facturaProduct : facturaDTO.getProductList().getProducts()) {
                        WaybillProductDTO waybillProductDTO = new WaybillProductDTO();
                        waybillProductDTO.setProductId(facturaProduct.getProductId());
                        waybillProductDTO.setBranch(facturaProduct.getBranch());
                        waybillProductDTO.setOrdNo(Integer.valueOf(facturaProduct.getOrdNo()));
                        waybillProductDTO.setCommittentTinOrPinfl(facturaProduct.getCommittentTin());
                        waybillProductDTO.setCommittentName(facturaProduct.getCommittentName());
                        waybillProductDTO.setProductName(facturaProduct.getName());
                        waybillProductDTO.setCatalogName(facturaProduct.getCatalogName());
                        waybillProductDTO.setCatalogCode(facturaProduct.getCatalogCode());
                        waybillProductDTO.setPackageCode(facturaProduct.getPackageCode());
                        waybillProductDTO.setPackageName(facturaProduct.getPackageName());
                        waybillProductDTO.setAmount(facturaProduct.getCount());
                        waybillProductDTO.setPrice(facturaProduct.getSumma());
                        waybillProductDTO.setDeliverySum(facturaProduct.getDeliverySum());
                        waybillProductDTO.setBrutto(facturaProduct.getWeightBrutto());
                        waybillProductDTO.setNetto(facturaProduct.getWeightNetto());
                        waybillProductDTO.setWeightBrutto(facturaProduct.getCount().multiply(Optional.ofNullable(facturaProduct.getWeightBrutto()).orElse(BigDecimal.ZERO)));
                        waybillProductDTO.setWeightNetto(facturaProduct.getCount().multiply(Optional.ofNullable(facturaProduct.getWeightNetto()).orElse(BigDecimal.ZERO)));
                        totalWeightBrutto = totalWeightBrutto.add(waybillProductDTO.getWeightBrutto());
                        productInfoDTO.addProduct(waybillProductDTO);
                    }
                    productInfoDTO.setTotalWeightBrutto(totalWeightBrutto);
                    productGroupDTO.setProductInfoDTO(productInfoDTO);
                    waybillRoadWayTransportDTO.addProductGroup(productGroupDTO);
                    waybillDTO.setRoadway(waybillRoadWayTransportDTO);
                    waybillDTO.setTotalDeliveryCost(facturaDTO.getProductList().getDeliverySumWithVat());
                }
                return this.save(new Waybill(), waybillDTO);
            }
            throw validationService.badRequestExceptionThrow("Parse sign error", "Can not parse factura sign").get();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Factura.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long convertFromAcceptanceTransferAct(Long id) {
        return acceptanceTransferActRepository.getDetail(id).map(acceptanceTransferAct -> {

            Company seller = acceptanceTransferAct.getSeller();
            Company buyer = acceptanceTransferAct.getBuyer();

            WaybillDTO waybillDTO = new WaybillDTO();
            waybillDTO.setShipmentId(acceptanceTransferAct.getId());
            waybillDTO.setWaybillDoc(new WaybillDocDTO(acceptanceTransferAct.getActNo(), acceptanceTransferAct.getActDate()));
            AcceptanceTransferActDetail acceptanceTransferActDetail = acceptanceTransferAct.getAcceptanceTransferActDetail();
            if (acceptanceTransferActDetail != null && StringUtils.hasText(acceptanceTransferActDetail.getContractNo())) {
                waybillDTO.setContractDoc(new WaybillContractDocDTO(acceptanceTransferActDetail.getContractNo(), acceptanceTransferActDetail.getContractDate()));
            }
            //consignor
            WaybillCompanyDTO consignor = new WaybillCompanyDTO();
            consignor.setName(seller.getName());
            consignor.setTinOrPinfl(seller.getTin());
            waybillDTO.setConsignor(consignor);

            //consignee
            WaybillCompanyDTO consignee = new WaybillCompanyDTO();
            consignee.setTinOrPinfl(buyer.getTin());
            consignee.setName(buyer.getName());
            if (acceptanceTransferActDetail != null) {
                if (StringUtils.hasText(acceptanceTransferActDetail.getContractNo()) && acceptanceTransferActDetail.getContractDate() != null) {
                    waybillDTO.setContractDoc(new WaybillContractDocDTO(acceptanceTransferActDetail.getContractNo(), acceptanceTransferActDetail.getContractDate()));
                }
                consignee.setBranchCode(acceptanceTransferActDetail.getBuyerBranchCode());
                consignee.setBranchName(acceptanceTransferActDetail.getBuyerBranchName());
            }
            waybillDTO.setConsignee(consignee);

            if (acceptanceTransferActDetail != null && !CollectionUtils.isEmpty(acceptanceTransferActDetail.getProducts())) {
                WaybillRoadWayTransportDTO waybillRoadWayTransportDTO = new WaybillRoadWayTransportDTO();
                WaybillProductGroupDTO productGroupDTO = new WaybillProductGroupDTO();
                WaybillProductInfoDTO productInfoDTO = new WaybillProductInfoDTO();
                productInfoDTO.setTotalDeliverySum(acceptanceTransferAct.getTotalPrice());
                for (AcceptanceTransferActProduct acceptanceTransferActProduct : acceptanceTransferActDetail.getProducts()) {
                    WaybillProductDTO waybillProductDTO = new WaybillProductDTO();
                    waybillProductDTO.setProductId(acceptanceTransferActProduct.getProductId());
                    waybillProductDTO.setOrdNo(acceptanceTransferActProduct.getOrdNo());
                    if (acceptanceTransferActProduct.getProduct() != null) {
                        if (acceptanceTransferActProduct.getProduct().getBranch() != null) {
                            waybillProductDTO.setBranch(acceptanceTransferActProduct.getProduct().getBranch().toCommonDTO());
                        }
                        waybillProductDTO.setBrutto(acceptanceTransferActProduct.getProduct().getWeightBrutto());
                        waybillProductDTO.setNetto(acceptanceTransferActProduct.getProduct().getWeightNetto());
                        waybillProductDTO.setWeightBrutto(acceptanceTransferActProduct.getQty().multiply(Optional.ofNullable(acceptanceTransferActProduct.getProduct().getWeightBrutto()).orElse(BigDecimal.ZERO)));
                        waybillProductDTO.setWeightNetto(acceptanceTransferActProduct.getQty().multiply(Optional.ofNullable(acceptanceTransferActProduct.getProduct().getWeightNetto()).orElse(BigDecimal.ZERO)));
                        if (acceptanceTransferActProduct.getProduct().getCompanyCommittent() != null) {
                            waybillProductDTO.setCommittentTinOrPinfl(acceptanceTransferActProduct.getProduct().getCompanyCommittent().getCommittent().getTin());
                            waybillProductDTO.setCommittentName(acceptanceTransferActProduct.getProduct().getCompanyCommittent().getCommittent().getName());
                        }
                    }
                    waybillProductDTO.setProductName(acceptanceTransferActProduct.getName());
                    waybillProductDTO.setCatalogName(acceptanceTransferActProduct.getCatalogName());
                    waybillProductDTO.setCatalogCode(acceptanceTransferActProduct.getCatalogCode());
                    waybillProductDTO.setPackageCode(acceptanceTransferActProduct.getPackageCode());
                    waybillProductDTO.setPackageName(acceptanceTransferActProduct.getPackageName());
                    waybillProductDTO.setAmount(acceptanceTransferActProduct.getQty());
                    waybillProductDTO.setPrice(acceptanceTransferActProduct.getPrice());
                    waybillProductDTO.setDeliverySum(acceptanceTransferActProduct.getDeliverySum());
                    productInfoDTO.addProduct(waybillProductDTO);
                }
                productGroupDTO.setProductInfoDTO(productInfoDTO);
                waybillRoadWayTransportDTO.addProductGroup(productGroupDTO);
                waybillDTO.setRoadway(waybillRoadWayTransportDTO);
                waybillDTO.setTotalDeliveryCost(acceptanceTransferAct.getTotalPrice());
            }

            return this.save(new Waybill(), waybillDTO);
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(AcceptanceTransferAct.class.getSimpleName(), "id", id));
    }

    public WaybillDriverTransportDTO[] getDriverTransports(String tin) {
        return facturaClient.getDriverTransports(tin);
    }

    public List<TransportCheckResponseDTO> getTransports(TransportCheckRequestDTO transportCheckRequestDTO) {
        if (CollectionUtils.isEmpty(transportCheckRequestDTO.getTinOrPinfls()) || CollectionUtils.isEmpty(transportCheckRequestDTO.getRegNumbers())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.FILL_IN_ALL_REQUIRED_FIELDS)).get();
        }
        transportCheckRequestDTO.setDate(LocalDateTime.now().format(DateUtils.YEAR_MONTH_DAY_TIMEZONE_MILLIS_FORMATTER));
        List<TransportCheckResponseDTO> transports = List.of(facturaClient.getTransports(transportCheckRequestDTO));

        Long companyId = getCurrentCompanyId();
        Long userId = getCurrentUserId();
        for (TransportCheckResponseDTO transport : transports) {

            recordFieldHistoryUsage(CompanyFieldHistoryDTO.builder()
                    .companyId(companyId)
                    .userId(userId)
                    .fieldType(FieldHistoryType.TRANSPORT)
                    .identifier(transport.getRegNo())
                    .displayValue(transport.getModel())
                    .extraData(CompanyFieldHistoryService.buildTransportExtraData(transport.getTransportType(), transport.getOwnershipType(), transportCheckRequestDTO.getTinOrPinfls().getFirst()))
                    .build());
        }
        return transports;
    }

    public WaybillEmpowermentDTO[] getCompanyEmpowerments(String tin) {
        return facturaClient.getCompanyEmpowerments(tin);
    }

    @Transactional
    public Long convertFromStockTransfer(Long stockTransferId) {
        return stockTransferRepository.getDetail(stockTransferId).map(stockTransfer -> {
            Long waybillId = null;
            if (!CollectionUtils.isEmpty(stockTransfer.getProducts())) {
                Map<Long, List<StockTransferProduct>> orderProductMap = new LinkedHashMap<>();
                for (StockTransferProduct stockTransferProduct : stockTransfer.getProducts()) {
                    if (stockTransferProduct.getOrder() != null) {
                        orderProductMap.compute(stockTransferProduct.getOrder().getId(), (orderId, existingList) -> {
                            if (existingList != null) {
                                List<StockTransferProduct> modifiableList = new ArrayList<>(existingList);
                                modifiableList.add(stockTransferProduct);
                                return modifiableList;
                            }
                            return new ArrayList<>(List.of(stockTransferProduct));
                        });
                    }
                }
                for (Map.Entry<Long, List<StockTransferProduct>> entry : orderProductMap.entrySet()) {
                    Long orderId = entry.getKey();
                    List<StockTransferProduct> stockTransferProducts = entry.getValue();
                    //waybill
                    WaybillDTO waybillDTO = new WaybillDTO();
                    waybillDTO.setStockTransferId(stockTransfer.getId());
                    waybillDTO.setDeliveryType(WaybillDeliveryType.PICKUP.getCode());
                    waybillDTO.setWaybillDoc(new WaybillDocDTO(stockTransfer.getTransferNumber(), stockTransfer.getTransferDate().toLocalDate()));
                    Company company = stockTransfer.getCompany();
                    //consignor
                    WaybillCompanyDTO consignor = new WaybillCompanyDTO();
                    consignor.setName(company.getName());
                    consignor.setTinOrPinfl(company.getTin());
                    if (stockTransfer.getFromBranch() != null) {
                        consignor.setBranchName(stockTransfer.getFromBranch().getName());
                        consignor.setBranchCode(stockTransfer.getFromBranch().getBranchNumber());
                    }
                    waybillDTO.setConsignor(consignor);
                    //expeditor
                    if (stockTransfer.getExpeditor() != null) {
                        waybillDTO.setFreightForwarder(consignor);
                        //carrier
                        waybillDTO.setCarrier(consignor);
                    }
                    if (stockTransfer.getExpeditor() != null) {
                        WaybillPersonDTO responsiblePerson = new WaybillPersonDTO();
                        responsiblePerson.setId(stockTransfer.getExpeditorId());
                        responsiblePerson.setPinfl(stockTransfer.getExpeditor().getPinfl());
                        responsiblePerson.setFullName(stockTransfer.getExpeditor().getFullName());
                        waybillDTO.setResponsiblePerson(responsiblePerson);
                    }

                    //consignee
                    if (orderId != null) {
                        orderRepository.getDetail(orderId).ifPresent(order -> {
                            Company customer = order.getCustomer();
                            WaybillCompanyDTO consignee = new WaybillCompanyDTO();
                            consignee.setName(customer.getName());
                            consignee.setTinOrPinfl(customer.getTin());
                            if (order.getDeliveryBranch() != null) {
                                consignee.setBranchName(Optional.ofNullable(order.getDeliveryBranch().getOfficialName()).orElse(order.getDeliveryBranch().getName()));
                                consignee.setBranchCode(order.getDeliveryBranch().getCustomerBranchNumber());
                            }
                            waybillDTO.setConsignee(consignee);
                            waybillDTO.setDeliveryType(WaybillDeliveryType.DELIVERY.getCode());
                            if (order.getContract() != null) {
                                WaybillContractDocDTO contract = new WaybillContractDocDTO();
                                contract.setContractNo(order.getContract().getContractNumber());
                                contract.setContractDate(order.getContract().getContractDate());
                                waybillDTO.setContractDoc(contract);
                            }
                            WaybillClientDTO client = new WaybillClientDTO();
                            client.setId(order.getCustomer().getId());
                            client.setName(order.getCustomer().getName());
                            client.setTinOrPinfl(order.getCustomer().getTin());
                            if (order.getDeliveryBranch() != null) {
                                client.setBranchName(Optional.ofNullable(order.getDeliveryBranch().getOfficialName()).orElseGet(() -> order.getDeliveryBranch().getName()));
                                client.setBranchCode(order.getDeliveryBranch().getCustomerBranchNumber());
                            }
                            if (order.getContract() != null) {
                                client.setContractNo(order.getContract().getContractNumber());
                                client.setContractDate(order.getContract().getContractDate());
                            }
                            waybillDTO.setClient(client);

                            WaybillClientDTO payer = new WaybillClientDTO();
                            payer.setId(order.getCustomer().getId());
                            payer.setName(order.getCustomer().getName());
                            payer.setTinOrPinfl(order.getCustomer().getTin());
                            if (order.getDeliveryBranch() != null) {
                                payer.setBranchName(Optional.ofNullable(order.getDeliveryBranch().getOfficialName()).orElseGet(() -> order.getDeliveryBranch().getName()));
                                payer.setBranchCode(order.getDeliveryBranch().getCustomerBranchNumber());
                            }
                            if (order.getContract() != null) {
                                payer.setContractNo(order.getContract().getContractNumber());
                                payer.setContractDate(order.getContract().getContractDate());
                            }
                            waybillDTO.setPayer(payer);
                        });

                    }
                    BigDecimal totalSum = BigDecimal.ZERO;
                    WaybillRoadWayTransportDTO waybillRoadWayTransportDTO = new WaybillRoadWayTransportDTO();
                    waybillRoadWayTransportDTO.setTransportType(TransportType.Roadway);
                    if (StringUtils.hasText(stockTransfer.getToWarehouse().getTrailerModel()) && StringUtils.hasText(stockTransfer.getToWarehouse().getTrailerRegNo())) {
                        waybillRoadWayTransportDTO.setTrailer(new WaybillTruckDTO(stockTransfer.getToWarehouse().getTrailerRegNo(), stockTransfer.getToWarehouse().getTrailerModel()));
                    }
                    if (StringUtils.hasText(stockTransfer.getToWarehouse().getTruckRegNo()) && StringUtils.hasText(stockTransfer.getToWarehouse().getTruckModel())) {
                        waybillRoadWayTransportDTO.setTruckDTO(new WaybillTruckDTO(stockTransfer.getToWarehouse().getTruckRegNo(), stockTransfer.getToWarehouse().getTruckModel()));
                    }
                    WaybillProductGroupDTO productGroupDTO = new WaybillProductGroupDTO();
                    if (stockTransfer.getExpeditor() != null) {
                        //loading responsible person
                        WaybillPersonDTO loadingTrustee = new WaybillPersonDTO();
                        loadingTrustee.setId(stockTransfer.getExpeditorId());
                        loadingTrustee.setPinfl(stockTransfer.getExpeditor().getPinfl());
                        loadingTrustee.setFullName(stockTransfer.getExpeditor().getFullName());
                        productGroupDTO.setLoadingTrustee(loadingTrustee);

                        //unloading responsible person
                        WaybillPersonDTO unLoadingTrustee = new WaybillPersonDTO();
                        loadingTrustee.setId(stockTransfer.getExpeditorId());
                        loadingTrustee.setPinfl(stockTransfer.getExpeditor().getPinfl());
                        loadingTrustee.setFullName(stockTransfer.getExpeditor().getFullName());
                        productGroupDTO.setUnloadingTrustee(unLoadingTrustee);
                    }
                    if (orderId != null) {
                        orderRepository.findById(orderId).ifPresent(order -> {
                            if (order.getDeliveryBranch() != null && order.getDeliveryBranch().getAddress() != null) {
                                WaybillPointDTO unloadingPoint = new WaybillPointDTO();
                                if (order.getDeliveryBranch().getAddress().getRegion() != null) {
                                    unloadingPoint.setRegionId(order.getDeliveryBranch().getAddress().getRegion().getCode().intValue());
                                    unloadingPoint.setRegionName(order.getDeliveryBranch().getAddress().getRegion().getName());
                                }
                                if (order.getDeliveryBranch().getAddress().getDistrict() != null) {
                                    unloadingPoint.setDistrictCode(order.getDeliveryBranch().getAddress().getDistrict().getCode().intValue());
                                    unloadingPoint.setDistrictName(order.getDeliveryBranch().getAddress().getDistrict().getName());
                                }
                                if (order.getDeliveryBranch().getAddress().getLongitude() != null && order.getDeliveryBranch().getAddress().getLatitude() != null) {
                                    unloadingPoint.setLongitude(Double.valueOf(order.getDeliveryBranch().getAddress().getLongitude()));
                                    unloadingPoint.setLatitude(Double.valueOf(order.getDeliveryBranch().getAddress().getLatitude()));
                                }
                                unloadingPoint.setAddress(order.getDeliveryBranch().getAddress().getFullAddress());
                                productGroupDTO.setUnloadingPoint(unloadingPoint);
                            }
                        });
                        if (stockTransfer.getFromBranch() != null && stockTransfer.getFromBranch().getAddress() != null) {
                            WaybillPointDTO loadingPoint = new WaybillPointDTO();
                            if (stockTransfer.getFromBranch().getAddress().getRegion() != null) {
                                loadingPoint.setRegionId(stockTransfer.getFromBranch().getAddress().getRegion().getCode().intValue());
                                loadingPoint.setRegionName(stockTransfer.getFromBranch().getAddress().getRegion().getName());
                            }
                            if (stockTransfer.getFromBranch().getAddress().getDistrict() != null) {
                                loadingPoint.setDistrictCode(stockTransfer.getFromBranch().getAddress().getDistrict().getCode().intValue());
                                loadingPoint.setDistrictName(stockTransfer.getFromBranch().getAddress().getDistrict().getName());
                            }
                            if (stockTransfer.getFromBranch().getAddress().getLongitude() != null && stockTransfer.getFromBranch().getAddress().getLatitude() != null) {
                                loadingPoint.setLongitude(Double.valueOf(stockTransfer.getFromBranch().getAddress().getLongitude()));
                                loadingPoint.setLatitude(Double.valueOf(stockTransfer.getFromBranch().getAddress().getLatitude()));
                            }
                            loadingPoint.setAddress(stockTransfer.getFromBranch().getAddress().getFullAddress());
                            productGroupDTO.setLoadingPoint(loadingPoint);
                        }
                    } else {
                        if (stockTransfer.getFromBranch() != null && stockTransfer.getFromBranch().getAddress() != null) {
                            WaybillPointDTO loadingPoint = new WaybillPointDTO();
                            if (stockTransfer.getFromBranch().getAddress().getRegion() != null) {
                                loadingPoint.setRegionId(stockTransfer.getFromBranch().getAddress().getRegion().getCode().intValue());
                                loadingPoint.setRegionName(stockTransfer.getFromBranch().getAddress().getRegion().getName());
                            }
                            if (stockTransfer.getFromBranch().getAddress().getDistrict() != null) {
                                loadingPoint.setDistrictCode(stockTransfer.getFromBranch().getAddress().getDistrict().getCode().intValue());
                                loadingPoint.setDistrictName(stockTransfer.getFromBranch().getAddress().getDistrict().getName());
                            }
                            if (stockTransfer.getFromBranch().getAddress().getLongitude() != null && stockTransfer.getFromBranch().getAddress().getLatitude() != null) {
                                loadingPoint.setLongitude(Double.valueOf(stockTransfer.getFromBranch().getAddress().getLongitude()));
                                loadingPoint.setLatitude(Double.valueOf(stockTransfer.getFromBranch().getAddress().getLatitude()));
                            }
                            loadingPoint.setAddress(stockTransfer.getFromBranch().getAddress().getFullAddress());
                            productGroupDTO.setLoadingPoint(loadingPoint);
                        }
                        if (StockTransferType.W2W == stockTransfer.getStockTransferType() && (stockTransfer.getToBranch() != null && stockTransfer.getToBranch().getAddress() != null)) {
                            WaybillPointDTO unloadingPoint = new WaybillPointDTO();
                            if (stockTransfer.getToBranch().getAddress().getRegion() != null) {
                                unloadingPoint.setRegionId(stockTransfer.getToBranch().getAddress().getRegion().getCode().intValue());
                                unloadingPoint.setRegionName(stockTransfer.getToBranch().getAddress().getRegion().getName());
                            }
                            if (stockTransfer.getToBranch().getAddress().getDistrict() != null) {
                                unloadingPoint.setDistrictCode(stockTransfer.getToBranch().getAddress().getDistrict().getCode().intValue());
                                unloadingPoint.setDistrictName(stockTransfer.getToBranch().getAddress().getDistrict().getName());
                            }
                            if (stockTransfer.getToBranch().getAddress().getLongitude() != null && stockTransfer.getToBranch().getAddress().getLatitude() != null) {
                                unloadingPoint.setLongitude(Double.valueOf(stockTransfer.getToBranch().getAddress().getLongitude()));
                                unloadingPoint.setLatitude(Double.valueOf(stockTransfer.getToBranch().getAddress().getLatitude()));
                            }
                            unloadingPoint.setAddress(stockTransfer.getToBranch().getAddress().getFullAddress());
                            productGroupDTO.setUnloadingPoint(unloadingPoint);
                        }
                    }
                    WaybillProductInfoDTO productInfoDTO = new WaybillProductInfoDTO();
                    BigDecimal totalDeliverySum = BigDecimal.ZERO;
                    for (StockTransferProduct stockTransferProduct : stockTransferProducts) {
                        WaybillProductDTO waybillProductDTO = new WaybillProductDTO();
                        waybillProductDTO.setProductId(stockTransferProduct.getFromProductId());
                        waybillProductDTO.setBranch(stockTransfer.getFromBranch().toCommonDTO());
                        if (stockTransferProduct.getFromProduct().getCompanyCommittent() != null) {
                            waybillProductDTO.setCommittentTinOrPinfl(stockTransferProduct.getFromProduct().getCompanyCommittent().getCommittent().getTin());
                            waybillProductDTO.setCommittentName(stockTransferProduct.getFromProduct().getCompanyCommittent().getCommittent().getName());
                        }
                        waybillProductDTO.setProductName(stockTransferProduct.getFromProduct().getName());
                        waybillProductDTO.setCatalogName(stockTransferProduct.getFromProduct().getCatalogName());
                        waybillProductDTO.setCatalogCode(stockTransferProduct.getFromProduct().getCatalogCode());
                        waybillProductDTO.setPackageCode(stockTransferProduct.getFromProduct().getPackageCode());
                        waybillProductDTO.setPackageName(stockTransferProduct.getFromProduct().getPackageName());
                        waybillProductDTO.setAmount(stockTransferProduct.getQty());
                        if (stockTransferProduct.getFromProduct().getPrice() != null) {
                            totalSum = totalSum.add(stockTransferProduct.getFromProduct().getPrice().multiply(stockTransferProduct.getQty()));
                            waybillProductDTO.setPrice(stockTransferProduct.getFromProduct().getPrice().multiply(stockTransferProduct.getQty()));
                            waybillProductDTO.setDeliverySum(stockTransferProduct.getFromProduct().getPrice().multiply(stockTransferProduct.getQty()));
                        }
                        waybillProductDTO.setBrutto(stockTransferProduct.getFromProduct().getWeightBrutto());
                        waybillProductDTO.setNetto(stockTransferProduct.getFromProduct().getWeightNetto());
                        waybillProductDTO.setWeightBrutto(stockTransferProduct.getQty().multiply(Optional.ofNullable(stockTransferProduct.getFromProduct().getWeightBrutto()).orElse(BigDecimal.ZERO)));
                        waybillProductDTO.setWeightNetto(stockTransferProduct.getQty().multiply(Optional.ofNullable(stockTransferProduct.getFromProduct().getWeightNetto()).orElse(BigDecimal.ZERO)));
                        totalDeliverySum = totalDeliverySum.add(Optional.ofNullable(stockTransferProduct.getTotalPrice()).orElse(BigDecimal.ZERO));
                        productInfoDTO.addProduct(waybillProductDTO);
                    }
                    productInfoDTO.setTotalDeliverySum(totalDeliverySum);
                    productGroupDTO.setProductInfoDTO(productInfoDTO);
                    waybillRoadWayTransportDTO.addProductGroup(productGroupDTO);
                    waybillDTO.setRoadway(waybillRoadWayTransportDTO);
                    waybillDTO.setTotalDeliveryCost(totalSum);
                    waybillDTO.setStockTransferId(stockTransferId);
                    waybillId = this.save(new Waybill(), waybillDTO);
                }
            }
            return waybillId;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(StockTransfer.class.getSimpleName(), "id", stockTransferId));
    }

    @Transactional
    public Long convertFromStockOut(Long stockOutId) {
        return stockOutRepository.getDetail(stockOutId).map(stock -> {
            WaybillDTO waybillDTO = new WaybillDTO();
            waybillDTO.setDeliveryType(2);
            waybillDTO.setWaybillLocalType(0);
            //waybill-doc
            WaybillDocDTO waybillDocDTO = new WaybillDocDTO();
            waybillDocDTO.setWaybillNo(stock.getStockNumber());
            waybillDocDTO.setWaybillDate(stock.getTransferDate().toLocalDate());
            waybillDTO.setWaybillDoc(waybillDocDTO);

            //contract-doc
            if (stock.getContract() != null) {
                WaybillContractDocDTO contractDocDTO = new WaybillContractDocDTO();
                contractDocDTO.setContractNo(stock.getContract().getContractNumber());
                contractDocDTO.setContractDate(stock.getContract().getContractDate());
            }

            //responsible-person
            WaybillPersonDTO waybillPersonDTO = new WaybillPersonDTO();
            User responsiblePerson;
            if (stock.getOrder() != null && stock.getOrder().getExpeditor() != null) {
                responsiblePerson = stock.getOrder().getExpeditor();
            } else {
                responsiblePerson = stock.getCreator();
            }
            waybillPersonDTO.setId(responsiblePerson.getId());
            waybillPersonDTO.setFullName(responsiblePerson.getFullName());
            waybillPersonDTO.setPinfl(responsiblePerson.getPinfl());
            waybillDTO.setResponsiblePerson(waybillPersonDTO);

            //consignor
            WaybillCompanyDTO consignor = new WaybillCompanyDTO();
            consignor.setTinOrPinfl(stock.getSupplier().getTin());
            consignor.setName(stock.getSupplier().getName());
            if (stock.getBranch() != null) {
                consignor.setBranchCode(stock.getBranch().getName());
                consignor.setBranchName(stock.getBranch().getBranchNumber());
            }
            waybillDTO.setConsignor(consignor);

            //consignee
            WaybillCompanyDTO consignee = new WaybillCompanyDTO();
            consignee.setTinOrPinfl(stock.getSupplier().getTin());
            consignee.setName(stock.getSupplier().getName());
            if (stock.getDeliveryBranch() != null) {
                consignee.setBranchCode(stock.getDeliveryBranch().getCustomerBranchNumber());
                consignee.setBranchName(stock.getDeliveryBranch().getOfficialName());
            }
            waybillDTO.setConsignor(consignee);

            //freight-forwarder
            waybillDTO.setFreightForwarder(consignor);

            //carrier
            waybillDTO.setCarrier(consignor);
            if (stock.getVehicle() != null && StringUtils.hasText(stock.getVehicle().getTransportCompanyTin())) {
                WaybillCompanyDTO carrier = new WaybillCompanyDTO();
                carrier.setTinOrPinfl(stock.getVehicle().getTransportCompanyTin());
                carrier.setName(stock.getVehicle().getTransportCompanyName());
                waybillDTO.setCarrier(carrier);
            }

            //client
            WaybillClientDTO client = new WaybillClientDTO();
            client.setId(stock.getCustomer().getId());
            client.setTinOrPinfl(stock.getCustomer().getTin());
            client.setName(stock.getCustomer().getName());
            client.setContractNo(stock.getContract().getContractNumber());
            client.setContractDate(stock.getContract().getContractDate());
            if (stock.getDeliveryBranch() != null) {
                client.setBranchCode(stock.getDeliveryBranch().getCustomerBranchNumber());
                client.setBranchName(stock.getDeliveryBranch().getOfficialName());
            }
            waybillDTO.setClient(client);

            //payer
            WaybillClientDTO payer = new WaybillClientDTO();
            payer.setId(stock.getCustomer().getId());
            payer.setTinOrPinfl(stock.getCustomer().getTin());
            payer.setName(stock.getCustomer().getName());
            payer.setContractNo(stock.getContract().getContractNumber());
            payer.setContractDate(stock.getContract().getContractDate());
            if (stock.getDeliveryBranch() != null) {
                payer.setBranchCode(stock.getDeliveryBranch().getCustomerBranchNumber());
                payer.setBranchName(stock.getDeliveryBranch().getOfficialName());
            }
            waybillDTO.setPayer(payer);

            waybillDTO.setHasCommittent(stock.getCompanyCommittentId() != null);

            waybillDTO.setTransportType(1);
            waybillDTO.setStockOutId(stock.getId());

            //roadway object
            WaybillRoadWayTransportDTO roadWayDTO = new WaybillRoadWayTransportDTO();
            roadWayDTO.setDriver(waybillDTO.getResponsiblePerson());
            if (stock.getVehicle() != null) {
                WaybillPersonDTO driver = new WaybillPersonDTO();
                driver.setPinfl(stock.getVehicle().getOwnerPinfl());
                driver.setFullName(stock.getVehicle().getOwnerFullName());
                roadWayDTO.setDriver(driver);
                WaybillTruckDTO truck = new WaybillTruckDTO();
                truck.setRegNo(stock.getVehicle().getRegNo());
                truck.setModel(stock.getVehicle().getModel());
                roadWayDTO.setTruckDTO(truck);
                if (StringUtils.hasText(stock.getVehicle().getTrailerRegNo()) && StringUtils.hasText(stock.getVehicle().getTrailerModel())) {
                    WaybillTruckDTO trailer = new WaybillTruckDTO();
                    trailer.setRegNo(stock.getVehicle().getTrailerRegNo());
                    trailer.setModel(stock.getVehicle().getTrailerModel());
                    roadWayDTO.setTrailerType(stock.getVehicle().getTrailerType());
                    roadWayDTO.setTruckDTO(trailer);
                }
            } else if (stock.getWarehouse() != null) {
                if (StringUtils.hasText(stock.getWarehouse().getTruckModel()) && StringUtils.hasText(stock.getWarehouse().getTruckRegNo())) {
                    WaybillTruckDTO truck = new WaybillTruckDTO();
                    truck.setModel(stock.getWarehouse().getTruckModel());
                    truck.setRegNo(stock.getWarehouse().getTruckRegNo());
                    roadWayDTO.setTruckDTO(truck);
                    if (StringUtils.hasText(stock.getWarehouse().getTrailerModel()) && StringUtils.hasText(stock.getWarehouse().getTrailerRegNo())) {
                        WaybillTruckDTO trailer = new WaybillTruckDTO();
                        trailer.setModel(stock.getWarehouse().getTrailerModel());
                        trailer.setRegNo(stock.getWarehouse().getTrailerRegNo());
                        roadWayDTO.setTrailer(trailer);
                    }
                    roadWayDTO.setTrailerType(stock.getWarehouse().getTrailerType());
                    //carriages
                }
            }
            //product-group
            WaybillProductGroupDTO productGroupDTO = new WaybillProductGroupDTO();
            productGroupDTO.setLoadingTrustee(waybillDTO.getResponsiblePerson());
            //loading-point
            if (stock.getBranchId() != null && stock.getBranch().getAddress() != null && stock.getBranch().getAddress().getRegionId() != null && stock.getBranch().getAddress().getDistrictId() != null) {
                WaybillPointDTO loadingPoint = new WaybillPointDTO();
                loadingPoint.setSupplyRegionId(stock.getBranch().getAddress().getRegionId());
                loadingPoint.setRegionId(stock.getBranch().getAddress().getRegion().getCode().intValue());
                loadingPoint.setRegionName(stock.getBranch().getAddress().getRegion().getName());
                loadingPoint.setDistrictCode(stock.getBranch().getAddress().getDistrict().getCode().intValue());
                loadingPoint.setDistrictName(stock.getBranch().getAddress().getDistrict().getName());
                loadingPoint.setAddress(stock.getBranch().getAddress().getFullAddress());
                if (StringUtils.hasText(stock.getBranch().getAddress().getLongitude()) && StringUtils.hasText(stock.getBranch().getAddress().getLatitude())) {
                    loadingPoint.setLongitude(Double.valueOf(stock.getBranch().getAddress().getLongitude()));
                    loadingPoint.setLatitude(Double.valueOf(stock.getBranch().getAddress().getLatitude()));
                }
                productGroupDTO.setLoadingPoint(loadingPoint);
            }
            //unloading-point
            if (stock.getDeliveryBranch() != null && stock.getDeliveryBranch().getAddress() != null && stock.getDeliveryBranch().getAddress().getRegion() != null && stock.getDeliveryBranch().getAddress().getDistrictId() != null) {
                WaybillPointDTO unloadingPoint = new WaybillPointDTO();
                unloadingPoint.setSupplyRegionId(stock.getDeliveryBranch().getAddress().getRegionId());
                unloadingPoint.setRegionId(stock.getDeliveryBranch().getAddress().getRegion().getCode().intValue());
                unloadingPoint.setRegionName(stock.getDeliveryBranch().getAddress().getRegion().getName());
                unloadingPoint.setDistrictCode(stock.getDeliveryBranch().getAddress().getDistrict().getCode().intValue());
                unloadingPoint.setDistrictName(stock.getDeliveryBranch().getAddress().getDistrict().getName());
                unloadingPoint.setAddress(stock.getDeliveryBranch().getAddress().getFullAddress());
                if (StringUtils.hasText(stock.getDeliveryBranch().getAddress().getLongitude()) && StringUtils.hasText(stock.getDeliveryBranch().getAddress().getLatitude())) {
                    unloadingPoint.setLongitude(Double.valueOf(stock.getDeliveryBranch().getAddress().getLongitude()));
                    unloadingPoint.setLatitude(Double.valueOf(stock.getDeliveryBranch().getAddress().getLatitude()));
                }
                productGroupDTO.setUnloadingPoint(unloadingPoint);
            }
            if (CollectionUtils.isEmpty(stock.getProducts())) {
                WaybillProductInfoDTO productInfo = new WaybillProductInfoDTO();
                productInfo.setTotalDeliverySum(stock.getTotalPrice());
                productInfo.setTotalWeightBrutto(stock.getTotalBrutto());
                int ordNo = 0;
                for (StockProduct stockProduct : stock.getProducts()) {
                    WaybillProductDTO productDTO = new WaybillProductDTO();
                    productDTO.setProductId(stockProduct.getProductId());
                    productDTO.setBranch(stockProduct.getProduct().getBranch().toCommonDTO());
                    productDTO.setBrutto(stockProduct.getProduct().getWeightBrutto());
                    productDTO.setNetto(stockProduct.getProduct().getWeightNetto());
                    productDTO.setOrdNo(ordNo++);
                    if (stockProduct.getCompanyCommittentId() != null) {
                        productDTO.setCommittentTinOrPinfl(stockProduct.getCompanyCommittent().getCommittent().getTin());
                        productDTO.setCommittentName(stockProduct.getCompanyCommittent().getCommittent().getName());
                    }
                    productDTO.setProductName(stockProduct.getProduct().getName());
                    productDTO.setCatalogCode(stockProduct.getProduct().getCatalogCode());
                    productDTO.setCatalogName(stockProduct.getProduct().getCatalogName());
                    productDTO.setPackageCode(stockProduct.getProduct().getPackageCode());
                    productDTO.setPackageName(stockProduct.getProduct().getPackageName());
                    productDTO.setAmount(stockProduct.getQty());
                    productDTO.setPrice(stockProduct.getSellingPrice());
                    productDTO.setDeliverySum(stockProduct.getTotalSellingPrice());
                    productDTO.setWeightBrutto(stockProduct.getTotalBrutto());
                    productDTO.setWeightNetto(stockProduct.getTotalNetto());
                    productInfo.addProduct(productDTO);
                }
                productGroupDTO.setProductInfoDTO(productInfo);
            }
            roadWayDTO.setProductGroups(List.of(productGroupDTO));
            waybillDTO.setRoadway(roadWayDTO);
            return stock.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Stock.class.getSimpleName(), "id", stockOutId));
    }

    public String getSign(String tinOrPinfl, String waybillLocalId, Integer currentStateId, boolean isConsignor) {
        return waybillWebhookService.getSign(tinOrPinfl, waybillLocalId, currentStateId, isConsignor);
    }

    public WaybillDTO convertFromSign(String sign) {
        return waybillWebhookService.convertFromSign(sign);
    }

    public List<Long> getDraftIds() {
        String consignorTin = SecurityUtils.getCurrentCompanyTin();
        if (!StringUtils.hasText(consignorTin)) {
            return Collections.emptyList();
        }
        return waybillRepository.findAllIdsByStatusAndConsignorTinOrPinfl(WaybillStatus.DRAFT, consignorTin);
    }

    public Page<WaybillDTO> getListTo1C(WaybillFilter filter) {
        filter.setSize(1000);
        if (filter.isOutgoing()) {
            filter.setConsignorTin(Optional.ofNullable(filter.getConsignorTin()).orElse(getCurrentCompanyTin()));
        } else {
            filter.setConsigneeTin(Optional.ofNullable(filter.getConsigneeTin()).orElse(getCurrentCompanyTin()));
        }
        ResultList<Waybill> resultList = waybillRepository.getResultList(filter);
        List<WaybillDTO> result = new ArrayList<>();
        int count = 0;
        for (Waybill waybill : resultList.getList()) {
            if (WaybillStatus.DRAFT == waybill.getStatus()) {
                WaybillTransport waybillTransport = waybillTransportRepository.findFirstByWaybillId(waybill.getId()).orElse(null);
                if (waybillTransport != null) {
                    result.add(waybill.toDTO(waybillTransport).getWaybillDTO());
                } else {
                    count++;
                }
            } else if (org.apache.commons.lang3.StringUtils.isNotEmpty(waybill.getSignatureContent())) {
                WaybillDTO waybillDTO = convertFromSign(waybill.getSignatureContent());
                if (waybillDTO != null) {
                    result.add(waybillDTO);
                } else {
                    count++;
                }
            }
        }
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount() - count);
    }

    private void recordFieldHistory(Waybill waybill, WaybillPersonDTO driver) {
        Long companyId = waybill.getCompanyId();
        Long userId = getCurrentUserId();

        recordFieldHistoryUsage(CompanyFieldHistoryDTO.builder()
                .companyId(companyId)
                .userId(userId)
                .fieldType(FieldHistoryType.EXPEDITOR)
                .identifier(waybill.getFreightForwarderTinOrPinfl())
                .displayValue(waybill.getFreightForwarderName())
                .extraData(null)
                .build());

        if (driver != null) {
            recordFieldHistoryUsage(CompanyFieldHistoryDTO.builder()
                    .companyId(companyId)
                    .userId(userId)
                    .fieldType(FieldHistoryType.DRIVER)
                    .identifier(driver.getPinfl())
                    .displayValue(driver.getFullName())
                    .extraData(null)
                    .build());
        }

        recordFieldHistoryUsage(CompanyFieldHistoryDTO.builder()
                .companyId(companyId)
                .userId(userId)
                .fieldType(FieldHistoryType.RESPONSIBLE_PERSON)
                .identifier(waybill.getResponsiblePersonPinfl())
                .displayValue(waybill.getResponsiblePersonFullName())
                .extraData(null)
                .build());
    }

    private void recordFieldHistoryUsage(CompanyFieldHistoryDTO companyFieldHistoryDTO) {
        try {
            companyFieldHistoryService.create(companyFieldHistoryDTO);
        } catch (Exception e) {
            log.warn(
                    "Cannot record field history, companyId: {}, type: {}, identifier: {}",
                    companyFieldHistoryDTO.getCompanyId(),
                    companyFieldHistoryDTO.getFieldType(),
                    companyFieldHistoryDTO.getIdentifier(),
                    e
            );
        }
    }

}
