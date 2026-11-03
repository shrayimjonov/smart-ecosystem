package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.*;
import uz.uzkassa.silen.domain.mongo.ShipmentMark;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.acceptancetransferact.AcceptanceTransferActDTO;
import uz.uzkassa.silen.dto.acceptancetransferact.AcceptanceTransferActProductDTO;
import uz.uzkassa.silen.dto.act.AcceptanceTransferActDetailDTO;
import uz.uzkassa.silen.dto.act.AcceptanceTransferActListDTO;
import uz.uzkassa.silen.dto.act.AcceptanceTransferActProductSilenDTO;
import uz.uzkassa.silen.dto.act.AcceptanceTransferActSilenDTO;
import uz.uzkassa.silen.dto.callback.AcceptanceTransferActCallback;
import uz.uzkassa.silen.dto.filter.AcceptanceTransferActFilter;
import uz.uzkassa.silen.enumeration.ActStatus;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.ProductGroup;
import uz.uzkassa.silen.enumeration.Status;
import uz.uzkassa.silen.integration.SupplierClient;
import uz.uzkassa.silen.repository.mongo.ShipmentMarkRepository;
import uz.uzkassa.silen.repository.pg.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AcceptanceTransferActService extends BaseService {
    AcceptanceTransferActRepository actRepository;
    AcceptanceTransferActProductRepository actProductRepository;
    ShipmentRepository shipmentRepository;
    ShipmentMarkRepository shipmentMarkRepository;
    ShipmentItemRepository shipmentItemRepository;
    SupplierClient supplierClient;
    ContractRepository contractRepository;
    ExciseRepository exciseRepository;
    CustomerProductRepository customerProductRepository;

    @Transactional
    public AcceptanceTransferActDetailDTO create(AcceptanceTransferActSilenDTO dto) {
        Optional<AcceptanceTransferAct> firstByOrganizationAndCustomerAndActNumber = actRepository.findFirstByOrganizationIdAndActNumber(getCurrentOrganizationId(), dto.getActNumber());
        if (firstByOrganizationAndCustomerAndActNumber.isPresent()) {
            throw badRequest("С таким номерам Акт уже создан").get();
        }
        AcceptanceTransferAct act = new AcceptanceTransferAct();
        act.setActId(UUID.randomUUID().toString());
        act.setActNumber(dto.getActNumber());
        act.setActDate(dto.getActDate());
        act.setContractId(dto.getContractId());
        act.setCustomerId(dto.getCustomerId());
        act.setDescription(dto.getDescription());
        act.setOrganizationId(getCurrentOrganizationId());
        act.setComission(dto.getComission());
        act.setTotalCost(dto.getTotalCost());
        act.setTotalDiscount(dto.getTotalDiscount());
        act.setTotalPayment(dto.getTotalPayment());
        act.setStoreId(dto.getStoreId());
        for (AcceptanceTransferActProductSilenDTO actProductDTO : dto.getProducts()) {
            AcceptanceTransferActProduct actProduct = new AcceptanceTransferActProduct();
            actProduct.setOrganizationId(getCurrentOrganizationId());
            actProduct.setProductId(actProductDTO.getProductId());
            actProduct.setQuantity(actProductDTO.getQuantity());
            actProduct.setPrice(actProductDTO.getPrice());
            actProduct.setSerialId(actProductDTO.getSerialId());
            actProduct.setCost(actProductDTO.getCost());
            actProduct.setDiscount(actProductDTO.getDiscount());
            actProduct.setPaymentSum(actProductDTO.getPaymentSum());
            act.addProduct(actProduct);
        }
        return actRepository.save(act).toDTO();
    }

    @Transactional
    public AcceptanceTransferActDetailDTO update(String id, AcceptanceTransferActSilenDTO actDTO) {
        AcceptanceTransferAct act = actRepository.findById(id).orElseThrow(notFound());
        if (actRepository.existsByOrganizationIdAndActNumberAndIdNot(getCurrentOrganizationId(), actDTO.getActNumber(), id)) {
            throw badRequest("С таким номерам Акт уже создан").get();
        }
        act.setActNumber(actDTO.getActNumber());
        act.setActDate(actDTO.getActDate());
        act.setContractId(actDTO.getContractId());
        act.setCustomerId(actDTO.getCustomerId());
        act.setDescription(actDTO.getDescription());
        act.setComission(actDTO.getComission());
        act.setTotalCost(actDTO.getTotalCost());
        act.setTotalDiscount(actDTO.getTotalDiscount());
        act.setTotalPayment(actDTO.getTotalPayment());
        act.setSupplierBranchId(actDTO.getSupplierBranchId());
        act.setStoreId(actDTO.getStoreId());
        for (AcceptanceTransferActProductSilenDTO actProductDTO : actDTO.getProducts()) {
            AcceptanceTransferActProduct actProduct = Optional.ofNullable(actProductDTO.getId())
                    .flatMap(actProductRepository::findById)
                    .orElse(new AcceptanceTransferActProduct());
            actProduct.setId(actProductDTO.getId());
            actProduct.setProductId(actProductDTO.getProductId());
            actProduct.setSerialId(actProductDTO.getSerialId());
            actProduct.setQuantity(actProductDTO.getQuantity());
            actProduct.setPrice(actProductDTO.getPrice());
            actProduct.setCost(actProductDTO.getCost());
            actProduct.setDiscount(actProductDTO.getDiscount());
            actProduct.setPaymentSum(actProductDTO.getPaymentSum());
            act.addProduct(actProduct);
        }
        return actRepository.save(act).toDTO();
    }

    public Page<AcceptanceTransferActListDTO> findAllByFilter(AcceptanceTransferActFilter filter) {
        filter.setYear(LocalDate.now().getYear());
        return actRepository.findAllByFilter(filter).map(act -> {
            AcceptanceTransferActListDTO listDTO = new AcceptanceTransferActListDTO();
            listDTO.setId(act.getId());
            listDTO.setActId(act.getActId());
            listDTO.setActNumber(act.getActNumber());
            listDTO.setActDate(act.getActDate());
            listDTO.setDescription(act.getDescription());
            listDTO.setComission(act.getComission());
            listDTO.setCreatedDate(act.getCreatedDate());
            listDTO.setTotalCost(act.getTotalCost());
            listDTO.setStatus(act.getStatus().toSelectItem());

            if (act.getContract() != null) {
                listDTO.setContract(act.getContract().toSelectItem());
            }
            if (act.getCustomer() != null) {
                listDTO.setCustomer(act.getCustomer().toSelectItem());
            }
            if (act.getStore() != null) {
                listDTO.setStore(act.getStore().toSelectItem());
            }
            return listDTO;
        });
    }

    public AcceptanceTransferActDetailDTO findById(String id) {
        AcceptanceTransferAct transferAct = actRepository.findById(id).orElseThrow(notFound());
        AcceptanceTransferActDetailDTO dto = transferAct.toDTO();
        for (AcceptanceTransferActProduct actProduct : transferAct.getProducts()) {
            AcceptanceTransferActProductSilenDTO productDTO = actProduct.toDTO();
            if (actProduct.getShipmentItemId() != null) {
                List<ShipmentMark> shipmentMarks = shipmentMarkRepository.findAllByShipmentItemId(actProduct.getShipmentItemId());
                for (ShipmentMark shipmentMark : shipmentMarks) {
                    PackageType packageType = Optional.ofNullable(shipmentMark.getUnit()).flatMap(integer -> Optional.of(PackageType.fromCode(integer))).orElse(PackageType.BOTTLE);
                    if (PackageType.BOTTLE.equals(packageType)) {
                        productDTO.addMark(shipmentMark.getPrintCode());
                    } else if (Arrays.asList(PackageType.BOX, PackageType.PALLET).contains(packageType)) {
                        productDTO.addAggregation(shipmentMark.getPrintCode());
                    } else if (PackageType.BLOCK.equals(packageType)) {
                        productDTO.addBlock(shipmentMark.getPrintCode());
                    }
                }
            }
            dto.addProducts(productDTO);
        }
        return dto;
    }

    @Transactional
    public void delete(String id) {
        AcceptanceTransferAct acceptanceTransferAct = actRepository.findById(id).orElseThrow(notFound("Акт не найден"));
        if (ActStatus.DRAFT.equals(acceptanceTransferAct.getStatus())) {
            actRepository.delete(acceptanceTransferAct);
            if (acceptanceTransferAct.getShipmentId() != null) {
                cacheService.evictCollectionData("uz.uzkassa.silen.domain.Shipment.acts", acceptanceTransferAct.getShipmentId());
            }
        } else {
            throw badRequest("Толко '" + ActStatus.DRAFT.getText() + "' акт можно удалит").get();
        }

    }

    public List<SelectItem> getItems(AcceptanceTransferActFilter filter) {
        filter.setYear(LocalDate.now().getYear());
        return actRepository.findAllByFilter(filter).map(AcceptanceTransferAct::toSelectItem).getContent();
    }

    @Transactional
    public String convertShipmentToAct(String id) {
        final Shipment shipment = shipmentRepository.findById(id).orElseThrow(notFound());

        AcceptanceTransferAct act = new AcceptanceTransferAct();
        act.setActId(UUID.randomUUID().toString());
        act.setActNumber(shipment.getNumber());
        act.setActDate(shipment.getShipmentDate().toLocalDate());
//        act.setContractId(?);
        act.setCustomerId(shipment.getCustomerId());
        act.setStoreId(shipment.getStoreId());
        act.setOrganizationId(shipment.getOrganizationId());
        act.setDescription(shipment.getDescription());
        act.setShipment(shipment);
        act.setShipmentId(shipment.getId());
        boolean hasFinanceDiscount = false;
        final boolean isPharma = ProductGroup.Pharma == shipment.getOrganization().getProductGroup();
        if (shipment.getCustomer() != null) {
            act.setComission(shipment.getCustomer().getComission());

            // check this buyer have contract from client base
            final List<Contract> contracts = contractRepository.findAllByOrganizationIdAndCustomerTinAndStatusAndDeletedFalse(shipment.getOrganizationId(), shipment.getCustomer().getTin(), Status.ACCEPTED);
            if (CollectionUtils.isNotEmpty(contracts)) {
                act.setContractId(contracts.get(0).getId());
            }
            hasFinanceDiscount = shipment.getCustomer().isHasFinancialDiscount();
        }

        List<ShipmentItem> shipmentItems = shipmentItemRepository.findAllByShipmentId(id);
        for (ShipmentItem shipmentItem : shipmentItems) {
            AcceptanceTransferActProduct actProduct = new AcceptanceTransferActProduct();
            actProduct.setProductId(shipmentItem.getProductId());
            actProduct.setQuantity(shipmentItem.getQty().intValue());
            actProduct.setOrganizationId(shipment.getOrganizationId());
            actProduct.setSerialId(shipmentItem.getSerialId());
            actProduct.setShipmentItemId(shipmentItem.getId());

            if (shipmentItem.getProduct() == null) continue;
            final Product product = shipmentItem.getProduct();

            actProduct.setCost(BigDecimal.ZERO);
            actProduct.setDiscount(BigDecimal.ZERO);
            actProduct.setPaymentSum(BigDecimal.ZERO);
            BigDecimal price = Optional.ofNullable(product.getPrice()).orElse(BigDecimal.ZERO);
            actProduct.setPrice(price);
            if (shipment.getCustomer() != null) {
                final Map<String, CustomerProduct> discountProducts = customerProductRepository.findAllByCustomerId(shipment.getCustomer().getId()).stream().collect(Collectors.toMap(CustomerProduct::getProductId, Function.identity()));
                if (price.compareTo(BigDecimal.ZERO) != 0 && discountProducts.containsKey(shipmentItem.getProductId())) {
                    final CustomerProduct discountProduct = discountProducts.get(shipmentItem.getProductId());
                    BigDecimal discountAmount = discountProduct.getDiscount();
                    if (discountProduct.getDiscountPercent() != null) {
                        discountAmount = price.divide(new BigDecimal(100)).setScale(3, RoundingMode.HALF_UP).multiply(discountProduct.getDiscountPercent());
                    }
                    if (!hasFinanceDiscount) {
                        if (discountAmount != null && price.compareTo(discountAmount) > 0) {
                            actProduct.setPrice(price.subtract(discountAmount));
                        }
                    }

                }
            }


            if (isPharma) {
                BigDecimal profitRate = Optional.ofNullable(shipmentItem.getProfitRate()).orElse(BigDecimal.ZERO);
                final BigDecimal profitSumma = product.getBasePrice().divide(BigDecimal.valueOf(100)).multiply(profitRate);
                actProduct.setPrice(product.getBasePrice().add(profitSumma));
            }
            final Optional<Excise> excise = exciseRepository.findOneByType(product.getType());
            if (excise.isPresent() && excise.get().getAmount().compareTo(BigDecimal.ZERO) != 0) {
                final BigDecimal exciseRate = excise.get().calculateRate(product.getCapacity(), product.getTitleAlcohol());
                actProduct.setPrice(actProduct.getPrice().add(exciseRate));
            }
            actProduct.setCost(BigDecimal.valueOf(actProduct.getQuantity()).multiply(actProduct.getPrice()));
            actProduct.setDiscount(actProduct.getCost().divide(BigDecimal.valueOf(100)).multiply(act.getComission()));
            actProduct.setPaymentSum(actProduct.getCost().subtract(Optional.of(actProduct.getDiscount()).orElse(BigDecimal.ZERO)));

            act.setTotalCost(act.getTotalCost().add(actProduct.getCost()));
            act.setTotalDiscount(act.getTotalDiscount().add(actProduct.getDiscount()));
            act.setTotalPayment(act.getTotalPayment().add(actProduct.getPaymentSum()));


            act.addProduct(actProduct);
        }
        actRepository.save(act);
        cacheService.evictCollectionData("uz.uzkassa.silen.domain.Shipment.acts", shipment.getId());
        return act.getId();
    }

    public void sendAcceptanceTransferActToSupplyQueue(String id) {
        actRepository.findById(id).ifPresent(acceptanceTransferAct -> {
            AcceptanceTransferActDTO acceptanceTransferActDTO = acceptanceTransferAct.toActDTO();
            acceptanceTransferActDTO.setProducts(toProductsDTO(acceptanceTransferAct));
            supplierClient.sendAcceptanceTransferAct(acceptanceTransferActDTO);
        });
    }

    public Object getCustomerBranches(String tin) {
        return supplierClient.getCustomerBranches(tin);
    }

    private List<AcceptanceTransferActProductDTO> toProductsDTO(AcceptanceTransferAct acceptanceTransferAct) {
        AtomicInteger ordNo = new AtomicInteger(0);
        return acceptanceTransferAct.getProducts().stream().map(acceptanceTransferActProduct -> {
            AcceptanceTransferActProductDTO acceptanceTransferActProductDTO = new AcceptanceTransferActProductDTO();
            acceptanceTransferActProductDTO.setOrdNo(ordNo.getAndIncrement());
            acceptanceTransferActProductDTO.setName(acceptanceTransferActProduct.getProduct().getName());
            acceptanceTransferActProductDTO.setCatalogCode(acceptanceTransferActProduct.getProduct().getCatalogCode());
            acceptanceTransferActProductDTO.setCatalogName(acceptanceTransferActProduct.getProduct().getCatalogName());
            acceptanceTransferActProductDTO.setBarcode(acceptanceTransferActProduct.getProduct().getBarcode());
            acceptanceTransferActProductDTO.setPackageCode(acceptanceTransferActProduct.getProduct().getPackageCode());
            acceptanceTransferActProductDTO.setPackageName(acceptanceTransferActProduct.getProduct().getPackageName());
            if (acceptanceTransferActProduct.getQuantity() != null) {
                acceptanceTransferActProductDTO.setCount(BigDecimal.valueOf(acceptanceTransferActProduct.getQuantity()));
            }
            acceptanceTransferActProductDTO.setPrice(acceptanceTransferActProduct.getPrice());
            acceptanceTransferActProductDTO.setSumma(acceptanceTransferActProduct.getCost());
            if (acceptanceTransferActProduct.getSerialNumber() != null) {
                acceptanceTransferActProductDTO.setSerial(acceptanceTransferActProduct.getSerialNumber().getSerialNumber());
            }

            if (acceptanceTransferActProduct.getShipmentItemId() != null) {
                shipmentMarkRepository.findAllByShipmentItemId(acceptanceTransferActProduct.getShipmentItemId()).forEach(shipmentMark -> {
                    PackageType packageType = Optional.ofNullable(shipmentMark.getUnit()).flatMap(integer -> Optional.of(PackageType.fromCode(integer))).orElse(PackageType.BOTTLE);
                    if (PackageType.BOTTLE.equals(packageType)) {
                        acceptanceTransferActProductDTO.addMark(shipmentMark.getPrintCode());
                    } else if (Arrays.asList(PackageType.BOX, PackageType.PALLET).contains(packageType)) {
                        acceptanceTransferActProductDTO.addAggregation(shipmentMark.getPrintCode());
                    } else if (PackageType.BLOCK.equals(packageType)) {
                        acceptanceTransferActProductDTO.addBlock(shipmentMark.getPrintCode());
                    }
                });
            }
            return acceptanceTransferActProductDTO;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void updateStatus(String actId, ActStatus actStatus) {
        AcceptanceTransferAct acceptanceTransferAct = actRepository.findById(actId).orElseThrow(notFound("Акт не найден"));

        if (ActStatus.DRAFT.equals(acceptanceTransferAct.getStatus()) && ActStatus.SENT.equals(actStatus)) {
            acceptanceTransferAct.setStatus(actStatus);
            actRepository.save(acceptanceTransferAct);
            sendAcceptanceTransferActToSupplyQueue(actId);
            return;
        } else if (ActStatus.ACCEPT.equals(acceptanceTransferAct.getStatus()) && ActStatus.CANCEL.equals(actStatus)) {
            acceptanceTransferAct.setStatus(actStatus);
            actRepository.save(acceptanceTransferAct);
            return;
        }
        throw badRequest("Статус нельзя поменять на " + actStatus.getText()).get();
    }

    @Transactional
    public void callback(AcceptanceTransferActCallback callback) {
        AcceptanceTransferAct acceptanceTransferAct = actRepository.findById(callback.getId()).orElse(null);
        if (acceptanceTransferAct == null) {
            return;
        }

        if (ActStatus.SENT.equals(acceptanceTransferAct.getStatus())) {
            acceptanceTransferAct.setStatus(callback.getStatus());
            acceptanceTransferAct.setRejectNote(callback.getRejectNotes());
            actRepository.save(acceptanceTransferAct);
        }
    }

    @Transactional
    public void removeProductItem(String productItemId) {
        AcceptanceTransferActProduct productItem = actProductRepository.findById(productItemId).orElseThrow(notFound());
        actProductRepository.delete(productItem);
    }
}
