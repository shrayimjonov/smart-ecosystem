package uz.uzkassa.silen.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import uz.uzkassa.silen.domain.Carriages;
import uz.uzkassa.silen.domain.*;
import uz.uzkassa.silen.dto.WaybillListDTO;
import uz.uzkassa.silen.dto.callback.WaybillCallback;
import uz.uzkassa.silen.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.silen.dto.eimzo.SignatureCertificateDto;
import uz.uzkassa.silen.dto.filter.CustomerFilter;
import uz.uzkassa.silen.dto.filter.WaybillFilter;
import uz.uzkassa.silen.dto.invoice.FacturaDTO;
import uz.uzkassa.silen.dto.invoice.FacturaProductDTO;
import uz.uzkassa.silen.dto.invoice.ResponseDTO;
import uz.uzkassa.silen.dto.waybill.*;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.EimzoClient;
import uz.uzkassa.silen.integration.FacturaClient;
import uz.uzkassa.silen.integration.SoliqClient;
import uz.uzkassa.silen.repository.pg.*;
import uz.uzkassa.silen.rest.integration._1c.dto.Waybill1CDTO;
import uz.uzkassa.silen.security.SecurityUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaybillService extends BaseService {

    private final WaybillRepository waybillRepository;
    private final WaybillProductRepository waybillProductRepository;
    private final WaybillProductGroupRepository waybillProductGroupRepository;
    private final CustomerRepository customerRepository;
    private final FacturaClient facturaClient;
    private final EimzoClient eimzoClient;
    private final WaybillExecutorRepository waybillExecutorRepository;
    private final ListInvoiceRepository listInvoiceRepository;
    private final AcceptanceTransferActRepository acceptanceTransferActRepository;
    private final OrganizationPermissionRepository permissionRepository;
    private final SoliqClient soliqClient;
    private final ProductRepository productRepository;
    private final ExciseRepository exciseRepository;

    @Transactional
    public String create(final WaybillDTO waybillDTO) {
        final ResponseDTO responseDTO = facturaClient.getGuid();
        waybillDTO.setWaybillLocalId(responseDTO != null ? responseDTO.getData() : null);
        return save(waybillDTO);
    }

    @Transactional
    public String update(String id, final WaybillDTO waybillDTO) {
        Waybill waybill = waybillRepository.findById(id).orElseThrow(notFound("ТНН не найден"));
        waybillDTO.setId(id);
        if (waybillDTO.getRoadway().getId() == null) {
            if (waybill.getWaybillTransport() != null) {
                String transportId = waybill.getWaybillTransport().getId();
                waybillDTO.getRoadway().setId(transportId);
            }
        }
        if (waybillDTO.getRailway().getId() == null) {
            if (waybill.getWaybillTransport() != null) {
                String transportId = waybill.getWaybillTransport().getId();
                waybillDTO.getRoadway().setId(transportId);
            }
        }
        return save(waybillDTO);
    }

    public String save(final WaybillDTO waybillDTO) {
        Waybill waybill = convertWaybill(waybillDTO);
        waybill.setOrganizationId(SecurityUtils.getCurrentOrganizationId());
        // Transport type
        final TransportType transportType = TransportType.fromCode(waybillDTO.getTransportType());
        final boolean isRoadWay = TransportType.Roadway.equals(transportType);
        final boolean isRailWay = TransportType.Railway.equals(transportType);
        if (isRoadWay) {
            final RoadwayDTO roadwayDTO = waybillDTO.getRoadway();
            WaybillTransport waybillTransport = roadwayTransport(roadwayDTO);
            waybillTransport.setTransportType(transportType);

            for (ProductGroupDTO groupDTO : roadwayDTO.getProductGroups()) {
                WaybillProductGroups productGroups = convertProductGroup(groupDTO, true);
                waybillTransport.addProductGroups(productGroups);
            }
            waybill.addWaybillTransport(waybillTransport);
        }
        if (isRailWay) {
            final RailwayDTO railwayDTO = waybillDTO.getRailway();
            WaybillTransport waybillTransport = new WaybillTransport();
            waybillTransport.setId(railwayDTO.getId());
            waybillTransport.setTrainCarriageNumber(railwayDTO.getTrainCarriageNumber());
            waybillTransport.setTrainDirection(railwayDTO.getTrainDirection());
            waybillTransport.setTransportType(transportType);

            for (ProductGroupDTO groupDTO : railwayDTO.getProductGroups()) {
                WaybillProductGroups productGroups = convertProductGroup(groupDTO, false);
                waybillTransport.addProductGroups(productGroups);
            }
            waybill.addWaybillTransport(waybillTransport);
        }
        waybillRepository.save(waybill);
        return waybill.getId();
    }

    private WaybillProductGroups convertProductGroup(ProductGroupDTO groupDTO, boolean isRoadWay) {
        WaybillProductGroups productGroups = new WaybillProductGroups();
        productGroups.setId(groupDTO.getId());
        if (groupDTO.getLoadingTrustee() != null) {
            productGroups.setLoadingTrusteePinfl(groupDTO.getLoadingTrustee().getPinfl());
            productGroups.setLoadingTrusteeName(groupDTO.getLoadingTrustee().getFullName());
        } else {
            productGroups.setLoadingTrusteePinfl(null);
            productGroups.setLoadingTrusteeName(null);
        }
        if (groupDTO.getUnloadingTrustee() != null) {
            productGroups.setUnloadingTrusteePinfl(groupDTO.getUnloadingTrustee().getPinfl());
            productGroups.setUnloadingTrusteeName(groupDTO.getUnloadingTrustee().getFullName());
        } else {
            productGroups.setUnloadingTrusteePinfl(null);
            productGroups.setUnloadingTrusteeName(null);
        }
        if (groupDTO.getUnloadingEmpowermentDTO() != null) {
            productGroups.setUnloadingEmpowermentId(groupDTO.getUnloadingEmpowermentDTO().getEmpowermentId());
            productGroups.setUnloadingEmpowermentNo(groupDTO.getUnloadingEmpowermentDTO().getEmpowermentNo());
            productGroups.setUnloadingEmpowermentDateOfExpire(groupDTO.getUnloadingEmpowermentDTO().getEmpowermentDateOfExpire());
            productGroups.setUnloadingEmpowermentDateOfIssue(groupDTO.getUnloadingEmpowermentDTO().getEmpowermentDateOfIssue());
        } else {
            productGroups.setUnloadingEmpowermentId(null);
            productGroups.setUnloadingEmpowermentNo(null);
            productGroups.setUnloadingEmpowermentDateOfExpire(null);
            productGroups.setUnloadingEmpowermentDateOfIssue(null);
        }
        if (isRoadWay) {
            Optional.ofNullable(groupDTO.getLoadingPoint())
                    .ifPresent(loadingPoint -> {
                        productGroups.setLoadingRegionId(loadingPoint.getRegionId());
                        productGroups.setLoadingRegionName(loadingPoint.getRegionName());
                        productGroups.setLoadingDistrictId(loadingPoint.getDistrictCode());
                        productGroups.setLoadingDistrictName(loadingPoint.getDistrictName());
                        productGroups.setLoadingMahallaId(loadingPoint.getMahallaId());
                        productGroups.setLoadingMahallaName(loadingPoint.getMahallaName());
                        productGroups.setLoadingAddress(loadingPoint.getAddress());
                        productGroups.setLoadingLongitude(loadingPoint.getLongitude());
                        productGroups.setLoadingLatitude(loadingPoint.getLatitude());
                    });

            Optional.ofNullable(groupDTO.getUnloadingPoint())
                    .ifPresent(unloadingPoint -> {
                        productGroups.setUnloadingRegionId(unloadingPoint.getRegionId());
                        productGroups.setUnloadingRegionName(unloadingPoint.getRegionName());
                        productGroups.setUnloadingDistrictId(unloadingPoint.getDistrictCode());
                        productGroups.setUnloadingDistrictName(unloadingPoint.getDistrictName());
                        productGroups.setUnloadingMahallaId(unloadingPoint.getMahallaId());
                        productGroups.setUnloadingMahallaName(unloadingPoint.getMahallaName());
                        productGroups.setUnloadingAddress(unloadingPoint.getAddress());
                        productGroups.setUnloadingLongitude(unloadingPoint.getLongitude());
                        productGroups.setUnloadingLatitude(unloadingPoint.getLatitude());

                    });
        } else {
            Optional.ofNullable(groupDTO.getLoadingPoint())
                    .ifPresent(loadingStation -> {
                        productGroups.setLoadingStationId(loadingStation.getStationId());
                        productGroups.setLoadingStationName(loadingStation.getStationName());

                    });
            Optional.ofNullable(groupDTO.getUnloadingPoint())
                    .ifPresent(unloadingStation -> {
                        productGroups.setUnloadingStationId(unloadingStation.getStationId());
                        productGroups.setUnloadingStationName(unloadingStation.getStationName());

                    });

        }

        ProductInfoDTO productInfoDTO = groupDTO.getProductInfoDTO();
        if (productInfoDTO != null) {
            productGroups.setTotalDeliverySum(productInfoDTO.getTotalDeliverySum());
            productGroups.setTotalWeightBrutto(productInfoDTO.getTotalWeightBrutto());
            List<Products> products = productInfoDTO.getProducts();
            if (!CollectionUtils.isEmpty(products)) {
                for (Products product : products) {
                    WaybillProduct waybillProduct = new WaybillProduct();
                    log.info("WaybillProduct {}", waybillProduct);
                    waybillProduct.setId(product.getId());
                    waybillProduct.setOrdNo(product.getOrdNo());
                    waybillProduct.setCommittentTinOrPinfl(product.getCommittentTinOrPinfl());
                    waybillProduct.setCommittentName(product.getCommittentName());
                    waybillProduct.setProductName(product.getProductName());
                    waybillProduct.setCatalogCode(product.getCatalogCode());
                    waybillProduct.setCatalogName(product.getCatalogName());
                    waybillProduct.setPackageCode(product.getPackageCode());
                    waybillProduct.setPackageName(product.getPackageName());
                    waybillProduct.setAmount(product.getAmount());
                    waybillProduct.setPrice(product.getPrice());
                    waybillProduct.setDeliverySum(product.getDeliverySum());
                    waybillProduct.setWeightNetto(product.getWeightNetto());
                    waybillProduct.setWeightBrutto(product.getWeightBrutto());
                    waybillProduct.setProductId(product.getProductId());
                    productGroups.addProduct(waybillProduct);
                }
            }
        }
        return productGroups;
    }

    private WaybillTransport roadwayTransport(RoadwayDTO roadwayDTO) {
        WaybillTransport waybillTransport = new WaybillTransport();
        waybillTransport.setId(roadwayDTO.getId());
        if (CollectionUtils.isEmpty(roadwayDTO.getOtherCarOwners())) {
            waybillTransport.getOtherCarOwners().clear();
        } else {
            Set<CarOwner> carOwners = new HashSet<>();
            for (OtherCarOwner otherCarOwner : roadwayDTO.getOtherCarOwners()) {
                CarOwner carOwner = new CarOwner();
                carOwner.setTinOrPinfl(otherCarOwner.getTinOrPinfl());
                carOwner.setName(otherCarOwner.getName());
                carOwners.add(carOwner);
            }
            waybillTransport.setOtherCarOwners(carOwners);
        }
        if (roadwayDTO.getDriver() != null) {
            waybillTransport.setDriverPinfl(roadwayDTO.getDriver().getPinfl());
            waybillTransport.setDriverFullName(roadwayDTO.getDriver().getFullName());
        } else {
            waybillTransport.setDriverPinfl(null);
            waybillTransport.setDriverFullName(null);
        }
        if (roadwayDTO.getTruckDTO() != null) {
            waybillTransport.setTruckRegNo(roadwayDTO.getTruckDTO().getRegNo());
            waybillTransport.setTruckModel(roadwayDTO.getTruckDTO().getModel());
        } else {
            waybillTransport.setTruckRegNo(null);
            waybillTransport.setTruckModel(null);

        }
        if (roadwayDTO.getTrailer() != null) {
            waybillTransport.setTrailerRegNo(roadwayDTO.getTrailer().getRegNo());
            waybillTransport.setTrailerModel(roadwayDTO.getTrailer().getModel());
        } else {
            waybillTransport.setTrailerRegNo(null);
            waybillTransport.setTrailerModel(null);
        }
        waybillTransport.setTrailerType(roadwayDTO.getTrailerType());

        if (CollectionUtils.isEmpty(roadwayDTO.getCarriages())) {
            waybillTransport.getCarriages().clear();
        } else {
            List<Carriages> carriers = new LinkedList<>();
            for (TruckDTO truckDTO : roadwayDTO.getCarriages()) {
                Carriages carriages = new Carriages();
                carriages.setModel(truckDTO.getModel());
                carriages.setRegNo(truckDTO.getRegNo());
                carriers.add(carriages);
            }
            waybillTransport.setCarriages(carriers);
        }
        return waybillTransport;
    }

    public Waybill convertWaybill(WaybillDTO waybillDTO) {
        Waybill waybill = new Waybill();
        waybill.setId(waybillDTO.getId());
        waybill.setWaybillLocalId(waybillDTO.getWaybillLocalId());
        waybill.setComment(waybillDTO.getComment());
        waybill.setWaybillLocalType(waybillDTO.getWaybillLocalType());
        waybill.setDeliveryType(DeliveryType.fromCode(waybillDTO.getDeliveryType()));
        if (waybillDTO.getWaybillDoc() != null) {
            waybill.setWaybillNo(waybillDTO.getWaybillDoc().getWaybillNo());
            waybill.setWaybillDate(waybillDTO.getWaybillDoc().getWaybillDate());
        }
        if (waybillDTO.getOldWaybillDoc() != null) {
            waybill.setOldWaybillLocalId(waybillDTO.getOldWaybillDoc().getOldWaybillLocalId());
            waybill.setOldWaybillNo(waybillDTO.getOldWaybillDoc().getOldWaybillNo());
            waybill.setOldWaybillDate(waybillDTO.getOldWaybillDoc().getOldWaybillDate());
        } else {
            waybill.setOldWaybillLocalId(null);
            waybill.setOldWaybillNo(null);
            waybill.setOldWaybillDate(null);
        }
        if (waybillDTO.getContractDoc() != null) {
            waybill.setContractNo(waybillDTO.getContractDoc().getContractNo());
            waybill.setContractDate(waybillDTO.getContractDoc().getContractDate());
        } else {
            waybill.setContractNo(null);
            waybill.setContractDate(null);
        }

        if (waybillDTO.getConsignor() != null) {
            if (Objects.equals(waybillDTO.getConsignor().getTinOrPinfl(), getCurrentOrganizationTin())) {
                waybill.setWaybillType(WaybillType.OUTGOING);
            } else {
                waybill.setWaybillType(WaybillType.INCOMING);
            }
            waybill.setConsignorTinOrPinfl(waybillDTO.getConsignor().getTinOrPinfl());
            waybill.setConsignorName(waybillDTO.getConsignor().getName());
            waybill.setConsignorBranchCode(waybillDTO.getConsignor().getBranchCode());
            waybill.setConsignorBranchName(waybillDTO.getConsignor().getBranchName());
        }

        if (waybillDTO.getConsignee() != null) {
            waybill.setConsigneeTinOrPinfl(waybillDTO.getConsignee().getTinOrPinfl());
            waybill.setConsigneeName(waybillDTO.getConsignee().getName());
            waybill.setConsigneeBranchCode(waybillDTO.getConsignee().getBranchCode());
            waybill.setConsigneeBranchName(waybillDTO.getConsignee().getBranchName());
        }

        if (waybillDTO.getFreightForwarder() != null) {
            waybill.setFreightForwarderTinOrPinfl(waybillDTO.getFreightForwarder().getTinOrPinfl());
            waybill.setFreightForwarderName(waybillDTO.getFreightForwarder().getName());
            waybill.setFreightForwarderBranchCode(waybillDTO.getFreightForwarder().getBranchCode());
            waybill.setFreightForwarderBranchName(waybillDTO.getFreightForwarder().getBranchName());
        } else {
            waybill.setFreightForwarderTinOrPinfl(null);
            waybill.setFreightForwarderName(null);
            waybill.setFreightForwarderBranchCode(null);
            waybill.setFreightForwarderBranchName(null);
        }
        if (waybillDTO.getCarrier() != null) {
            waybill.setCarrierTinOrPinfl(waybillDTO.getCarrier().getTinOrPinfl());
            waybill.setCarrierName(waybillDTO.getCarrier().getName());
            waybill.setCarrierBranchCode(waybillDTO.getCarrier().getBranchCode());
            waybill.setCarrierBranchName(waybillDTO.getCarrier().getBranchName());
        } else {
            waybill.setCarrierTinOrPinfl(null);
            waybill.setCarrierName(null);
            waybill.setCarrierBranchCode(null);
            waybill.setCarrierBranchName(null);
        }
        if (waybillDTO.getClient() != null) {
            waybill.setClientTinOrPinfl(waybillDTO.getClient().getTinOrPinfl());
            waybill.setClientName(waybillDTO.getClient().getName());
            waybill.setClientBranchCode(waybillDTO.getClient().getBranchCode());
            waybill.setClientBranchName(waybillDTO.getClient().getBranchName());
            waybill.setClientContractNo(waybillDTO.getClient().getContractNo());
            waybill.setClientContractDate(waybillDTO.getClient().getContractDate());
        } else {
            waybill.setClientTinOrPinfl(null);
            waybill.setClientName(null);
            waybill.setClientBranchCode(null);
            waybill.setClientBranchName(null);
            waybill.setClientContractNo(null);
            waybill.setClientContractDate(null);
        }

        if (waybillDTO.getPayer() != null) {
            waybill.setPayerTinOrPinfl(waybillDTO.getPayer().getTinOrPinfl());
            waybill.setPayerName(waybillDTO.getPayer().getName());
            waybill.setPayerBranchCode(waybillDTO.getPayer().getBranchCode());
            waybill.setPayerBranchName(waybillDTO.getPayer().getBranchName());
            waybill.setPayerContractNo(waybillDTO.getPayer().getContractNo());
            waybill.setPayerContractDate(waybillDTO.getPayer().getContractDate());
        } else {
            waybill.setPayerTinOrPinfl(null);
            waybill.setPayerName(null);
            waybill.setPayerBranchCode(null);
            waybill.setPayerBranchName(null);
            waybill.setPayerContractNo(null);
            waybill.setPayerContractDate(null);
        }
        if (waybillDTO.getResponsiblePerson() != null) {
            waybill.setResponsiblePersonPinfl(waybillDTO.getResponsiblePerson().getPinfl());
            waybill.setResponsiblePersonFullName(waybillDTO.getResponsiblePerson().getFullName());
        } else {
            waybill.setResponsiblePersonPinfl(null);
            waybill.setResponsiblePersonFullName(null);
        }

        waybill.setHasCommittent(waybillDTO.getHasCommittent());
        waybill.setStatus(Optional.ofNullable(waybillDTO.getStatus()).orElse(WaybillStatus.DRAFT));
        waybill.setTotalDistance(waybillDTO.getTotalDistance());
        waybill.setDeliveryCost(waybillDTO.getDeliveryCost());
        waybill.setTotalDeliveryCost(waybillDTO.getTotalDeliveryCost());
        waybill.setTransportType(TransportType.fromCode(waybillDTO.getTransportType()));
        return waybill;
    }

    public Page<WaybillListDTO> findAllByFilter(WaybillFilter filter) {
        return waybillRepository.findAllByFilter(filter)
                .map(Waybill::toListDTO);
    }

    public Page<Waybill1CDTO> findAllByFilterFor1C(WaybillFilter filter) {
        return waybillRepository.findAllByFilter(filter)
                .map(waybill -> {
                    Waybill1CDTO waybill1CDTO = new Waybill1CDTO();
                    waybill1CDTO.setId(waybill.getId());
                    waybill1CDTO.setWaybillLocalId(waybill.getWaybillLocalId());
                    waybill1CDTO.setWaybillDoc(new WaybillDoc(waybill.getWaybillNo(), waybill.getWaybillDate()));
                    waybill1CDTO.setContractDoc(new ContractDoc(waybill.getContractNo(), waybill.getContractDate()));
                    waybill1CDTO.setDeliveryType(waybill.getDeliveryType());
                    waybill1CDTO.setDeliveryTypeName(waybill.getDeliveryType().getText());
                    WaybillCompany consignor = new WaybillCompany();
                    consignor.setName(waybill.getConsignorName());
                    consignor.setTinOrPinfl(waybill.getConsignorTinOrPinfl());
                    waybill1CDTO.setConsignor(consignor);
                    WaybillCompany consignee = new WaybillCompany();
                    consignee.setName(waybill.getConsigneeName());
                    consignee.setTinOrPinfl(waybill.getConsigneeTinOrPinfl());
                    waybill1CDTO.setConsignee(consignee);
                    waybill1CDTO.setCreatedDate(waybill.getCreatedDate());
                    waybill1CDTO.setLastModifiedDate(waybill.getLastModifiedDate());
                    waybill1CDTO.setStatus(waybill.getStatus());
                    waybill1CDTO.setStatusName(waybill.getStatus().getRuText());
                    waybill1CDTO.setWaybillType(waybill.getWaybillLocalType());
                    waybill1CDTO.setWaybillTypeName(WaybillLocalType.fromCode(waybill.getWaybillLocalType()));
                    waybill1CDTO.setWaybillDTO(waybill.toDTO());
                    return waybill1CDTO;
                });
    }

    public WaybillDTO getOne(String id) {
        return waybillRepository.findById(id)
                .map(Waybill::toDTO)
                .orElseThrow(notFound("ТТН не найден"));
    }

    @Transactional
    public void send(WaybillSendDTO sendDTO) {
        final Organization organization = organizationRepository.findById(getCurrentOrganizationId())
                .orElseThrow(() -> new BadRequestException("Организация не найден"));
        Optional<OrganizationPermissions> invoiceBalance = Optional.empty();
        if (!organization.isDemoAllowed()) {
            invoiceBalance = permissionRepository.findFirstByOrganizationIdAndPermissionsKeyEqualsAndCountGreaterThan(organization.getId(), ServiceType.E_INVOICE.name(), 0);
            if (invoiceBalance.isEmpty()) {
                throw badRequest("Недостаточно средств на балансе").get();
            }
        }
        final UUID requestId = UUID.randomUUID();

        Waybill waybill = waybillRepository.findFirstByWaybillLocalId(sendDTO.getWaybillLocalId())
                .orElseThrow(() -> new BadRequestException("ТТН не найдена"));
        final Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(sendDTO.getSign());
        if (pkcs7Dto.isSuccess() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(pkcs7Dto.getTimestampedSignerList())) {
            final String sign = pkcs7Dto.getPkcs7b64();
            final SignatureCertificateDto certificateDto = pkcs7Dto.getTimestampedSignerList().getFirst();

            sendDTO.setSign(sign);
            facturaClient.sendWaybill(
                    SecurityUtils.getCurrentLocale(),
                    getCurrentOrganizationTin(),
                    sendDTO,
                    requestId,
                    sendDTO.getWaybillLocalId()
            );

            waybill.setSignatureContent(sendDTO.getSign());
            waybill.setStatus(WaybillStatus.ConsignorSent);
            waybillRepository.save(waybill);

            WaybillExecutor waybillExecutor = new WaybillExecutor();
            waybillExecutor.setWaybillId(waybill.getId());
            waybillExecutor.setStatus(WaybillStatus.ConsignorSent);
            waybillExecutor.setOperationNumber(Integer.parseInt(certificateDto.getSerialNumber(), 16) + "");
            waybillExecutor.setOperationBy(certificateDto.getSubjectName().getFullName());
            waybillExecutor.setOperationDate(LocalDateTime.now());
            waybillExecutorRepository.save(waybillExecutor);
        } else {
            log.error("JSON: {}", pkcs7Dto);
            throw new BadRequestException("Not verified, reason: " + pkcs7Dto.getMessage());
        }
        invoiceBalance.ifPresent(balance -> {
            balance.setCount(balance.getCount() - 1);
            permissionRepository.save(balance);
        });
    }

    @Transactional
    public void cancel(WaybillSendDTO waybillSendDTO) {
        Waybill waybill = waybillRepository.findFirstByWaybillLocalId(waybillSendDTO.getWaybillLocalId())
                .orElseThrow(() -> new BadRequestException("ТТН не найдена"));
        if (WaybillStatus.ConsigneeAccepted != waybill.getStatus()) {
            throw new BadRequestException("Невозможно отменить ТТН со статусом " + waybill.getStatus());
        }
        final Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(waybillSendDTO.getSign());
        if (pkcs7Dto.isSuccess() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(pkcs7Dto.getTimestampedSignerList())) {
            final SignatureCertificateDto certificateDto = pkcs7Dto.getTimestampedSignerList().size() > 1 ?
                    pkcs7Dto.getTimestampedSignerList().get(1) : pkcs7Dto.getTimestampedSignerList().get(0);

            waybillSendDTO.setSign(pkcs7Dto.getPkcs7b64());
            facturaClient.cancelWaybill(SecurityUtils.getCurrentLocale(), getCurrentOrganizationTin(), waybillSendDTO, waybill.getWaybillLocalId());

            waybill.setStatus(WaybillStatus.ConsignorCanceled);
            waybill.setNotes(waybillSendDTO.getNotes());
            waybillRepository.save(waybill);
            WaybillExecutor waybillExecutor = new WaybillExecutor();
            waybillExecutor.setWaybillId(waybill.getId());
            waybillExecutor.setStatus(WaybillStatus.ConsignorCanceled);
            waybillExecutor.setOperationNumber(Integer.parseInt(certificateDto.getSerialNumber(), 16) + "");
            waybillExecutor.setOperationBy(certificateDto.getSubjectName().getFullName());
            waybillExecutor.setOperationDate(LocalDateTime.now());
            waybillExecutorRepository.save(waybillExecutor);
        } else {
            log.debug("JSON: {}", pkcs7Dto);
            throw new BadRequestException("Not verified, reason: " + pkcs7Dto.getMessage());
        }
    }

    @Transactional
    public void acceptOrReject(WaybillSendDTO waybillSendDTO, final WaybillStatus status) {
        Waybill waybill = waybillRepository.findFirstByWaybillLocalId(waybillSendDTO.getWaybillLocalId())
                .orElseThrow(() -> new BadRequestException("ТТН не найдена"));
        if (WaybillStatus.ConsignorSent != waybill.getStatus()) {
            throw new BadRequestException("Невозможно отменить ТТН со статусом " + waybill.getStatus());
        }
        final Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(waybillSendDTO.getSign());
        if (pkcs7Dto.isSuccess() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(pkcs7Dto.getTimestampedSignerList())) {
            final SignatureCertificateDto certificateDto = pkcs7Dto.getTimestampedSignerList().size() > 1 ?
                    pkcs7Dto.getTimestampedSignerList().get(1) : pkcs7Dto.getTimestampedSignerList().get(0);

            waybillSendDTO.setSign(pkcs7Dto.getPkcs7b64());
            if (WaybillStatus.ConsigneeAccepted == status) {
                facturaClient.acceptWaybill(SecurityUtils.getCurrentLocale(), getCurrentOrganizationTin(), waybillSendDTO, waybillSendDTO.getWaybillLocalId());
                waybill.setStatus(WaybillStatus.ConsigneeAccepted);
            } else {
                facturaClient.rejectWaybill(SecurityUtils.getCurrentLocale(), getCurrentOrganizationTin(), waybillSendDTO, waybillSendDTO.getWaybillLocalId());
                waybill.setStatus(WaybillStatus.ConsigneeRejected);
            }


            waybill.setNotes(waybillSendDTO.getNotes());

            WaybillExecutor waybillExecutor = new WaybillExecutor();
            waybillExecutor.setWaybillId(waybill.getId());
            waybillExecutor.setStatus(waybill.getStatus());
            waybillExecutor.setOperationNumber(Integer.parseInt(certificateDto.getSerialNumber(), 16) + "");
            waybillExecutor.setOperationBy(certificateDto.getSubjectName().getFullName());
            waybillExecutor.setOperationDate(LocalDateTime.now());
            waybill.addExecutor(waybillExecutor);
            waybillRepository.save(waybill);
        } else {
            log.debug("JSON: {}", pkcs7Dto);
            throw new BadRequestException("Not verified, reason: " + pkcs7Dto.getMessage());
        }
    }

    @Transactional
    public String convertInvoice(final Long id) {
        return listInvoiceRepository.findById(id)
                .filter(inv -> !inv.isDeleted())
                .map(invoice -> {
                    if (Status.DRAFT.equals(invoice.getStatus())) {
                        return createFromInvoiceDraft(invoice);
                    } else {
                        return createFromInvoiceSign(convertToDto(invoice.getSignatureContent()), invoice.getId());
                    }
                })
                .orElseThrow(() -> new BadRequestException("С таким идентификатором СФ не найден"));
    }

    private FacturaDTO convertToDto(final String sign) {
        final Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
        if (pkcs7Dto.isSuccess()) {
            return parseBase64(pkcs7Dto.getPkcs7Info().getDocumentBase64());
        } else {
            throw new BadRequestException(pkcs7Dto.getMessage());
        }
    }


    public String createFromInvoiceDraft(ListInvoice listInvoice) {
        final Invoice invoice = listInvoice.getInvoice();
        if (invoice == null) {
            throw badRequest("СФ не найден").get();
        }
        final Organization seller = Optional.ofNullable(getCurrentOrganization()).orElseThrow(notFound("Продавец не найден"));
        final Customer buyer = customerRepository.findFirstByOrganizationIdAndTinAndDeletedIsFalse(seller.getId(), listInvoice.getBuyerTin()).orElseThrow(badRequest("Покупатель не найден"));

        Waybill waybill = new Waybill();
        waybill.setInvoiceId(listInvoice.getId());
        waybill.setWaybillType(WaybillType.OUTGOING);
        waybill.setWaybillLocalType(WaybillLocalType.STANDART.getCode());
        waybill.setOrganizationId(seller.getId());
        final ResponseDTO responseDTO = facturaClient.getGuid();
        waybill.setWaybillLocalId(responseDTO != null ? responseDTO.getData() : null);
        waybill.setDeliveryType(DeliveryType.DELIVERY);

        waybill.setWaybillNo(invoice.getNumber());
        waybill.setWaybillDate(invoice.getInvoiceDate());
        waybill.setContractNo(invoice.getContractNumber());
        waybill.setContractDate(invoice.getContractDate());

        waybill.setConsignorTinOrPinfl(seller.getTin());
        waybill.setConsignorName(seller.getName());

        waybill.setConsigneeTinOrPinfl(buyer.getTin());
        waybill.setConsigneeName(buyer.getName());

        waybill.setHasCommittent(invoice.getHasComittent());
        waybill.setStatus(WaybillStatus.DRAFT);
        waybill.setTotalDistance(BigDecimal.ZERO);
        waybill.setDeliveryCost(BigDecimal.ZERO);
        waybill.setTotalDeliveryCost(BigDecimal.ZERO);

        // converting to RoadwayTransport
        waybill.setTransportType(TransportType.Roadway);
        if (TransportType.Roadway == waybill.getTransportType()) {
            WaybillTransport roadwayTransport = new WaybillTransport();
            roadwayTransport.setTransportType(TransportType.Roadway);

            if (!CollectionUtils.isEmpty(invoice.getItems())) {
                WaybillProductGroups productGroups = new WaybillProductGroups();
                productGroups.setTotalDeliverySum(BigDecimal.ZERO);
                productGroups.setTotalWeightBrutto(BigDecimal.ZERO);

                if (seller.getDistrict() != null) {
                    productGroups.setLoadingRegionId(seller.getDistrict().getRegionId().intValue());
                    productGroups.setLoadingRegionName(seller.getDistrict().getRegion().getNameLocale());
                    productGroups.setLoadingDistrictId(seller.getDistrict().getCode().intValue());
                    productGroups.setLoadingDistrictName(seller.getDistrict().getNameLocale());
                }
                productGroups.setLoadingAddress(seller.getAddress());
                productGroups.setLoadingLongitude(Optional.ofNullable(seller.getLon()).orElse(0.0));
                productGroups.setLoadingLatitude(Optional.ofNullable(seller.getLat()).orElse(0.0));

                if (buyer.getDistrict() != null) {
                    productGroups.setUnloadingRegionId(buyer.getDistrict().getRegionId().intValue());
                    productGroups.setUnloadingRegionName(buyer.getDistrict().getRegion().getName());
                    productGroups.setUnloadingDistrictId(buyer.getDistrict().getCode().intValue());
                    productGroups.setUnloadingDistrictName(buyer.getDistrict().getName());
                    productGroups.setUnloadingAddress(buyer.getAddress());
                    productGroups.setUnloadingLongitude(buyer.getLon());
                    productGroups.setUnloadingLatitude(buyer.getLat());
                }

                if (invoice.getEmpowermentNo() != null) {
                    productGroups.setUnloadingEmpowermentId(null);
                    productGroups.setUnloadingEmpowermentNo(invoice.getEmpowermentNo());
                    productGroups.setUnloadingEmpowermentDateOfExpire(invoice.getEmpowermentDateOfExpire());
                    productGroups.setUnloadingEmpowermentDateOfIssue(invoice.getEmpowermentDateOfIssue());
                }

                for (InvoiceItem invoiceItem : invoice.getItems()) {
                    if (Objects.equals(invoiceItem.getCatalogCode(), "10499001018000000")) continue;
                    WaybillProduct waybillProduct = new WaybillProduct();
                    waybillProduct.setOrdNo(Integer.parseInt(invoiceItem.getOrderNo()));
                    waybillProduct.setCommittentTinOrPinfl(invoiceItem.getComittentTin());
                    waybillProduct.setCommittentName(invoiceItem.getComittentName());
                    waybillProduct.setProductName(invoiceItem.getName());
                    waybillProduct.setCatalogCode(invoiceItem.getCatalogCode());
                    waybillProduct.setCatalogName(invoiceItem.getCatalogName());
                    waybillProduct.setPackageCode(invoiceItem.getPackageCode());
                    waybillProduct.setPackageName(invoiceItem.getPackageName());
                    waybillProduct.setAmount(invoiceItem.getQty());
                    waybillProduct.setPrice(invoiceItem.getPrice());

                    productRepository.findFirstByBarcodeAndOrganizationIdAndDeletedIsFalse(invoiceItem.getBarcode(), seller.getId())
                            .ifPresent(product -> {
                                final Optional<Excise> excise = exciseRepository.findOneByType(product.getType());
                                if (excise.isPresent() && excise.get().getAmount().compareTo(BigDecimal.ZERO) != 0) {
                                    final BigDecimal exciseRate = excise.get().calculateRate(product.getCapacity(), product.getTitleAlcohol());
                                    waybillProduct.setPrice(waybillProduct.getPrice().add(exciseRate));
                                }

                                waybillProduct.setWeightNetto(waybillProduct.getAmount().multiply(product.getWeightNetto()));
                                waybillProduct.setWeightBrutto(waybillProduct.getAmount().multiply(product.getWeightBrutto()));
                            });
                    waybillProduct.setDeliverySum(waybillProduct.getPrice().multiply(waybillProduct.getAmount()));
                    productGroups.setTotalDeliverySum(productGroups.getTotalDeliverySum().add(waybillProduct.getDeliverySum()));
                    productGroups.setTotalWeightBrutto(productGroups.getTotalWeightBrutto().add(waybillProduct.getWeightBrutto()));

                    // relation for WaybillProduct and WaybillProductGroup
                    productGroups.addProduct(waybillProduct);
                }

                // relation WaybillProductGroup with WaybillTransport
                roadwayTransport.addProductGroups(productGroups);
            }

            // relation WaybillTransport with Waybill
            waybill.addWaybillTransport(roadwayTransport);
        }
        waybillRepository.save(waybill);
        return waybill.getId();
    }

    public String createFromInvoiceSign(FacturaDTO facturaDTO, Long id) {

        final Organization seller = Optional.ofNullable(getCurrentOrganization()).orElseThrow(notFound("Продавец не найден"));

        Waybill waybill = new Waybill();
        waybill.setInvoiceId(id);
        waybill.setWaybillType(WaybillType.OUTGOING);
        waybill.setWaybillLocalType(WaybillLocalType.STANDART.getCode());
        waybill.setOrganizationId(SecurityUtils.getCurrentOrganizationId());
        final ResponseDTO responseDTO = facturaClient.getGuid();
        waybill.setWaybillLocalId(responseDTO != null ? responseDTO.getData() : null);
        waybill.setDeliveryType(DeliveryType.DELIVERY);

        if (facturaDTO.getFacturaDoc() != null) {
            waybill.setWaybillNo(facturaDTO.getFacturaDoc().getFacturaNo());
            waybill.setWaybillDate(facturaDTO.getFacturaDoc().getFacturaDate());
        }
        if (facturaDTO.getContractDoc() != null) {
            waybill.setContractNo(facturaDTO.getContractDoc().getContractNo());
            waybill.setContractDate(facturaDTO.getContractDoc().getContractDate());
        }

        if (facturaDTO.getSeller() != null) {
            waybill.setConsignorTinOrPinfl(facturaDTO.getSellerTin());
            waybill.setConsignorName(facturaDTO.getSeller().getName());
            waybill.setConsignorBranchCode(facturaDTO.getSeller().getBranchCode());
            waybill.setConsignorBranchCode(facturaDTO.getSeller().getBranchName());
        }

        if (facturaDTO.getBuyer() != null) {
            waybill.setConsigneeTinOrPinfl(facturaDTO.getBuyerTin());
            waybill.setConsigneeName(facturaDTO.getBuyer().getName());
            waybill.setConsigneeBranchName(facturaDTO.getBuyer().getBranchName());
            waybill.setConsigneeBranchCode(facturaDTO.getBuyer().getBranchCode());
        }

        waybill.setStatus(WaybillStatus.DRAFT);
        waybill.setTotalDistance(BigDecimal.ZERO);
        waybill.setDeliveryCost(BigDecimal.ZERO);
        waybill.setTotalDeliveryCost(BigDecimal.ZERO);

        // converting to RoadwayTransport
        waybill.setTransportType(TransportType.Roadway);
        if (TransportType.Roadway == waybill.getTransportType()) {
            WaybillTransport roadwayTransport = new WaybillTransport();
            roadwayTransport.setTransportType(TransportType.Roadway);

            if (facturaDTO.getProductList() != null && !CollectionUtils.isEmpty(facturaDTO.getProductList().getProducts())) {
                WaybillProductGroups productGroups = new WaybillProductGroups();
                productGroups.setTotalDeliverySum(BigDecimal.ZERO);
                productGroups.setTotalWeightBrutto(BigDecimal.ZERO);

                if (seller.getDistrict() != null) {
                    productGroups.setLoadingRegionId(seller.getDistrict().getRegionId().intValue());
                    productGroups.setLoadingRegionName(seller.getDistrict().getRegion().getNameLocale());
                    productGroups.setLoadingDistrictId(seller.getDistrict().getCode().intValue());
                    productGroups.setLoadingDistrictName(seller.getDistrict().getNameLocale());
                    productGroups.setLoadingAddress(seller.getAddress());
                }
                productGroups.setLoadingAddress(seller.getAddress());
                productGroups.setLoadingLongitude(Optional.ofNullable(seller.getLon()).orElse(0.0));
                productGroups.setLoadingLatitude(Optional.ofNullable(seller.getLat()).orElse(0.0));

                for (FacturaProductDTO facturaProductDTO : facturaDTO.getProductList().getProducts()) {
                    if (Objects.equals(facturaProductDTO.getCatalogCode(), "10499001018000000")) continue;
                    WaybillProduct waybillProduct = new WaybillProduct();
                    waybillProduct.setOrdNo(Integer.valueOf(facturaProductDTO.getOrdNo()));
                    waybillProduct.setCommittentTinOrPinfl(facturaProductDTO.getComittentTin());
                    waybillProduct.setCommittentName(facturaProductDTO.getComittentName());
                    waybillProduct.setProductName(facturaProductDTO.getName());
                    waybillProduct.setCatalogCode(facturaProductDTO.getCatalogCode());
                    waybillProduct.setCatalogName(facturaProductDTO.getCatalogName());
                    waybillProduct.setPackageCode(facturaProductDTO.getPackageCode());
                    waybillProduct.setPackageName(facturaProductDTO.getPackageName());
                    waybillProduct.setAmount(facturaProductDTO.getCount());
                    waybillProduct.setPrice(facturaProductDTO.getSumma());

                    productRepository.findFirstByBarcodeAndOrganizationIdAndDeletedIsFalse(facturaProductDTO.getBarcode(), seller.getId())
                            .ifPresent(product -> {
                                final Optional<Excise> excise = exciseRepository.findOneByType(product.getType());
                                if (excise.isPresent() && excise.get().getAmount().compareTo(BigDecimal.ZERO) != 0) {
                                    final BigDecimal exciseRate = excise.get().calculateRate(product.getCapacity(), product.getTitleAlcohol());
                                    waybillProduct.setPrice(waybillProduct.getPrice().add(exciseRate));
                                }
                                waybillProduct.setWeightNetto(waybillProduct.getAmount().multiply(product.getWeightNetto()));
                                waybillProduct.setWeightBrutto(waybillProduct.getAmount().multiply(product.getWeightBrutto()));

                            });
                    waybillProduct.setDeliverySum(waybillProduct.getPrice().multiply(waybillProduct.getAmount()));
                    productGroups.setTotalDeliverySum(productGroups.getTotalDeliverySum().add(waybillProduct.getDeliverySum()));
                    productGroups.setTotalWeightBrutto(productGroups.getTotalWeightBrutto().add(waybillProduct.getWeightBrutto()));

                    // relation WaybillProduct with WaybillProductGroup
                    productGroups.addProduct(waybillProduct);
                }

                // relation WaybillProductGroup with WaybillTransport
                roadwayTransport.addProductGroups(productGroups);
            }

            // relation WaybillTransport with Waybill
            waybill.addWaybillTransport(roadwayTransport);
        }
        waybillRepository.save(waybill);
        return waybill.getId();
    }


    @Transactional
    public String createFromAct(String acceptanceTransferActId) {
        AcceptanceTransferAct acceptanceTransferAct = acceptanceTransferActRepository.findById(acceptanceTransferActId).orElseThrow(notFound("Акты приема-передачи не найден"));
        if (ObjectUtils.isEmpty(acceptanceTransferAct.getCustomer()) && ObjectUtils.isEmpty(acceptanceTransferAct.getStore())) throw badRequest("Покупатель не найден").get();
        if (ObjectUtils.isEmpty(acceptanceTransferAct.getOrganization())) throw badRequest("Продавец не найден").get();

        Waybill waybill = new Waybill();
        waybill.setWaybillType(WaybillType.OUTGOING);
        waybill.setWaybillLocalType(WaybillLocalType.STANDART.getCode());
        waybill.setOrganizationId(SecurityUtils.getCurrentOrganizationId());

        final ResponseDTO responseDTO = facturaClient.getGuid();
        waybill.setWaybillLocalId(responseDTO != null ? responseDTO.getData() : null);
        waybill.setDeliveryType(DeliveryType.DELIVERY);
        if (acceptanceTransferAct.getStore() != null) {
            waybill.setDeliveryType(DeliveryType.PICKUP);
        }
        waybill.setWaybillNo(acceptanceTransferAct.getActNumber());
        waybill.setWaybillDate(acceptanceTransferAct.getActDate());

        if (DeliveryType.PICKUP != waybill.getDeliveryType() && acceptanceTransferAct.getContract() != null) {
            waybill.setContractNo(acceptanceTransferAct.getContract().getNumber());
            waybill.setContractDate(acceptanceTransferAct.getContract().getDate());
        }

        Organization seller = acceptanceTransferAct.getOrganization();
        waybill.setConsignorTinOrPinfl(seller.getTin());
        waybill.setConsignorName(seller.getName());

        Customer buyer = null;
        if (acceptanceTransferAct.getCustomer() != null) {
            buyer = acceptanceTransferAct.getCustomer();
            waybill.setConsigneeTinOrPinfl(buyer.getTin());
            waybill.setConsigneeName(buyer.getName());
            waybill.setHasCommittent(buyer.isCommittent());
        }

        waybill.setStatus(WaybillStatus.DRAFT);
        waybill.setTotalDistance(BigDecimal.ZERO);
        waybill.setDeliveryCost(BigDecimal.ZERO);
        waybill.setTotalDeliveryCost(BigDecimal.ZERO);

        // converting to RoadwayTransport
        waybill.setTransportType(TransportType.Roadway);
        if (TransportType.Roadway == waybill.getTransportType()) {
            WaybillTransport roadwayTransport = new WaybillTransport();
            roadwayTransport.setTransportType(TransportType.Roadway);
            if (!CollectionUtils.isEmpty(acceptanceTransferAct.getProducts())) {

                WaybillProductGroups productGroups = new WaybillProductGroups();
                productGroups.setTotalDeliverySum(BigDecimal.ZERO);
                productGroups.setTotalWeightBrutto(BigDecimal.ZERO);

                if (seller.getDistrict() != null) {
                    District district = seller.getDistrict();
                    productGroups.setLoadingRegionId(district.getRegionId().intValue());
                    productGroups.setLoadingRegionName(district.getRegion().getNameLocale());
                    productGroups.setLoadingDistrictId(district.getCode().intValue());
                    productGroups.setLoadingDistrictName(district.getNameLocale());
                    productGroups.setLoadingAddress(seller.getAddress());
                    productGroups.setLoadingLongitude(seller.getLon());
                    productGroups.setLoadingLatitude(seller.getLat());
                }

                if (buyer != null && buyer.getDistrict() != null) {
                    District district = buyer.getDistrict();
                    productGroups.setUnloadingRegionId(district.getRegionId().intValue());
                    productGroups.setUnloadingRegionName(district.getRegion().getNameLocale());
                    productGroups.setUnloadingDistrictId(district.getCode().intValue());
                    productGroups.setUnloadingDistrictName(district.getNameLocale());
                    productGroups.setUnloadingAddress(buyer.getAddress());
                    productGroups.setUnloadingLongitude(buyer.getLon());
                    productGroups.setUnloadingLatitude(buyer.getLat());
                } else if (acceptanceTransferAct.getStore() != null) {
                    Store store = acceptanceTransferAct.getStore();
                    District district = store.getDistrict();
                    productGroups.setUnloadingRegionId(district.getRegionId().intValue());
                    productGroups.setUnloadingRegionName(district.getRegion().getNameLocale());
                    productGroups.setUnloadingDistrictId(district.getCode().intValue());
                    productGroups.setUnloadingDistrictName(district.getNameLocale());
                    productGroups.setUnloadingAddress(store.getAddress());
                    if (store.getPersonalUser() != null) {
                        productGroups.setUnloadingTrusteeName(store.getPersonalUser().getName());
                        productGroups.setUnloadingTrusteeName(store.getPersonalUser().getPinfl());
                    }
                }
                int orderNumber = 1;
                for (AcceptanceTransferActProduct acceptanceTransferActProduct : acceptanceTransferAct.getProducts()) {
                    final Product product = acceptanceTransferActProduct.getProduct();
                    WaybillProduct waybillProduct = new WaybillProduct();
                    waybillProduct.setOrdNo(orderNumber++);
                    waybillProduct.setProductId(product.getId());
                    if (buyer != null && buyer.isCommittent()) {
                        waybillProduct.setCommittentTinOrPinfl(buyer.getTin());
                        waybillProduct.setCommittentName(buyer.getName());
                    }
                    waybillProduct.setProductName(product.getName());
                    waybillProduct.setCatalogCode(product.getCatalogCode());
                    waybillProduct.setCatalogName(product.getCatalogName());
                    waybillProduct.setPackageCode(product.getPackageCode());
                    waybillProduct.setPackageName(product.getPackageName());
                    waybillProduct.setAmount(BigDecimal.valueOf(acceptanceTransferActProduct.getQuantity()));
                    waybillProduct.setPrice(acceptanceTransferActProduct.getPrice());

                    final Optional<Excise> excise = exciseRepository.findOneByType(product.getType());
                    if (excise.isPresent() && excise.get().getAmount().compareTo(BigDecimal.ZERO) != 0) {
                        final BigDecimal exciseRate = excise.get().calculateRate(product.getCapacity(), product.getTitleAlcohol());
                        waybillProduct.setPrice(waybillProduct.getPrice().add(exciseRate));
                    }
                    waybillProduct.setDeliverySum(waybillProduct.getPrice().multiply(waybillProduct.getAmount()));
                    waybillProduct.setWeightNetto(waybillProduct.getAmount().multiply(Optional.ofNullable(product.getWeightNetto()).orElse(BigDecimal.ZERO)));
                    waybillProduct.setWeightBrutto(waybillProduct.getAmount().multiply(Optional.ofNullable(product.getWeightBrutto()).orElse(BigDecimal.ZERO)));
                    // total calculation
                    productGroups.setTotalDeliverySum(productGroups.getTotalDeliverySum().add(acceptanceTransferActProduct.getPaymentSum()));
                    productGroups.setTotalWeightBrutto(productGroups.getTotalWeightBrutto().add(waybillProduct.getWeightBrutto()));

                    // relation WaybillProduct with WaybillProductGroup
                    productGroups.addProduct(waybillProduct);

                }
                //relation WaybillProductGroup with WaybillTransport
                roadwayTransport.addProductGroups(productGroups);
            }

            // relation WaybillTransport with Waybill
            waybill.addWaybillTransport(roadwayTransport);
        }
        waybillRepository.save(waybill);
        return waybill.getId();
    }

    public List<CompanyTransportDTO> getCompanyTransports(String tin, EnumSet<WaybillTransportType> types) {
        List<CompanyTransportDTO> list = new ArrayList<>();
        CompanyTransportDTO[] companyTransports = facturaClient.getCompanyTransports(tin);
        if (companyTransports == null) {
            throw new BadRequestException("Нет данных по данному ИНН", tin);
        }
        for (CompanyTransportDTO companyTransport : companyTransports) {
            if (types.contains(WaybillTransportType.fromCode(companyTransport.getTransportType()))) {
                list.add(companyTransport);
            }
        }
        return list;
    }

    public EmpowermentDTO[] getCompanyEmpowerments(String tin) {
        CompanyEmpowermentsDTO result = facturaClient.getCompanyEmpowerments(tin);
        if (result != null) {
            return result.getRows();
        } else {
            throw new BadRequestException("Нет данных по данному ИНН", tin);
        }
    }

    public void delete(String id) {
        Waybill waybill = waybillRepository.findById(id).orElseThrow(notFound("ТНН не найден"));
        waybillRepository.delete(waybill);
    }

    public void deleteProductGroup(String id) {
        WaybillProductGroups waybillProductGroups = waybillProductGroupRepository.findById(id)
                .orElseThrow(notFound("Направление не найден"));
        waybillProductGroupRepository.delete(waybillProductGroups);
    }

    public void deleteProduct(String id) {
        WaybillProduct waybillProduct = waybillProductRepository.findById(id)
                .orElseThrow(notFound("Продукция не найден"));
        waybillProductRepository.delete(waybillProduct);
    }

    public List<WaybillCustomerDTO> getCompanyCustomers(CustomerFilter filter) {
        return customerRepository.findAllByFilter(filter)
                .map(customer -> {
                    WaybillCustomerDTO dto = new WaybillCustomerDTO();
                    dto.setId(customer.getId());
                    dto.setTin(customer.getTin());
                    dto.setName(customer.getName());
                    dto.setCommittent(customer.isCommittent());
                    return dto;
                }).getContent();
    }

    public PhysicalPersonDTO getPhysicalPersonData(String tinOrPinfl) {
        PhysicalPersonDTO result = soliqClient.getUserByTinOrPinfl(tinOrPinfl);
        if (result != null) {
            return result;
        } else {
            throw new BadRequestException("Нет данных по данному ИНН", tinOrPinfl);
        }
    }

    public void callback(WaybillCallback waybillCallback) {
        try {
            final Waybill waybill = waybillRepository.findFirstByWaybillLocalId(waybillCallback.getWaybillLocalId())
                    .orElse(null);
            if (waybill != null) {
                waybill.setStatus(WaybillStatus.fromCode(waybillCallback.getCurrentStateId()));
                waybillRepository.save(waybill);
            }
        } catch (Exception e) {
            log.error("Waybill callback error {}", e.getMessage(), e);
        }
        /*WaybillExecutor waybillExecutor = new WaybillExecutor();
        waybillExecutor.setWaybillId(waybill.getId());
        waybillExecutor.setStatus(waybill.getStatus());
        waybillExecutor.setOperationNumber(Integer.parseInt(certificateDto.getSerialNumber(), 16) + "");
        waybillExecutor.setOperationBy(certificateDto.getSubjectName().getFullName());
        waybillExecutor.setOperationDate(LocalDateTime.now());
        waybillExecutorRepository.save(waybillExecutor);*/
    }

    public Object getStations() {
        return facturaClient.getStations();
    }
}

