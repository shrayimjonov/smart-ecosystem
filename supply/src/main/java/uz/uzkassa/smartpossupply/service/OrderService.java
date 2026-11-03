package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.Lgota;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.address.District;
import uz.uzkassa.smartpossupply.domain.address.Region;
import uz.uzkassa.smartpossupply.domain.category.Category;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyCommittent;
import uz.uzkassa.smartpossupply.domain.company.Partners;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.domain.factura.Contract;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.domain.order.OrderProduct;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteCustomerBranch;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteItem;
import uz.uzkassa.smartpossupply.domain.pricelist.PriceList;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerial;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.domain.trip.Trip;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseExpeditor;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseProduct;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDateDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.category.CategoryDTO;
import uz.uzkassa.smartpossupply.dto.companycommittent.CompanyCommittentDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerWithLimitDTO;
import uz.uzkassa.smartpossupply.dto.customer.TerminalCustomerListDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchAddressDTO;
import uz.uzkassa.smartpossupply.dto.lgota.LgotaDTO;
import uz.uzkassa.smartpossupply.dto.offlineorder.OfflineOrderDTO;
import uz.uzkassa.smartpossupply.dto.offlineorder.OfflineOrderUpdateDTO;
import uz.uzkassa.smartpossupply.dto.offlineorder.OrderUpdateDTO;
import uz.uzkassa.smartpossupply.dto.order.AgentTrackingOrderListDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderDetailDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderExpeditorReportDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderExpeditorTotalQtyReportDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderListDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderListForTransferDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderProductDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderProductDetailDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderReportDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderStatusUpdateDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderUpdateStatusDTO;
import uz.uzkassa.smartpossupply.dto.order.ProductExpeditorReportCreateDTO;
import uz.uzkassa.smartpossupply.dto.order.ProductExpeditorReportDTO;
import uz.uzkassa.smartpossupply.dto.order.ProductExpeditorResponseDTO;
import uz.uzkassa.smartpossupply.dto.order.SalesAndCustomerReportDTO;
import uz.uzkassa.smartpossupply.dto.order.SalesAndCustomerSummaryDTO;
import uz.uzkassa.smartpossupply.dto.order.SmartPosOrderWebHookDTO;
import uz.uzkassa.smartpossupply.dto.order.hippo.OrderHippoDTO;
import uz.uzkassa.smartpossupply.dto.order.hippo.OrderProductHippoDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileOrderDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileOrderDetailDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileOrderListDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileOrderListForExpeditorDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileOrderProductDetailDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileOrderStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileReturnOrderDTO;
import uz.uzkassa.smartpossupply.dto.order.onec.Order1CDTO;
import uz.uzkassa.smartpossupply.dto.order.onec.Order1CListDTO;
import uz.uzkassa.smartpossupply.dto.order.onec.OrderProduct1CDTO;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalOrderDetailDTO;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalOrderListDTO;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalOrderProductDetailDTO;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalOrderStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.dto.planroute.AgentTrackingDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductExpeditorListDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductListTableDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductLookUpWithWarehouseQtyDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.trip.OrderTripDTO;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.enums.DeliveryType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.OfflineOrderStatus;
import uz.uzkassa.smartpossupply.enums.OrderSource;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.OrderType;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.PlanRouteCustomerBranchStatus;
import uz.uzkassa.smartpossupply.enums.PlanRouteStatus;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.enums.ProductListType;
import uz.uzkassa.smartpossupply.enums.ProductOrigin;
import uz.uzkassa.smartpossupply.enums.SortOrderType;
import uz.uzkassa.smartpossupply.enums.TemplateType;
import uz.uzkassa.smartpossupply.enums.TemplateTypeAlias;
import uz.uzkassa.smartpossupply.enums.TripStatus;
import uz.uzkassa.smartpossupply.enums.Vat;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.filters.OrderReportFilter;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.integration.SmartposClient;
import uz.uzkassa.smartpossupply.mappers.OrderMobileMapper;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqNotificationProducer;
import uz.uzkassa.smartpossupply.repository.CategoryRepository;
import uz.uzkassa.smartpossupply.repository.CompanyCommittentWarehouseRepository;
import uz.uzkassa.smartpossupply.repository.ContractRepository;
import uz.uzkassa.smartpossupply.repository.CustomerBranchRepository;
import uz.uzkassa.smartpossupply.repository.DistrictRepository;
import uz.uzkassa.smartpossupply.repository.OfflineOrderRepository;
import uz.uzkassa.smartpossupply.repository.OrderProductRepository;
import uz.uzkassa.smartpossupply.repository.OrderRepository;
import uz.uzkassa.smartpossupply.repository.PartnerRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteCustomerBranchRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteExpeditorRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteRepository;
import uz.uzkassa.smartpossupply.repository.PriceListRepository;
import uz.uzkassa.smartpossupply.repository.ProductContractWarehouseRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.repository.ProductSerialWarehouseRepository;
import uz.uzkassa.smartpossupply.repository.RegionRepository;
import uz.uzkassa.smartpossupply.repository.TripRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseExpeditorRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseProductRepository;
import uz.uzkassa.smartpossupply.repository.WarehouseRepository;
import uz.uzkassa.smartpossupply.utils.DateUtils;
import uz.uzkassa.smartpossupply.utils.MarkUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.validator.ValidationContext;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderService extends BaseService {

    OrderRepository orderRepository;
    OrderProductRepository orderProductRepository;
    WarehouseProductRepository warehouseProductRepository;
    ProductService productService;
    PlanRouteRepository planRouteRepository;
    TemplateEngine templateEngine;
    OrderMobileMapper orderMobileMapper;
    PlanRouteCustomerBranchRepository planRouteCustomerBranchRepository;
    ContractRepository contractRepository;
    CategoryRepository categoryRepository;
    StockInService stockInService;
    WarehouseRepository warehouseRepository;
    CustomerBranchRepository customerBranchRepository;
    PriceListRepository priceListRepository;
    LgotaService lgotaService;
    ProductSerialService productSerialService;
    ContractService contractService;
    CompanyCommittentService companyCommittentService;
    CategoryService categoryService;
    CustomerService customerService;
    SmartposClient smartposClient;
    PartnerRepository partnerRepository;
    ProductContractWarehouseRepository productContractWarehouseRepository;
    ProductSerialWarehouseRepository productSerialWarehouseRepository;
    CompanyCommittentWarehouseRepository companyCommittentWarehouseRepository;
    PlanRouteService planRouteService;
    WarehouseExpeditorRepository warehouseExpeditorRepository;
    ProductRepository productRepository;
    PlanRouteExpeditorRepository planRouteExpeditorRepository;
    PackageQuantityService packageQuantityService;
    NumberingContext numberingContext;
    OfflineOrderRepository offlineOrderRepository;
    CustomerBranchService customerBranchService;
    OfflineOrderService offlineOrderService;
    RegionRepository regionRepository;
    DistrictRepository districtRepository;
    TransactionalExecutorService transactionalExecutorService;
    AgentTrackingService agentTrackingService;
    EntityManager entityManager;
    TripRepository tripRepository;
    TripService tripService;
    RabbitMqNotificationProducer notificationProducer;
    ValidationContext validationContext;

    @Transactional
    public Long create(OrderDTO orderDTO) {
        validationContext.validate(orderDTO);
        return this.createEntity(orderDTO).getId();
    }

    @Transactional
    public Order createEntity(OrderDTO orderDTO) {
        Company supplier;
        if (orderDTO.getSupplierId() != null) {
            supplier = companyRepository.findById(orderDTO.getSupplierId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", orderDTO.getSupplierId()));
        } else if (StringUtils.isNotEmpty(orderDTO.getSupplierTin())) {
            supplier = companyRepository.findByTinAndDeletedIsFalse(orderDTO.getSupplierTin()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", orderDTO.getSupplierTin()));
        } else {
            supplier = getCurrentCompany();
        }
        Company customer;
        if (AppType.SMARTPOS.equals(orderDTO.getAppType())) {
            if (StringUtils.isNotEmpty(orderDTO.getCustomerTin())) {
                customer = companyRepository.findByTinAndDeletedIsFalse(orderDTO.getCustomerTin()).orElse(companyRepository.findFirstByTinAndDeletedIsFalse(orderDTO.getCustomerTin()).orElse(null));
                if (customer == null) {
                    customer = customerService.create(orderDTO.getCustomerTin());
                }
            } else {
                throw validationService.badRequestExceptionThrow("Customer TIN required").get();
            }
        } else {
            customer = StringUtils.isNotEmpty(orderDTO.getCustomerTin()) ?
                companyRepository.findByTinAndDeletedIsFalse(orderDTO.getCustomerTin()).orElse(companyRepository.findFirstByTinAndDeletedIsFalse(orderDTO.getCustomerTin()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", orderDTO.getCustomerTin()))) :
                companyRepository.findById(orderDTO.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", orderDTO.getCustomerId()));
        }

        if (PaymentType.TRANSFER == orderDTO.getPaymentType() && StringUtils.isEmpty(customer.getTin())) {
            throw validationService.badRequestExceptionThrow("invalid.customer.tin").get();
        }

        if (!partnerRepository.existsBySupplierIdAndCustomerIdAndCompanyId(supplier.getId(), customer.getId(), supplier.getId())) {
            partnerRepository.save(new Partners(supplier.getId(), customer.getId(), supplier.getId()));
        }
        if (orderDTO.getDeliveryBranchId() != null && orderDTO.getAgentId() == null) {
            companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.WORK_WITH_AGENT)
                .ifPresent(companySettings -> {
                    if (companySettings.isEnabled()) {
                        planRouteService.getLastPlanRouteByCustomerBranch(orderDTO.getDeliveryBranchId(), supplier.getId()).ifPresent(planRoute -> {
                            orderDTO.setAgentId(planRoute.getAgentId());
                            orderDTO.setSource(OrderSource.PLAN_ROUTE.getCode());
                            orderDTO.setPlanRouteId(planRoute.getId());
                            for (PlanRouteItem planRouteItem : planRoute.getItems()) {
                                for (PlanRouteCustomerBranch planRouteCustomerBranch : planRouteItem.getPlanRouteCustomerBranches()) {
                                    if (orderDTO.getDeliveryBranchId().equals(planRouteCustomerBranch.getId())) {
                                        orderDTO.setPlanRouteCustomerBranchId(planRouteCustomerBranch.getId());
                                        break;
                                    }
                                }
                            }
                            if (orderDTO.getExpeditorId() == null) {
                                planRoute.getExpeditors().stream().findFirst().ifPresent(planRouteExpeditor -> orderDTO.setExpeditorId(planRouteExpeditor.getExpeditorId()));
                            }
                        });
                    }
                });
        }

        Order order = new Order();
        if (StringUtils.isNotEmpty(orderDTO.getOrderNumber())) {
            order.setOrderNumber(orderDTO.getOrderNumber());
        } else {
            Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.ORDER, supplier.getId());
            order.setOrderNumber(numberingMap.get(NUMBER));
            order.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        }
        order.setOrderType(Optional.ofNullable(orderDTO.getOrderType()).orElse(OrderType.OUTGOING));
        order.setMarketCode(orderDTO.getMarketCode());
        order.setCustomerId(customer.getId());
        order.setCustomer(customer);
        order.setSupplierId(supplier.getId());
        order.setCompanyId(supplier.getId());
        order.setBranchId(orderDTO.getBranchId());
        order.setWarehouseId(orderDTO.getWarehouseId());
        order.setVehicleId(orderDTO.getVehicleId());
        order.setOrderDate(orderDTO.getOrderDate());
        order.setContractId(orderDTO.getContractId());
        order.setDescription(orderDTO.getDescription());
        order.setStatus(Optional.ofNullable(orderDTO.getStatus()).orElse(OrderStatus.NEW));
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setTotalForeignPrice(orderDTO.getTotalForeignPrice());
        order.setExchangeRate(orderDTO.getExchangeRate());
        order.setCurrencyType(orderDTO.getCurrencyType());
        order.setTotalPriceWithoutVat(orderDTO.getTotalPriceWithoutVat());
        order.setTotalQty(orderDTO.getTotalQty());
        order.setTotalVatPrice(orderDTO.getTotalVatPrice());
        order.setDeliveryBranchId(orderDTO.getDeliveryBranchId());
        order.setCreateStockIn(orderDTO.isCreateStockIn());
        order.setExpeditorId(orderDTO.getExpeditorId());
        order.setAgentId(orderDTO.getAgentId());
        order.setTotalBrutto(orderDTO.getTotalBrutto());
        order.setTotalNetto(orderDTO.getTotalNetto());
        order.setPlanRouteCustomerBranchId(orderDTO.getPlanRouteCustomerBranchId());
        order.setPlanRouteId(orderDTO.getPlanRouteId());
        order.setTotalDiscountPrice(orderDTO.getTotalDiscountPrice());
        if (StringUtils.isNotEmpty(orderDTO.getSource())) {
            order.setOrderSource(OrderSource.valueOf(orderDTO.getSource()));
        }
        if (AppType.SMARTPOS.equals(orderDTO.getAppType())) {
            if (DeliveryType.DELIVERY.equals(orderDTO.getDeliveryType()) && orderDTO.getDeliveryAddress() != null) {
                Address deliveryAddress = addressMapper.toEntity(orderDTO.getDeliveryAddress());
                if (orderDTO.getDeliveryAddress().getRegionCode() != null) {
                    Region region = regionRepository.findByCode(orderDTO.getDeliveryAddress().getRegionCode()).orElseThrow(validationService.entityNotFoundExceptionThrow(Region.class.getSimpleName(), "code", orderDTO.getDeliveryAddress().getRegionCode()));
                    deliveryAddress.setRegionId(region.getId());
                }
                if (orderDTO.getDeliveryAddress().getDistrictCode() != null) {
                    District district = districtRepository.findByRegionCodeAndCode(orderDTO.getDeliveryAddress().getRegionCode(), orderDTO.getDeliveryAddress().getDistrictCode()).orElseThrow(validationService.entityNotFoundExceptionThrow(District.class.getSimpleName(), "code", orderDTO.getDeliveryAddress().getDistrictCode()));
                    deliveryAddress.setDistrictId(district.getId());
                }
                order.setDeliveryAddress(deliveryAddress);
            } else if (DeliveryType.SELF_PICKUP.equals(orderDTO.getDeliveryType())) {
                if (orderDTO.getBranchId() != null) {
                    branchRepository.findById(orderDTO.getBranchId()).ifPresent(branch -> order.setDeliveryAddress(branch.getAddress()));
                }
            }
            order.setOrderSource(OrderSource.MARKETPLACE);
        } else {
            order.setOrderSource(OrderSource.valueOf(Optional.ofNullable(orderDTO.getSource()).orElse(OrderSource.MANUAL.getCode())));
        }
        order.setSaleType(orderDTO.getSaleType());
        order.setAdvancePaymentAmount(orderDTO.getAdvancePaymentAmount());
        order.setAdvancePaymentPercent(orderDTO.getAdvancePaymentPercent());
        order.setSpecificationDate(orderDTO.getSpecificationDate());
        order.setSpecificationNumber(orderDTO.getSpecificationNumber());
        order.setPriceListId(orderDTO.getPriceListId());
        order.setFrom1C(orderDTO.isFrom1C());
        order.setFromHippo(orderDTO.isFromHippo());
        order.setDeliveryType(orderDTO.getDeliveryType());
        order.setAppType(orderDTO.getAppType());
        order.setDeliveryDate(orderDTO.getDeliveryDate());
        order.setPaymentType(orderDTO.getPaymentType());
        order.setSku(BigDecimal.valueOf(orderDTO.getProducts().size()));

        if (orderDTO.getAdvancePaymentPercent() != null && orderDTO.getTotalPrice() != null) {
            order.setAdvancePaymentPercent(orderDTO.getAdvancePaymentPercent());
            order.setAdvancePaymentAmount(orderDTO.getTotalPrice().multiply(orderDTO.getAdvancePaymentPercent()).divide(BigDecimal.valueOf(100L), 2, RoundingMode.HALF_EVEN));
        }
        orderRepository.save(order);
        int sOrder = 0;
        int orderTotalQtyInPackage = 0;
        for (OrderProductDTO orderProductDTO : orderDTO.getProducts()) {
            validateOrder(orderProductDTO.getId(), orderProductDTO);
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProductId(orderProductDTO.getId());
            orderProduct.setWarehouseId(orderProductDTO.getWarehouseId());
            orderProduct.setUnitId(orderProductDTO.getUnitId());

            orderProduct.setQty(orderProductDTO.getQty());
            orderProduct.setPrice(orderProductDTO.getPrice());
            orderProduct.setPriceWithoutVat(orderProductDTO.getPriceWithoutVat());

            orderProduct.setTotalPrice(orderProductDTO.getTotalPrice());
            orderProduct.setTotalForeignPrice(orderProductDTO.getTotalForeignPrice());
            orderProduct.setTotalPriceWithoutVat(orderProductDTO.getTotalPriceWithoutVat());

            orderProduct.setVatRate(orderProductDTO.getVatRate());
            orderProduct.setVatPrice(orderProductDTO.getVatPrice());
            orderProduct.setTotalVatPrice(orderProductDTO.getTotalVatPrice());
            orderProduct.setExciseRate(orderProductDTO.getExciseRate());
            orderProduct.setExciseSum(orderProductDTO.getExciseSum());

            orderProduct.setPriceListPrice(orderProductDTO.getPriceListPrice());

            orderProduct.setExpiryDate(orderProductDTO.getExpiryDate());
            orderProduct.setCode1C(orderProductDTO.getCode1C());
            orderProduct.setBasePrice(orderProductDTO.getBasePrice());
            orderProduct.setProductSerialId(orderProductDTO.getProductSerialId());
            orderProduct.setProductContractId(orderProductDTO.getProductContractId());

            orderProduct.setMarkupPrice(orderProductDTO.getMarkupPrice());
            orderProduct.setMarkupPercent(orderProductDTO.getMarkupPercent());

            orderProduct.setCompanyCommittentId(orderProductDTO.getCompanyCommittentId());
            orderProduct.setCommittentVatRegCode(orderProductDTO.getCommittentVatRegCode());
            orderProduct.setCommittentVatRegStatus(orderProductDTO.getCommittentVatRegStatus());

            orderProduct.setLgotaVatSum(orderProductDTO.getLgotaVatSum());
            orderProduct.setTotalBrutto(orderProductDTO.getTotalBrutto());
            orderProduct.setTotalNetto(orderProductDTO.getTotalNetto());
            orderProduct.setDiscountId(orderProductDTO.getDiscountId());
            orderProduct.setDiscountItemProductId(orderProductDTO.getDiscountItemProductId());
            orderProduct.setSOrder(++sOrder);
            orderProduct.setDiscountPercent(orderProductDTO.getDiscountPercent());
            orderProduct.setDiscountPrice(orderProductDTO.getDiscountPrice());
            orderProduct.setTotalDiscountPrice(orderProductDTO.getTotalDiscountPrice());
            orderProduct.setDiscountProductUniqueId(orderProductDTO.getDiscountProductUniqueId());
            orderProduct.setParentDiscountProductUniqueId(orderProductDTO.getParentDiscountProductUniqueId());
            orderProduct.setForeignPrice(orderProductDTO.getForeignPrice());
            Product product = productRepository.findById(orderProductDTO.getId()).orElse(null);
            if (product != null && product.getQtyInPackage() != null && product.getQtyInPackage().signum() > 0) {
                int totalQtyInPackage = Optional.ofNullable(orderProduct.getQty()).orElse(BigDecimal.ZERO).divide(product.getQtyInPackage(), RoundingMode.FLOOR).intValue();
                orderTotalQtyInPackage = orderTotalQtyInPackage + totalQtyInPackage;
                orderProduct.setTotalQtyInPackage(totalQtyInPackage);
            }
            if (OrderSource.MANUAL.equals(order.getOrderSource()) || OrderSource.PLAN_ROUTE.equals(order.getOrderSource())) {
                companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(order.getCompanyId(), GenericSettingsType.WORK_WITH_RESERVATION)
                    .ifPresent(companySettings -> {
                        if (companySettings.isEnabled()) {
                            WarehouseProduct warehouseProduct = warehouseProductRepository
                                .findByWarehouseIdAndProductIdAndUnitId(Optional.ofNullable(order.getWarehouseId()).orElse(orderProduct.getWarehouseId()), orderProduct.getProductId(), orderProduct.getUnitId())
                                .orElseGet(WarehouseProduct::new);
                            if (warehouseProduct.getId() == null) {
                                warehouseProduct.setWarehouseId(order.getWarehouseId());
                                warehouseProduct.setProductId(orderProduct.getProductId());
                                warehouseProduct.setUnitId(orderProduct.getUnitId());
                            }
                            warehouseProduct.addReservationQty(Optional.ofNullable(orderProduct.getQty()).orElse(BigDecimal.ZERO));
                            if (Optional.ofNullable(warehouseProduct.getQty()).orElse(BigDecimal.ZERO).subtract(warehouseProduct.getReservationQty()).compareTo(BigDecimal.ZERO) < 0) {
                                if (product != null) {
                                    throw new BadRequestException(localizationService.localize(MessageConstants.RESERVATION_QTY_MORE_THAN_QTY_IN_WAREHOUSE, new Object[]{Optional.ofNullable(product.getName()).orElse(""), Optional.ofNullable(product.getBarcode()).orElse("")}));
                                }
                            }
                            warehouseProductRepository.save(warehouseProduct);
                        }
                    });
            }
            orderProduct.setOrderId(order.getId());
            orderProduct.setOrder(order);
            orderProductRepository.save(orderProduct);
            if (!CollectionUtils.isEmpty(orderProductDTO.getPackageQuantities())) {
                packageQuantityService.create(orderProduct, orderProductDTO.getPackageQuantities());
            }
        }
        order.setTotalQtyInPackage(orderTotalQtyInPackage);
        orderRepository.saveAndFlush(order);

        if (SecurityUtils.isFromMobile()) {
            notificationProducer.send(TemplateType.ORDER_FROM_AGENT, order.getCompanyId(), Map.of(
                    TemplateTypeAlias.AGENT, order.getAgentId(),
                    TemplateTypeAlias.ORDER_NUMBER, order.getOrderNumber(),
                    TemplateTypeAlias.CLIENT_NAME, order.getCustomer().getName()
            ));
        }

        return order;
    }

    @Transactional
    public void create(Payload payload) {
        offlineOrderRepository.findById(payload.getId()).ifPresent(offlineOrder -> {
            try {
                OfflineOrderDTO offlineOrderJsonDTO = (OfflineOrderDTO) offlineOrder.getOrderJson();

                if (offlineOrderJsonDTO.getCustomerId() == null && offlineOrderJsonDTO.getCustomerMobileId() != null) {
                    companyRepository.findByMobileId(offlineOrderJsonDTO.getCustomerMobileId()).ifPresentOrElse(company -> {
                        offlineOrderJsonDTO.setCustomerId(company.getId());
                    }, () -> {
                        if (offlineOrderJsonDTO.getCustomer() != null) {
                            offlineOrderJsonDTO.getCustomer().setMobileId(offlineOrderJsonDTO.getCustomerMobileId());
                            offlineOrderJsonDTO.setCustomerId(customerService.create(offlineOrderJsonDTO.getCustomer()).getId());
                        }
                    });
                }

                if (offlineOrderJsonDTO.getDeliveryBranchId() == null && offlineOrderJsonDTO.getDeliveryBranchMobileId() != null) {
                    customerBranchRepository.findByMobileId(offlineOrderJsonDTO.getDeliveryBranchMobileId()).ifPresentOrElse(customerBranch -> {
                        offlineOrderJsonDTO.setDeliveryBranchId(customerBranch.getBranchId());
                    }, () -> {
                        if (offlineOrderJsonDTO.getDeliveryBranch() != null) {
                            offlineOrderJsonDTO.getDeliveryBranch().setMobileId(offlineOrderJsonDTO.getDeliveryBranchMobileId());
                            offlineOrderJsonDTO.getDeliveryBranch().setCustomerId(offlineOrderJsonDTO.getCustomerId());
                            offlineOrderJsonDTO.setDeliveryBranchId(customerBranchService.create(offlineOrderJsonDTO.getDeliveryBranch()));
                        }
                    });
                }

                if (offlineOrderJsonDTO.getContractId() == null && offlineOrderJsonDTO.getContractMobileId() != null) {
                    contractRepository.findByMobileId(offlineOrderJsonDTO.getContractMobileId()).ifPresentOrElse(contract -> {
                        offlineOrderJsonDTO.setContractId(contract.getId());
                    }, () -> {
                        if (offlineOrderJsonDTO.getContract() != null) {
                            offlineOrderJsonDTO.getContract().setMobileId(offlineOrderJsonDTO.getContractMobileId());
                            offlineOrderJsonDTO.getContract().setCustomerId(offlineOrderJsonDTO.getCustomerId());
                            offlineOrderJsonDTO.setContractId(contractService.create(offlineOrderJsonDTO.getContract()));
                        }
                    });
                }

                offlineOrderJsonDTO.setId(offlineOrder.getId());
                Long orderId = this.create(this.mapOfflineToOrderDTO(offlineOrderJsonDTO));
                offlineOrderService.update(new OfflineOrderUpdateDTO(offlineOrder.getId(), orderId, OfflineOrderStatus.SUCCESS, null));

            } catch (Exception e) {
                log.error("Error while creating order from offlineOrder {}", e.getMessage(), e);
                transactionalExecutorService.runInNewTransaction(() -> offlineOrderService.update(new OfflineOrderUpdateDTO(offlineOrder.getId(), null, OfflineOrderStatus.FAIL, e.getMessage())));

            }
        });
    }

    @Transactional
    public void update(Payload payload) {
        offlineOrderRepository.findById(payload.getId()).ifPresent(offlineOrder -> {
            try {
                OrderUpdateDTO offlineOrderJsonDTO = (OrderUpdateDTO) offlineOrder.getOrderJson();
                Long orderId = offlineOrderJsonDTO.getOrderId();
                if (orderId == null) {
                    throw new BadRequestException("Order id is required for offline update");
                }
                OrderDTO orderDTO = mapOfflineUpdateToOrderDTO(offlineOrderJsonDTO);
                this.update(orderId, orderDTO);
                offlineOrderService.update(new OfflineOrderUpdateDTO(offlineOrder.getId(), orderId, OfflineOrderStatus.SUCCESS, null));

            } catch (Exception e) {
                log.error("Error while updating order from offlineOrder {}", e.getMessage(), e);
                transactionalExecutorService.runInNewTransaction(() ->
                        offlineOrderService.update(new OfflineOrderUpdateDTO(offlineOrder.getId(), offlineOrder.getOrderId(), OfflineOrderStatus.FAIL, e.getMessage()
                        ))
                );
            }
        });
    }


    @Transactional
    public MobileReturnOrderDTO createOffline(MobileOrderDTO mobileOrderDTO) {
        if (!customerBranchService.validateCustomerBranchBelongsToCustomer(mobileOrderDTO.getDeliveryBranchId(), mobileOrderDTO.getCustomerId())) {
            throw new BadRequestException("ТТ не принадлежит эту клиенту");
        }
        mobileOrderDTO.setOrderType(OrderType.INCOMING);
        mobileOrderDTO.setBranchId(Optional.ofNullable(mobileOrderDTO.getBranchId()).orElse(SecurityUtils.getCurrentBranchId()));
        mobileOrderDTO.setWarehouseId(Optional.ofNullable(mobileOrderDTO.getWarehouseId()).orElseGet(SecurityUtils::getCurrentWarehouseId));
        if (mobileOrderDTO.getBranchId() == null && mobileOrderDTO.getWarehouseId() != null) {
            warehouseRepository.findById(mobileOrderDTO.getWarehouseId()).ifPresent(warehouse -> mobileOrderDTO.setBranchId(warehouse.getBranchId()));
        }
        OrderDTO orderDTO = orderMobileMapper.toDTO(mobileOrderDTO);
        orderDTO.setSource(OrderSource.PLAN_ROUTE.getCode());
        orderDTO.setPlanRouteCustomerBranchId(mobileOrderDTO.getPlanRouteCustomerBranchId());
        PlanRouteCustomerBranch planRouteCustomerBranch = null;
        orderDTO.setAgentId(SecurityUtils.getCurrentUserId());
        if (mobileOrderDTO.getPlanRouteCustomerBranchId() != null) {
            planRouteCustomerBranch = planRouteCustomerBranchRepository.findById(mobileOrderDTO.getPlanRouteCustomerBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteCustomerBranch.class.getSimpleName(), "id", mobileOrderDTO.getPlanRouteCustomerBranchId()));
            if (planRouteCustomerBranch.getPlanRouteItem().getPlanRoute() != null) {
                orderDTO.setPlanRouteId(planRouteCustomerBranch.getPlanRouteItem().getPlanRouteId());
                if (!CollectionUtils.isEmpty(planRouteCustomerBranch.getPlanRouteItem().getPlanRoute().getExpeditors())) {
                    orderDTO.setExpeditorId(planRouteCustomerBranch.getPlanRouteItem().getPlanRoute().getExpeditors().getFirst().getExpeditorId());
                }
            }
        }

        Order order = this.createEntity(orderDTO);
        if (planRouteCustomerBranch != null) {
            planRouteCustomerBranch.setOrderId(order.getId());
            planRouteCustomerBranchRepository.save(planRouteCustomerBranch);
        }

        agentTrackingService.create(
                new AgentTrackingDTO(EntityTypeEnum.ORDER, order.getId(), mobileOrderDTO.getLongitude(),mobileOrderDTO.getLatitude()));
        return new MobileReturnOrderDTO(order.getId(), order.getOrderNumber());
    }

    @Transactional
    public Long create(MobileOrderDTO mobileOrderDTO){
        try {
            log.info("Mobile Order create request. userId={}, companyId={}, json={}",
                    SecurityUtils.getCurrentUserId(),
                    getCurrentCompanyId(),
                    objectMapper.writeValueAsString(mobileOrderDTO));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize MobileOrderDTO for logging: {}", e.getMessage(), e);
        }

        try {
            return this.createOffline(mobileOrderDTO).getOrderId();
        } catch (Exception e) {
            log.error("Error creating mobile order. userId(agent)={}, customerId={}, deliveryBranchId={}, " +
                            "warehouseId={}, branchId={}, planRouteCustomerBranchId={}, message={}",
                    SecurityUtils.getCurrentUserId(),
                    mobileOrderDTO.getCustomerId(),
                    mobileOrderDTO.getDeliveryBranchId(),
                    mobileOrderDTO.getWarehouseId(),
                    mobileOrderDTO.getBranchId(),
                    mobileOrderDTO.getPlanRouteCustomerBranchId(),
                    e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Long create(Order1CDTO order1CDTO) {
        order1CDTO.setSource(OrderSource.ONE_C.getCode());
        return this.create(order1CDTO, null);
    }

    @Transactional
    public Long create(OrderHippoDTO orderHippoDTO) {
        /*try {
            log.info("Hippo Order JSON : {}", objectMapper.writeValueAsString(orderHippoDTO));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }*/
        this.validateOrderHippo(orderHippoDTO);
        if (orderRepository.existsByCompanyIdAndOrderNumberAndDeletedIsFalse(getCurrentCompanyId(), orderHippoDTO.getOrderNumber())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM, new Object[]{localizationService.localize(Order.class.getSimpleName()), localizationService.localize(MessageConstants.ORDER_NUMBER), orderHippoDTO.getOrderNumber()})).get();
        }
        Company customer = companyRepository.findFirstByTinAndDeletedIsFalse(orderHippoDTO.getCustomerTin()).orElse(null);
        if (customer == null) {
            customer = customerService.create(orderHippoDTO.getCustomerTin());
        }
        CustomerBranch customerBranch = customerBranchRepository.findFirstByCustomerIdAndMarketCode(customer.getId(), orderHippoDTO.getMarketCode()).orElseThrow(validationService.entityNotFoundExceptionThrow(CustomerBranch.class.getSimpleName(), "marketCode", orderHippoDTO.getMarketCode()));

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setFromHippo(true);
        orderDTO.setSource(OrderSource.HIPPO.getCode());
        orderDTO.setCustomerId(customer.getId());
        orderDTO.setOrderType(OrderType.INCOMING);
        orderDTO.setOrderNumber(orderHippoDTO.getOrderNumber());
        orderDTO.setOrderDate(orderHippoDTO.getOrderDate());
        orderDTO.setMarketCode(orderHippoDTO.getMarketCode());
        orderDTO.setDescription(orderHippoDTO.getDescription());
        orderDTO.setBranchId(customerBranch.getBranchId());
        orderDTO.setWarehouseId(customerBranch.getWarehouseId());

        if (StringUtils.isEmpty(orderHippoDTO.getContractNumber()) || orderHippoDTO.getContractDate() == null) {
            String message = "Заказ " + orderHippoDTO.getOrderNumber() +
                " Клиент " + customer.getName() + " " + customer.getTin() +
                " Отсутствуют данные по договору: номер или дата." +
                " Укажите данные и попробуйте еще раз";
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), message).get();
        }
        Contract contract = contractService.createOrGet(ContractDTO.builder()
            .customerId(customer.getId())
            .contractDate(orderHippoDTO.getContractDate())
            .contractNumber(orderHippoDTO.getContractNumber())
            .companyId(getCurrentCompanyId())
            .build());
        orderDTO.setContractId(contract.getId());


        Unit unit = getUnit();
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalPriceWithoutVat = BigDecimal.ZERO;
        BigDecimal totalVatPrice = BigDecimal.ZERO;

        for (OrderProductHippoDTO orderProductHippoDTO : orderHippoDTO.getProducts()) {
            Product product = productService.getProductByBarcodeAndVatBarcode(orderProductHippoDTO.getBarcode(), orderProductHippoDTO.getCatalogCode(), customerBranch.getBranchId());
            if (product == null) {
                throw validationService.badRequestExceptionThrow("Товар не найден по этому штрихкоду " + orderProductHippoDTO.getBarcode()).get();
            }
            OrderProductDTO orderProductDTO = new OrderProductDTO();
            orderProductDTO.setId(product.getId());
            orderProductDTO.setQty(orderProductHippoDTO.getCount());
            orderProductDTO.setWarehouseId(customerBranch.getWarehouseId());
            totalQty = totalQty.add(orderProductHippoDTO.getCount());
            totalPrice = totalPrice.add(orderProductHippoDTO.getDeliverySumWithVat());
            totalPriceWithoutVat = totalPriceWithoutVat.add(orderProductHippoDTO.getDeliverySum());
            totalVatPrice = totalVatPrice.add(orderProductHippoDTO.getVatSum());
            if (orderProductHippoDTO.getVatRate() == null || BigDecimal.ZERO.compareTo(orderProductHippoDTO.getVatRate()) == 0) {
                orderProductDTO.setPrice(orderProductHippoDTO.getSumma());
            } else {
                orderProductDTO.setPrice(orderProductHippoDTO.getDeliverySumWithVat().divide(orderProductHippoDTO.getCount(), 2, RoundingMode.CEILING));
            }
            orderProductDTO.setPriceWithoutVat(orderProductHippoDTO.getSumma());

            orderProductDTO.setTotalPrice(orderProductHippoDTO.getDeliverySumWithVat());
            orderProductDTO.setTotalPriceWithoutVat(orderProductHippoDTO.getDeliverySum());

            orderProductDTO.setVatRate(orderProductHippoDTO.getVatRate());
            if (orderProductHippoDTO.getVatSum() != null && BigDecimal.ZERO.compareTo(orderProductHippoDTO.getVatSum()) != 0) {
                orderProductDTO.setVatPrice(orderProductHippoDTO.getVatSum().divide(orderProductHippoDTO.getCount(), 2, RoundingMode.CEILING));
            }
            orderProductDTO.setTotalVatPrice(orderProductHippoDTO.getVatSum());
            orderProductDTO.setUnitId(unit.getId());
            orderDTO.addProduct(orderProductDTO);
        }
        orderDTO.setTotalPrice(totalPrice);
        orderDTO.setTotalQty(totalQty);
        orderDTO.setTotalPriceWithoutVat(totalPriceWithoutVat);
        orderDTO.setTotalVatPrice(totalVatPrice);
        return this.create(orderDTO);
    }

    @Transactional
    public Long create(Order1CDTO order1CDTO, Long currentCompanyId) {
        /*try {
            log.info("1C Order JSON :{}", objectMapper.writeValueAsString(order1CDTO));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }*/
        if (order1CDTO.getWarehouseId() == null) {
            order1CDTO.getProducts().forEach(orderProduct1CDTO -> {
                if (orderProduct1CDTO.getWarehouseId() == null) {
                    throw validationService.badRequestExceptionThrow("Выбрать склад для товара. Штрих-код продукта: ".concat(orderProduct1CDTO.getBarcode()), "").get();
                }
            });
        }
/*
        Company customer = companyRepository.findFirstByTinAndDeletedIsFalse(order1CDTO.getCustomerTin()).orElse(null);
        if (customer == null) {
            customer = customerService.create(order1CDTO.getCustomerTin());
        }*/
        Company customer = null;
        if (order1CDTO.getCustomerTin() != null) {
            customer = companyRepository.findFirstByTinAndDeletedIsFalse(order1CDTO.getCustomerTin()).orElse(null);
        }
        if (customer == null && order1CDTO.getCustomerCode() != null) {
            customer = partnerRepository.findFirstByExternalIdAndDeletedIsFalse(order1CDTO.getCustomerCode()).map(Partners::getCustomer).orElse(null);
        }
        if (customer == null) {
            customer = customerService.create(order1CDTO.getCustomerTin());
        }

        Company company;
        if (getCurrentCompanyId() == null && currentCompanyId != null) {
            company = companyRepository.findById(currentCompanyId).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", currentCompanyId));
        } else {
            company = getCurrentCompany();
        }
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setFrom1C(true);
        orderDTO.setSource(order1CDTO.getSource());
        orderDTO.setSupplierTin(company.getTin());
        orderDTO.setOrderType(OrderType.INCOMING);
        orderDTO.setOrderNumber(order1CDTO.getOrderNumber());
        orderDTO.setOrderDate(order1CDTO.getOrderDate());
        orderDTO.setDescription(order1CDTO.getDescription());
        orderDTO.setBranchId(order1CDTO.getBranchId());
        orderDTO.setWarehouseId(order1CDTO.getWarehouseId());
        orderDTO.setCustomerTin(order1CDTO.getCustomerTin());
        orderDTO.setCustomerId(customer.getId());
        orderDTO.setAdvancePaymentPercent(order1CDTO.getAdvancePaymentPercent());
        orderDTO.setSpecificationDate(order1CDTO.getSpecificationDate());
        orderDTO.setSpecificationNumber(order1CDTO.getSpecificationNumber());
        orderDTO.setCreateStockIn(order1CDTO.isCreateStockIn());
        orderDTO.setPaymentType(order1CDTO.getPaymentType());

        if (StringUtils.isNotEmpty(order1CDTO.getDeliveryBranchCode())) {
            customerBranchRepository.findFirstByExternalIdAndDeletedIsFalse(order1CDTO.getDeliveryBranchCode())
                    .map(CustomerBranch::getId)
                    .ifPresent(orderDTO::setDeliveryBranchId);
        }

        if (order1CDTO.getContract() != null) {
            if (StringUtils.isEmpty(order1CDTO.getContract().getNumber()) || order1CDTO.getContract().getDate() == null) {
                String message = "Заказ " + order1CDTO.getOrderNumber() +
                    " Клиент " + customer.getName() + " " + customer.getTin() +
                    " Отсутствуют данные по договору: номер или дата." +
                    " Укажите данные и попробуйте еще раз";
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), message).get();
            }
            Contract contract = contractService.createOrGet(ContractDTO.builder()
                .customerId(customer.getId())
                .contractDate(order1CDTO.getContract().getDate())
                .contractNumber(order1CDTO.getContract().getNumber())
                .companyId(company.getId())
                .build());
            orderDTO.setContractId(contract.getId());
        }
        Unit unit = getUnit();
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalPriceWithoutVat = BigDecimal.ZERO;
        BigDecimal totalVatPrice = BigDecimal.ZERO;
        for (OrderProduct1CDTO orderProduct1CDTO : order1CDTO.getProducts()) {
            if (orderProduct1CDTO.isHasMark()) {
                if (StringUtils.isEmpty(orderProduct1CDTO.getBarcode())) {
                    throw validationService.badRequestExceptionThrow("Требуется штрих-код продукта").get();
                }
            } else {
                if (StringUtils.isEmpty(orderProduct1CDTO.getCatalogCode())) {
                    throw validationService.badRequestExceptionThrow("Требуется ИКПУ продукта").get();
                }
            }
            Product product = productService.getProductByBarcodeAndVatBarcode(orderProduct1CDTO.getBarcode(), orderProduct1CDTO.getCatalogCode(), order1CDTO.getBranchId());
            if (product == null) {
                Category category = null;
                if (StringUtils.isNotEmpty(orderProduct1CDTO.getCatalogCode()) && StringUtils.isNotEmpty(orderProduct1CDTO.getCatalogName()) && StringUtils.isNotEmpty(orderProduct1CDTO.getCatalogName().trim())) {
                    category = categoryService.createOrGet(CategoryDTO
                        .builder()
                        .name(orderProduct1CDTO.getCatalogName())
                        .catalogName(orderProduct1CDTO.getCatalogName())
                        .catalogCode(orderProduct1CDTO.getCatalogCode())
                        .branchId(order1CDTO.getBranchId())
                        .environmentType(EnvironmentType.WEB)
                        .build()
                    );
                }
                if (category == null) {
                    category = categoryRepository.findFirstByMainIsTrueAndBranchIdAndDeletedIsFalse(order1CDTO.getBranchId()).orElse(null);
                    if (category == null) {
                        category =
                            categoryRepository
                                .findFirstByBranchIdAndDeletedIsFalse(order1CDTO.getBranchId())
                                .orElseThrow(validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.BEFORE_STOCK_IN_SYNC_YOUR_CATALOG)));
                    }
                }

                ProductDTO productDTO = ProductDTO
                    .builder()
                    .name(orderProduct1CDTO.getName())
                    .barcode(orderProduct1CDTO.getBarcode())
                    .vatBarcode(orderProduct1CDTO.getCatalogCode())
                    .catalogName(orderProduct1CDTO.getCatalogName())
                    .packageName(orderProduct1CDTO.getPackageName())
                    .packageCode(orderProduct1CDTO.getPackageCode())
                    .vat(Vat.getByAmount(orderProduct1CDTO.getVatRate()))
                    .exciseRate(orderProduct1CDTO.getExciseRate())
                    .exciseSum(orderProduct1CDTO.getExciseSum())
                    .unitId(unit.getId())
                    .price(orderProduct1CDTO.getSumma())
                    .branchId(order1CDTO.getBranchId())
                    .companyId(company.getId())
                    .categoryIds(List.of(category.getId()))
                    .hasMark(orderProduct1CDTO.isHasMark())
                    .origin(ProductOrigin.valueOf(Optional.ofNullable(orderProduct1CDTO.getOrigin()).orElse(ProductOrigin.PURCHASED.getCode())))
                    .productType(MarkUtils.getProductType(company.getProductGroup(), orderProduct1CDTO.getCatalogCode()))
                    .build();
                product = productService.createAndReturn(productDTO);
            }
            OrderProductDTO orderProductDTO = new OrderProductDTO();
            orderProductDTO.setId(product.getId());
            orderProductDTO.setQty(orderProduct1CDTO.getCount());
            orderProductDTO.setWarehouseId(order1CDTO.getWarehouseId() == null ? orderProduct1CDTO.getWarehouseId() : null);
            totalQty = totalQty.add(orderProduct1CDTO.getCount());
            totalPrice = totalPrice.add(orderProduct1CDTO.getDeliverySumWithVat());
            totalPriceWithoutVat = totalPriceWithoutVat.add(orderProduct1CDTO.getDeliverySum());
            totalVatPrice = totalVatPrice.add(orderProduct1CDTO.getVatSum());
            if (orderProduct1CDTO.getVatRate() == null || BigDecimal.ZERO.compareTo(orderProduct1CDTO.getVatRate()) == 0) {
                orderProductDTO.setPrice(orderProduct1CDTO.getSumma());
            } else {
                orderProductDTO.setPrice(orderProduct1CDTO.getDeliverySumWithVat().divide(orderProduct1CDTO.getCount(), 2, RoundingMode.CEILING));
            }
            orderProductDTO.setPriceWithoutVat(orderProduct1CDTO.getSumma());

            orderProductDTO.setTotalPrice(orderProduct1CDTO.getDeliverySumWithVat());
            orderProductDTO.setTotalPriceWithoutVat(orderProduct1CDTO.getDeliverySum());

            orderProductDTO.setVatRate(orderProduct1CDTO.getVatRate());
            if (orderProduct1CDTO.getVatSum() != null && BigDecimal.ZERO.compareTo(orderProduct1CDTO.getVatSum()) != 0) {
                orderProductDTO.setVatPrice(orderProduct1CDTO.getVatSum().divide(orderProduct1CDTO.getCount(), 2, RoundingMode.CEILING));
            }
            orderProductDTO.setTotalVatPrice(orderProduct1CDTO.getVatSum());
            orderProductDTO.setBasePrice(orderProduct1CDTO.getBaseSumma());
            orderProductDTO.setExciseRate(orderProduct1CDTO.getExciseRate());
            orderProductDTO.setExciseSum(orderProduct1CDTO.getExciseSum());

            orderProductDTO.setUnitId(unit.getId());
            orderProductDTO.setMarkupPercent(orderProduct1CDTO.getProfitRate());
            orderProductDTO.setExpiryDate(orderProduct1CDTO.getExpiryDate());
            orderProductDTO.setCode1C(orderProduct1CDTO.getCode1C());
            if (StringUtils.isNotEmpty(orderProduct1CDTO.getSerial())) {
                ProductSerial productSerial = productSerialService.createOrGet(
                    ProductSerialDTO
                        .builder()
                        .productId(product.getId())
                        .serialNumber(orderProduct1CDTO.getSerial())
                        .baseSumma(orderProduct1CDTO.getBaseSumma())
                        .build()
                );
                orderProductDTO.setProductSerialId(productSerial.getId());
                orderProductDTO.setSerialNumber(productSerial.getSerialNumber());
            }

            if (StringUtils.isNotEmpty(orderProduct1CDTO.getCommittentTin())) {
                Company committent = companyRepository.findFirstByTinAndDeletedIsFalse(orderProduct1CDTO.getCommittentTin()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", orderProduct1CDTO.getCommittentTin()));
                CompanyCommittent companyCommittent = companyCommittentService.createOrGet(CompanyCommittentDTO
                    .builder()
                    .committentId(committent.getId())
                    .companyId(company.getId())
                    .build());
                orderProductDTO.setCompanyCommittentId(companyCommittent.getId());
            }
            orderProductDTO.setCommittentVatRegCode(orderProduct1CDTO.getCommittentVatRegCode());
            orderProductDTO.setCommittentVatRegStatus(orderProduct1CDTO.getCommittentVatRegStatus());

            if (orderProduct1CDTO.getLgotaId() != null) {
                Lgota lgota = lgotaService.createOrGet(LgotaDTO
                    .builder()
                    .lgotaId(orderProduct1CDTO.getLgotaId())
                    .name(orderProduct1CDTO.getName())
                    .type(orderProduct1CDTO.getLgotaType())
                    .catalogCode(orderProduct1CDTO.getCatalogCode())
                    .build());
                orderProductDTO.setLgotaId(lgota.getId());
                orderProductDTO.setLgotaVatSum(orderProduct1CDTO.getLgotaVatSum());
            }

            orderDTO.addProduct(orderProductDTO);

        }
        orderDTO.setTotalPrice(totalPrice);
        orderDTO.setTotalQty(totalQty);
        orderDTO.setTotalPriceWithoutVat(totalPriceWithoutVat);
        orderDTO.setTotalVatPrice(totalVatPrice);
        return this.create(orderDTO);
    }

    public OrderDetailDTO get(Long id, Boolean fetchQtyInWarehouse) {
        Order order = orderRepository.getDetail(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
        this.validateCompany(order.getCompanyId());

        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        orderDetailDTO.setId(order.getId());
        orderDetailDTO.setMarketCode(order.getMarketCode());
        orderDetailDTO.setOrderNumber(order.getOrderNumber());
        orderDetailDTO.setOrderDate(order.getOrderDate());
        orderDetailDTO.setOrderType(order.getOrderType());
        orderDetailDTO.setFrom1C(order.isFrom1C());
        orderDetailDTO.setFromHippo(order.isFromHippo());
        orderDetailDTO.setAppType(order.getAppType());
        orderDetailDTO.setTotalDiscountPrice(order.getTotalDiscountPrice());
        orderDetailDTO.setTotalQtyInPackage(order.getTotalQtyInPackage());
        orderDetailDTO.setLastModifiedDate(order.getUpdatedDate());
        orderDetailDTO.setRejectReason(order.getRejectReason());
        if (order.getStockTransfer() != null) {
            orderDetailDTO.setStockTransfer(order.getStockTransfer().toCommonDTO());
            if (order.getStockTransfer().getTrip() != null) {
                orderDetailDTO.setTrip(order.getStockTransfer().getTrip().toCommonDTO());
            }
        }
        if (order.getCustomer() != null) {
            partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(order.getCompanyId(), order.getCustomerId(), order.getSupplierId()).ifPresent(partners -> orderDetailDTO.setCustomer(new CustomerWithLimitDTO(order.getCustomer().toCommonBaseDTO(), partners.getLimitBalance(), partners.getCurrentBalance(), partners.isHasLimit(), partners.getCustomer().getPhone(), partners.getCustomer().getBrand())));
        }
        if (order.getSupplier() != null) {
            orderDetailDTO.setSupplier(order.getSupplier().toCommonBaseDTO());
        }
        if (order.getBranch() != null) {
            orderDetailDTO.setBranch(order.getBranch().toCommonDTO());
        }
        if (order.getWarehouse() != null) {
            orderDetailDTO.setWarehouse(order.getWarehouse().toCommonDTO());
        }
        if (order.getVehicle() != null) {
            orderDetailDTO.setVehicle(order.getVehicle().toLookupDTO());
        }
        if (order.getCopyOrder() != null) {
            orderDetailDTO.setCopyOrder(order.getCopyOrder().toCommonDTO());
        }
        if (order.getContract() != null) {
            orderDetailDTO.setContract(order.getContract().toLookUpDTO());
        }
        if (order.getDeliveryAddress() != null) {
            orderDetailDTO.setDeliveryAddress(addressMapper.toMarketplaceDetailDTO(order.getDeliveryAddress()));
        }
        orderDetailDTO.setDeliveryType(order.getDeliveryType());
        orderDetailDTO.setStatus(order.getStatus());
        orderDetailDTO.setDescription(order.getDescription());
        orderDetailDTO.setTotalQty(order.getTotalQty());
        orderDetailDTO.setTotalPrice(order.getTotalPrice());
        orderDetailDTO.setExchangeRate(order.getExchangeRate());
        orderDetailDTO.setCurrencyType(order.getCurrencyType());
        orderDetailDTO.setTotalForeignPrice(order.getTotalForeignPrice());
        orderDetailDTO.setTotalPriceWithoutVat(order.getTotalPriceWithoutVat());
        orderDetailDTO.setTotalVatPrice(order.getTotalVatPrice());
        if (order.getDeliveryBranch() != null) {
            orderDetailDTO.setDeliveryBranch(order.getDeliveryBranch().toCommonDTO());
        }
        orderDetailDTO.setSaleType(order.getSaleType());
        orderDetailDTO.setPaymentType(order.getPaymentType());
        orderDetailDTO.setAdvancePaymentAmount(order.getAdvancePaymentAmount());
        orderDetailDTO.setAdvancePaymentPercent(order.getAdvancePaymentPercent());
        if (order.getStockOut() != null && !order.getStockOut().isDeleted()) {
            orderDetailDTO.setStock(order.getStockOut().toCommonDTO());
        }
        if (order.getStockIn() != null && !order.getStockIn().isDeleted()) {
            orderDetailDTO.setStockIn(order.getStockIn().toCommonDTO());
        }
        if (order.getPriceListId() != null) {
            orderDetailDTO.setPriceList(order.getPriceList().toCommonDTO());
        }
        orderDetailDTO.setDeliveryDate(order.getDeliveryDate());
        orderDetailDTO.setSource(order.getOrderSource());
        orderDetailDTO.setTotalBrutto(order.getTotalBrutto());
        orderDetailDTO.setTotalNetto(order.getTotalNetto());
        orderDetailDTO.setCreatedDate(order.getCreatedDate());
        orderDetailDTO.setUpdatedDate(order.getUpdatedDate());
        orderDetailDTO.setTotalQty(order.getTotalQty());
        orderDetailDTO.setSku(order.getSku());
        if (order.getExpeditorId() != null) {
            orderDetailDTO.setExpeditor(order.getExpeditor().toCommonDTO());
        }
        Map<String, List<OrderProductDetailDTO>> productMap = new LinkedHashMap<>();
        order.getProducts().forEach(orderProduct -> {
            OrderProductDetailDTO orderProductDetailDTO = new OrderProductDetailDTO();
            orderProductDetailDTO.setOrderProductId(orderProduct.getId());
            orderProductDetailDTO.setId(orderProduct.getProductId());
            orderProductDetailDTO.setName(orderProduct.getProduct().getName());
            orderProductDetailDTO.setBarcode(orderProduct.getProduct().getBarcode());
            orderProductDetailDTO.setVatBarcode(orderProduct.getProduct().getVatBarcode());
            orderProductDetailDTO.setCapacity(orderProduct.getProduct().getCapacity());
            orderProductDetailDTO.setHasMark(orderProduct.getProduct().isHasMark());
            orderProductDetailDTO.setProductType(orderProduct.getProduct().getProductType());
            orderProductDetailDTO.setBoxBarcode(orderProduct.getProduct().getBoxBarcode());
            orderProductDetailDTO.setBlockBarcode(orderProduct.getProduct().getBlockBarcode());
            if (orderProduct.getProduct().getParent() != null) {
                orderProductDetailDTO.setParent(orderProduct.getProduct().getParent().toProductCommonDTO());
                if (StringUtils.isEmpty(orderProductDetailDTO.getBlockBarcode())) {
                    orderProductDetailDTO.setBlockBarcode(orderProduct.getProduct().getParent().getBarcode());
                }
            }
            if (orderProduct.getWarehouse() != null) {
                orderProductDetailDTO.setWarehouse(orderProduct.getWarehouse().toCommonDTO());
            }
            orderProductDetailDTO.setPackageCode(orderProduct.getProduct().getPackageCode());
            orderProductDetailDTO.setPackageName(orderProduct.getProduct().getPackageName());
            orderProductDetailDTO.setUnit(orderProduct.getUnit().toDTO());
            orderProductDetailDTO.setQty(orderProduct.getQty());
            orderProductDetailDTO.setQtyInPackage(orderProduct.getProduct().getQtyInPackage());
            orderProductDetailDTO.setQtyInBox(orderProduct.getProduct().getQtyInBox());
            orderProductDetailDTO.setQtyInPallet(orderProduct.getProduct().getQtyInPallet());
            orderProductDetailDTO.setTotalBrutto(orderProduct.getTotalBrutto());
            orderProductDetailDTO.setTotalNetto(orderProduct.getTotalNetto());
            orderProductDetailDTO.setTotalDiscountPrice(orderProduct.getTotalDiscountPrice());
            orderProductDetailDTO.setDiscountPrice(orderProduct.getDiscountPrice());
            orderProductDetailDTO.setDiscountPercent(orderProduct.getDiscountPercent());
            orderProductDetailDTO.setTotalQtyInPackage(orderProduct.getTotalQtyInPackage());
            orderProductDetailDTO.setDiscountProductUniqueId(orderProduct.getDiscountProductUniqueId());
            orderProductDetailDTO.setParentDiscountProductUniqueId(orderProduct.getParentDiscountProductUniqueId());
            if (orderProduct.getDiscount() != null) {
                orderProductDetailDTO.setDiscount(orderProduct.getDiscount().toListDTO());
                if (orderProduct.getDiscountItemProduct() != null) {
                    orderProductDetailDTO.setDiscountItemProduct(orderProduct.getDiscountItemProduct().toCommonDTO());
                    orderProductDetailDTO.setDiscountValueType(orderProduct.getDiscountItemProduct().getValueType());
                    orderProductDetailDTO.setDiscountLimit(orderProduct.getDiscountItemProduct().getDiscountLimit());
                }
            }
            if (!OrderStatus.APPROVED.equals(order.getStatus()) || (fetchQtyInWarehouse != null && fetchQtyInWarehouse)) {
                orderProductDetailDTO.setQtyInWarehouse(
                    warehouseProductRepository.getQty(
                        Optional.ofNullable(order.getWarehouseId()).orElseGet(orderProduct::getWarehouseId),
                        orderProduct.getProductId(),
                        orderProduct.getUnitId()
                    )
                );
                orderProductDetailDTO.setReservationQty(
                    warehouseProductRepository.getReservationQty(
                        Optional.ofNullable(order.getWarehouseId()).orElseGet(orderProduct::getWarehouseId),
                        orderProduct.getProductId(),
                        orderProduct.getUnitId()
                    )
                );
                if (orderProduct.getProductContractId() != null) {
                    orderProductDetailDTO.setQtyInWarehouseByContract(productContractWarehouseRepository.getQty(Optional.ofNullable(order.getWarehouseId()).orElseGet(orderProduct::getWarehouseId), orderProduct.getProductContractId()));
                }
                if (orderProduct.getProductSerialId() != null) {
                    orderProductDetailDTO.setQtyInWarehouseBySerial(productSerialWarehouseRepository.getQty(Optional.ofNullable(order.getWarehouseId()).orElseGet(orderProduct::getWarehouseId), orderProduct.getProductSerialId()));
                }
                if (orderProduct.getCompanyCommittentId() != null) {
                    orderProductDetailDTO.setQtyInWarehouseByCommittent(companyCommittentWarehouseRepository.getQty(Optional.ofNullable(order.getWarehouseId()).orElseGet(orderProduct::getWarehouseId), orderProduct.getCompanyCommittentId()));
                }
            }
            orderProductDetailDTO.setPrice(orderProduct.getPrice());
            orderProductDetailDTO.setForeignPrice(orderProduct.getForeignPrice());
            orderProductDetailDTO.setPriceWithoutVat(orderProduct.getPriceWithoutVat());
            orderProductDetailDTO.setTotalPrice(orderProduct.getTotalPrice());
            orderProductDetailDTO.setTotalForeignPrice(orderProduct.getTotalForeignPrice());
            orderProductDetailDTO.setTotalPriceWithoutVat(orderProduct.getTotalPriceWithoutVat());
            orderProductDetailDTO.setVatRate(orderProduct.getVatRate());
            orderProductDetailDTO.setVatPrice(orderProduct.getVatPrice());
            orderProductDetailDTO.setTotalVatPrice(orderProduct.getTotalVatPrice());
            orderProductDetailDTO.setCatalogPrice(orderProduct.getProduct().getPrice());
            orderProductDetailDTO.setCatalogForeignPrice(orderProduct.getProduct().getForeignPrice());
            orderProductDetailDTO.setPriceListPrice(orderProduct.getPriceListPrice());
            orderProductDetailDTO.setExciseRate(orderProduct.getExciseRate());
            orderProductDetailDTO.setExciseSum(orderProduct.getExciseSum());
            orderProductDetailDTO.setBlockProducts(orderProduct.getProduct().getBlockProducts());

            orderProductDetailDTO.setMarkupPercent(orderProduct.getMarkupPercent());
            orderProductDetailDTO.setMarkupPrice(orderProduct.getMarkupPrice());

            if (orderProduct.getCompanyCommittentId() != null) {
                orderProductDetailDTO.setCompanyCommittent(orderProduct.getCompanyCommittent().toLookUpDTO());
            }
            if (orderProduct.getProductSerialId() != null) {
                orderProductDetailDTO.setProductSerial(orderProduct.getProductSerial().toLookUpDTO());
            }
            if (orderProduct.getProductContractId() != null) {
                orderProductDetailDTO.setProductContract(orderProduct.getProductContract().toCommonDTO());
            }
            orderProductDetailDTO.setExpiryDate(orderProduct.getExpiryDate());
            orderProductDetailDTO.setCode1C(orderProduct.getCode1C());
            orderProductDetailDTO.setBasePrice(orderProduct.getBasePrice());
            orderProductDetailDTO.setBrutto(orderProduct.getProduct().getWeightBrutto());
            orderProductDetailDTO.setNetto(orderProduct.getProduct().getWeightNetto());
            orderProductDetailDTO.setLgotaVatSum(orderProduct.getLgotaVatSum());
            if (StringUtils.isNotEmpty(orderProduct.getDiscountProductUniqueId())) {
                orderDetailDTO.addProduct(orderProductDetailDTO);
                productMap.put(orderProduct.getDiscountProductUniqueId(), new ArrayList<>());
            } else if (StringUtils.isNotEmpty(orderProduct.getParentDiscountProductUniqueId())) {
                if (productMap.containsKey(orderProduct.getParentDiscountProductUniqueId())) {
                    productMap.get(orderProduct.getParentDiscountProductUniqueId()).add(orderProductDetailDTO);
                } else {
                    productMap.put(orderProduct.getParentDiscountProductUniqueId(), new ArrayList<>(List.of(orderProductDetailDTO)));
                }
            } else {
                orderDetailDTO.addProduct(orderProductDetailDTO);
            }
            orderProductDetailDTO.setPackageQuantities(packageQuantityService.getByCompositeId(orderProduct));
        });
        if (!CollectionUtils.isEmpty(productMap)) {
            orderDetailDTO.getProducts().forEach(orderProductDetailDTO -> {
                if (productMap.containsKey(orderProductDetailDTO.getDiscountProductUniqueId())) {
                    orderProductDetailDTO.getDiscountProducts().addAll(productMap.get(orderProductDetailDTO.getDiscountProductUniqueId()));
                }
            });
        }
        return orderDetailDTO;
    }

    public List<OrderListForTransferDTO> getListForTransfer(OrderFilter filter) {
        return orderRepository.getListForTransfer(filter)
            .stream().map(order -> {
                OrderListForTransferDTO orderDetailDTO = new OrderListForTransferDTO();
                orderDetailDTO.setId(order.getId());
                orderDetailDTO.setOrderNumber(order.getOrderNumber());
                orderDetailDTO.setOrderDate(order.getOrderDate());
                if (order.getCustomer() != null) {
                    partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(order.getCompanyId(), order.getCustomerId(), order.getSupplierId()).ifPresent(partners -> orderDetailDTO.setCustomer(new CustomerWithLimitDTO(partners.getCustomer().toCommonBaseDTO(), partners.getLimitBalance(), partners.getCurrentBalance(), partners.isHasLimit(), partners.getCustomer().getPhone(), partners.getCustomer().getBrand())));
                }
                if (order.getBranch() != null) {
                    orderDetailDTO.setBranch(order.getBranch().toCommonDTO());
                }
                if (order.getWarehouse() != null) {
                    orderDetailDTO.setWarehouse(order.getWarehouse().toCommonDTO());
                }
                if (order.getDeliveryAddress() != null) {
                    orderDetailDTO.setDeliveryAddress(addressMapper.toMarketplaceDetailDTO(order.getDeliveryAddress()));
                }
                orderDetailDTO.setStatus(order.getStatus());
                if (order.getDeliveryBranch() != null) {
                    orderDetailDTO.setDeliveryBranch(order.getDeliveryBranch().toCommonDTO());
                }
                orderDetailDTO.setDeliveryDate(order.getDeliveryDate());
                orderDetailDTO.setTotalBrutto(order.getTotalBrutto());
                orderDetailDTO.setTotalNetto(order.getTotalNetto());
                if (order.getExpeditorId() != null) {
                    orderDetailDTO.setExpeditor(order.getExpeditor().toCommonDTO());
                }
                if (order.getPlanRouteId() != null) {
                    orderDetailDTO.setPlanRoute(order.getPlanRoute().toCommonDTO());
                }
                orderDetailDTO.setAgent(order.getAgent().toCommonDTO());
                return orderDetailDTO;
            }).toList();
    }

    public List<MobileOrderDetailDTO> getForMobileOffline(Long agentId, LocalDate date) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.atTime(LocalTime.MAX);
        List<Order> orders = orderRepository.getAllForOfflineMobile(agentId, from, to);
        return orders.stream().map(this::getDetailForMobile).toList();
    }

    public MobileOrderDetailDTO getForMobile(Long id) {
        Order order = orderRepository.getDetail(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
        this.validateCompany(order.getCompanyId());
        return this.getDetailForMobile(order);
    }

    private MobileOrderDetailDTO getDetailForMobile(Order order) {
        MobileOrderDetailDTO mobileOrderDetailDTO = new MobileOrderDetailDTO();
        mobileOrderDetailDTO.setId(order.getId());
        mobileOrderDetailDTO.setOrderNumber(order.getOrderNumber());
        mobileOrderDetailDTO.setOrderDate(order.getOrderDate());
        if (order.getCustomer() != null) {
            mobileOrderDetailDTO.setCustomer(order.getCustomer().toCommonBaseDTO());
        }
        mobileOrderDetailDTO.setStatus(order.getStatus());
        if (order.getBranch() != null) {
            mobileOrderDetailDTO.setBranch(order.getBranch().toCommonDTO());
        }
        if (order.getWarehouse() != null) {
            mobileOrderDetailDTO.setWarehouse(order.getWarehouse().toCommonDTO());
        }
        if (order.getContract() != null) {
            mobileOrderDetailDTO.setContract(order.getContract().toLookUpDTO());
        }
        mobileOrderDetailDTO.setDescription(order.getDescription());
        if (order.getDeliveryBranch() != null) {
            mobileOrderDetailDTO.setDeliveryBranch(order.getDeliveryBranch().toCommonDTO());
            mobileOrderDetailDTO.setDeliveryAddress(addressMapper.toDetailDTO(order.getDeliveryBranch().getAddress()));
        }
        mobileOrderDetailDTO.setSaleType(order.getSaleType());
        mobileOrderDetailDTO.setPaymentType(order.getPaymentType());
        mobileOrderDetailDTO.setAdvancePaymentAmount(order.getAdvancePaymentAmount());
        mobileOrderDetailDTO.setAdvancePaymentPercent(order.getAdvancePaymentPercent());

        mobileOrderDetailDTO.setTotalQty(order.getTotalQty());
        mobileOrderDetailDTO.setTotalPrice(order.getTotalPrice());
        mobileOrderDetailDTO.setTotalVatPrice(order.getTotalVatPrice());
        mobileOrderDetailDTO.setTotalBrutto(order.getTotalBrutto());
        mobileOrderDetailDTO.setTotalNetto(order.getTotalNetto());
        mobileOrderDetailDTO.setTotalDiscountPrice(order.getTotalDiscountPrice());
        mobileOrderDetailDTO.setTotalPriceWithoutVat(order.getTotalPriceWithoutVat());
        mobileOrderDetailDTO.setWithPlanRoute(order.getPlanRouteCustomerBranchId() != null);
        mobileOrderDetailDTO.setDeliveryDate(order.getDeliveryDate());
        mobileOrderDetailDTO.setLastModifiedDate(order.getUpdatedDate());
        mobileOrderDetailDTO.setRejectReason(order.getRejectReason());
        if (order.getPriceListId() != null) {
            mobileOrderDetailDTO.setPriceList(order.getPriceList().toCommonCodeDTO());
        }
        if (order.getExpeditorId() != null) {
            mobileOrderDetailDTO.setExpeditor(order.getExpeditor().toCommonDTO());
        }
        Map<String, List<MobileOrderProductDetailDTO>> productMap = new LinkedHashMap<>();

        order
            .getProducts()
            .forEach(orderProduct -> {
                MobileOrderProductDetailDTO mobileOrderProductDetailDTO = new MobileOrderProductDetailDTO();
                mobileOrderProductDetailDTO.setOrderProductId(orderProduct.getId());
                mobileOrderProductDetailDTO.setId(orderProduct.getProductId());
                mobileOrderProductDetailDTO.setName(orderProduct.getProduct().getName());
                mobileOrderProductDetailDTO.setCapacity(orderProduct.getProduct().getCapacity());
                mobileOrderProductDetailDTO.setProductType(orderProduct.getProduct().getProductType());
                mobileOrderProductDetailDTO.setVat(orderProduct.getProduct().getVat());
                mobileOrderProductDetailDTO.setUnit(orderProduct.getUnit().toDTO());
                mobileOrderProductDetailDTO.setQty(orderProduct.getQty());
                mobileOrderProductDetailDTO.setQtyInPackage(orderProduct.getProduct().getQtyInPackage());
                mobileOrderProductDetailDTO.setQtyInBox(orderProduct.getProduct().getQtyInBox());
                mobileOrderProductDetailDTO.setQtyInPallet(orderProduct.getProduct().getQtyInPallet());
                mobileOrderProductDetailDTO.setPrice(orderProduct.getPrice());
                mobileOrderProductDetailDTO.setTotalPrice(orderProduct.getTotalPrice());
                mobileOrderProductDetailDTO.setVatRate(orderProduct.getVatRate());
                mobileOrderProductDetailDTO.setVatPrice(orderProduct.getVatPrice());
                mobileOrderProductDetailDTO.setTotalVatPrice(orderProduct.getTotalVatPrice());
                mobileOrderProductDetailDTO.setExciseRate(orderProduct.getExciseRate());
                mobileOrderProductDetailDTO.setExciseSum(orderProduct.getExciseSum());
                mobileOrderProductDetailDTO.setTotalBrutto(orderProduct.getTotalBrutto());
                mobileOrderProductDetailDTO.setTotalNetto(orderProduct.getTotalNetto());
                mobileOrderProductDetailDTO.setTotalDiscountPrice(orderProduct.getTotalDiscountPrice());
                mobileOrderProductDetailDTO.setDiscountPrice(orderProduct.getDiscountPrice());
                mobileOrderProductDetailDTO.setDiscountPercent(orderProduct.getDiscountPercent());
                mobileOrderProductDetailDTO.setTotalQtyInPackage(orderProduct.getTotalQtyInPackage());
                mobileOrderProductDetailDTO.setDiscountProductUniqueId(orderProduct.getDiscountProductUniqueId());
                mobileOrderProductDetailDTO.setParentDiscountProductUniqueId(orderProduct.getParentDiscountProductUniqueId());
                mobileOrderProductDetailDTO.setPriceWithoutVat(orderProduct.getPriceWithoutVat());
                mobileOrderProductDetailDTO.setTotalPriceWithoutVat(orderProduct.getTotalPriceWithoutVat());
                if (orderProduct.getDiscount() != null) {
                    mobileOrderProductDetailDTO.setDiscount(orderProduct.getDiscount().toListDTO());
                    if (orderProduct.getDiscountItemProduct() != null) {
                        mobileOrderProductDetailDTO.setDiscountItemProduct(orderProduct.getDiscountItemProduct().toCommonDTO());
                        mobileOrderProductDetailDTO.setDiscountValueType(orderProduct.getDiscountItemProduct().getValueType());
                        mobileOrderProductDetailDTO.setDiscountLimit(orderProduct.getDiscountItemProduct().getDiscountLimit());
                    }
                }
                if (!OrderStatus.COMPLETED.equals(order.getStatus())) {
                    mobileOrderProductDetailDTO.setQtyInWarehouse(
                        warehouseProductRepository.getQty(
                            Optional.ofNullable(order.getWarehouseId()).orElse(orderProduct.getWarehouseId()),
                            orderProduct.getProductId(),
                            orderProduct.getUnitId()
                        )
                    );
                    mobileOrderProductDetailDTO.setReservationQty(
                        warehouseProductRepository.getReservationQty(
                            Optional.ofNullable(order.getWarehouseId()).orElse(orderProduct.getWarehouseId()),
                            orderProduct.getProductId(),
                            orderProduct.getUnitId()
                        )
                    );
                }
                mobileOrderProductDetailDTO.setBrutto(orderProduct.getProduct().getWeightBrutto());
                mobileOrderProductDetailDTO.setNetto(orderProduct.getProduct().getWeightNetto());
                if (StringUtils.isNotEmpty(orderProduct.getDiscountProductUniqueId())) {
                    mobileOrderDetailDTO.addProduct(mobileOrderProductDetailDTO);
                    productMap.put(orderProduct.getDiscountProductUniqueId(), new ArrayList<>());
                } else if (StringUtils.isNotEmpty(orderProduct.getParentDiscountProductUniqueId())) {
                    if (productMap.containsKey(orderProduct.getParentDiscountProductUniqueId())) {
                        productMap.get(orderProduct.getParentDiscountProductUniqueId()).add(mobileOrderProductDetailDTO);
                    } else {
                        productMap.put(orderProduct.getParentDiscountProductUniqueId(), new ArrayList<>(List.of(mobileOrderProductDetailDTO)));
                    }
                } else {
                    mobileOrderDetailDTO.addProduct(mobileOrderProductDetailDTO);
                }
            });
        if (!CollectionUtils.isEmpty(productMap)) {
            mobileOrderDetailDTO.getProducts().forEach(orderProductDetailDTO -> {
                if (productMap.containsKey(orderProductDetailDTO.getDiscountProductUniqueId())) {
                    orderProductDetailDTO.getDiscountProducts().addAll(productMap.get(orderProductDetailDTO.getDiscountProductUniqueId()));
                }
            });
        }
        return mobileOrderDetailDTO;
    }

    public TerminalOrderDetailDTO getForTerminal(Long id) {
        Order order = orderRepository.getDetail(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", id));

        this.validateCompany(order.getCompanyId());

        TerminalOrderDetailDTO terminalOrderDetailDTO = new TerminalOrderDetailDTO();
        terminalOrderDetailDTO.setId(order.getId());
        terminalOrderDetailDTO.setOrderNumber(order.getOrderNumber());
        terminalOrderDetailDTO.setOrderDate(order.getOrderDate());
        if (order.getCustomer() != null) {
            TerminalCustomerListDTO customerListDTO = new TerminalCustomerListDTO();
            customerListDTO.setId(order.getCustomerId());
            customerListDTO.setTin(order.getCustomer().getTin());
            customerListDTO.setName(order.getCustomer().getName());
            customerListDTO.setBrand(order.getCustomer().getBrand());
            if (order.getCustomer().getAddress() != null) {
                customerListDTO.setAddress(addressMapper.toDetailDTO(order.getCustomer().getAddress()));
            }
            partnerRepository.findByCompanyIdAndCustomerIdAndSupplierId(getCurrentCompanyId(), order.getCustomer().getId(), getCurrentCompanyId()).ifPresent(partners -> {
                customerListDTO.setBalance(partners.getCurrentBalance());
            });
            terminalOrderDetailDTO.setCustomer(customerListDTO);
        }
        terminalOrderDetailDTO.setStatus(order.getStatus());
        if (order.getBranch() != null) {
            terminalOrderDetailDTO.setBranch(order.getBranch().toCommonDTO());
        }
        if (order.getWarehouse() != null) {
            terminalOrderDetailDTO.setWarehouse(order.getWarehouse().toCommonDTO());
        }
        if (order.getStockOutId() != null) {
            terminalOrderDetailDTO.setStockOut(order.getStockOut().toCommonDTO());
        }

        terminalOrderDetailDTO.setDescription(order.getDescription());
        if (order.getDeliveryBranch() != null) {
            terminalOrderDetailDTO.setDeliveryBranch(order.getDeliveryBranch().toCommonDTO());
            terminalOrderDetailDTO.setDeliveryAddress(addressMapper.toDetailDTO(order.getDeliveryBranch().getAddress()));
        }
        terminalOrderDetailDTO.setSaleType(order.getSaleType());
        terminalOrderDetailDTO.setPaymentType(order.getPaymentType());
        terminalOrderDetailDTO.setAdvancePaymentAmount(order.getAdvancePaymentAmount());
        terminalOrderDetailDTO.setAdvancePaymentPercent(order.getAdvancePaymentPercent());

        terminalOrderDetailDTO.setTotalQty(order.getTotalQty());
        terminalOrderDetailDTO.setTotalPrice(order.getTotalPrice());
        terminalOrderDetailDTO.setTotalVatPrice(order.getTotalVatPrice());
        terminalOrderDetailDTO.setTotalDiscountPrice(order.getTotalDiscountPrice());
        terminalOrderDetailDTO.setRejectReason(order.getRejectReason());
        if (order.getStockTransfer() != null && order.getStockTransfer().getTrip() != null) {
            terminalOrderDetailDTO.setTrip(order.getStockTransfer().getTrip().toCommonDTO());
        }
        Map<String, List<TerminalOrderProductDetailDTO>> productMap = new LinkedHashMap<>();

        order
            .getProducts()
            .forEach(orderProduct -> {
                TerminalOrderProductDetailDTO terminalOrderProductDetailDTO = new TerminalOrderProductDetailDTO();
                terminalOrderProductDetailDTO.setOrderProductId(orderProduct.getId());
                terminalOrderProductDetailDTO.setId(orderProduct.getProductId());
                terminalOrderProductDetailDTO.setName(orderProduct.getProduct().getName());
                terminalOrderProductDetailDTO.setCapacity(orderProduct.getProduct().getCapacity());
                terminalOrderProductDetailDTO.setProductType(orderProduct.getProduct().getProductType());
                terminalOrderProductDetailDTO.setVat(orderProduct.getProduct().getVat());
                terminalOrderProductDetailDTO.setUnit(orderProduct.getUnit().toDTO());
                terminalOrderProductDetailDTO.setQty(orderProduct.getQty());
                terminalOrderProductDetailDTO.setQtyInPackage(orderProduct.getProduct().getQtyInPackage());
                terminalOrderProductDetailDTO.setQtyInBox(orderProduct.getProduct().getQtyInBox());
                terminalOrderProductDetailDTO.setQtyInPallet(orderProduct.getProduct().getQtyInPallet());
                terminalOrderProductDetailDTO.setPrice(orderProduct.getPrice());
                terminalOrderProductDetailDTO.setTotalPrice(orderProduct.getTotalPrice());
                terminalOrderProductDetailDTO.setVatRate(orderProduct.getVatRate());
                terminalOrderProductDetailDTO.setVatPrice(orderProduct.getVatPrice());
                terminalOrderProductDetailDTO.setTotalVatPrice(orderProduct.getTotalVatPrice());
                terminalOrderProductDetailDTO.setExciseRate(orderProduct.getExciseRate());
                terminalOrderProductDetailDTO.setExciseSum(orderProduct.getExciseSum());
                terminalOrderProductDetailDTO.setBarcode(orderProduct.getProduct().getBarcode());
                terminalOrderProductDetailDTO.setCatalogCode(orderProduct.getProduct().getVatBarcode());
                terminalOrderProductDetailDTO.setTotalDiscountPrice(orderProduct.getTotalDiscountPrice());
                terminalOrderProductDetailDTO.setDiscountPrice(orderProduct.getDiscountPrice());
                terminalOrderProductDetailDTO.setDiscountPercent(orderProduct.getDiscountPercent());
                terminalOrderProductDetailDTO.setDiscountProductUniqueId(orderProduct.getDiscountProductUniqueId());
                terminalOrderProductDetailDTO.setParentDiscountProductUniqueId(orderProduct.getParentDiscountProductUniqueId());
                if (orderProduct.getDiscount() != null) {
                    terminalOrderProductDetailDTO.setDiscount(orderProduct.getDiscount().toListDTO());
                }
                if (StringUtils.isNotEmpty(orderProduct.getDiscountProductUniqueId())) {
                    terminalOrderDetailDTO.addProduct(terminalOrderProductDetailDTO);
                    productMap.put(orderProduct.getDiscountProductUniqueId(), new ArrayList<>());
                } else if (StringUtils.isNotEmpty(orderProduct.getParentDiscountProductUniqueId())) {
                    if (productMap.containsKey(orderProduct.getParentDiscountProductUniqueId())) {
                        productMap.get(orderProduct.getParentDiscountProductUniqueId()).add(terminalOrderProductDetailDTO);
                    } else {
                        productMap.put(orderProduct.getParentDiscountProductUniqueId(), new ArrayList<>(List.of(terminalOrderProductDetailDTO)));
                    }
                } else {
                    terminalOrderDetailDTO.addProduct(terminalOrderProductDetailDTO);
                }
            });
        if (!CollectionUtils.isEmpty(productMap)) {
            terminalOrderDetailDTO.getProducts().forEach(orderProductDetailDTO -> {
                if (productMap.containsKey(orderProductDetailDTO.getDiscountProductUniqueId())) {
                    orderProductDetailDTO.getDiscountProducts().addAll(productMap.get(orderProductDetailDTO.getDiscountProductUniqueId()));
                }
            });
        }
        return terminalOrderDetailDTO;
    }

    @Transactional
    public Long copy(Long id) {
        Order oldOrder = orderRepository.getDetail(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.ORDER, oldOrder.getCompanyId());

        Order order = new Order();
        order.setCopyOrderId(oldOrder.getId());
        order.setOrderSource(oldOrder.getOrderSource());
        order.setOrderNumber(numberingMap.get(NUMBER));
        order.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        order.setOrderType(oldOrder.getOrderType());
        order.setCustomerId(oldOrder.getCustomerId());
        order.setSupplierId(oldOrder.getSupplierId());
        order.setCompanyId(oldOrder.getCompanyId());
        order.setBranchId(oldOrder.getBranchId());
        order.setVehicleId(oldOrder.getVehicleId());
        order.setWarehouseId(oldOrder.getWarehouseId());
        order.setOrderDate(oldOrder.getOrderDate());
        order.setContractId(oldOrder.getContractId());
        order.setDescription(oldOrder.getDescription());
        order.setStatus(OrderStatus.NEW);
        order.setTotalPrice(oldOrder.getTotalPrice());
        order.setTotalForeignPrice(oldOrder.getTotalForeignPrice());
        order.setExchangeRate(oldOrder.getExchangeRate());
        order.setCurrencyType(oldOrder.getCurrencyType());
        order.setTotalPriceWithoutVat(oldOrder.getTotalPriceWithoutVat());
        order.setTotalQty(oldOrder.getTotalQty());
        order.setTotalVatPrice(oldOrder.getTotalVatPrice());
        order.setDeliveryBranchId(oldOrder.getDeliveryBranchId());
        order.setSaleType(oldOrder.getSaleType());
        order.setPaymentType(oldOrder.getPaymentType());
        order.setAdvancePaymentAmount(oldOrder.getAdvancePaymentAmount());
        order.setAdvancePaymentPercent(oldOrder.getAdvancePaymentPercent());
        order.setPriceListId(oldOrder.getPriceListId());
        order.setFrom1C(oldOrder.isFrom1C());
        order.setFromHippo(oldOrder.isFromHippo());
        order.setDeliveryDate(oldOrder.getDeliveryDate());
        order.setAppType(oldOrder.getAppType());
        order.setTotalDiscountPrice(oldOrder.getTotalDiscountPrice());
        orderRepository.save(order);
        oldOrder
            .getProducts()
            .forEach(oldOrderProduct -> {
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setProductId(oldOrderProduct.getProductId());
                orderProduct.setWarehouseId(oldOrderProduct.getWarehouseId());
                orderProduct.setUnitId(oldOrderProduct.getUnitId());

                orderProduct.setQty(oldOrderProduct.getQty());
                orderProduct.setPrice(oldOrderProduct.getPrice());
                orderProduct.setForeignPrice(oldOrderProduct.getForeignPrice());
                orderProduct.setPriceWithoutVat(oldOrderProduct.getPriceWithoutVat());

                orderProduct.setTotalPrice(oldOrderProduct.getTotalPrice());
                orderProduct.setTotalForeignPrice(oldOrderProduct.getTotalForeignPrice());
                orderProduct.setTotalPriceWithoutVat(oldOrderProduct.getTotalPriceWithoutVat());

                orderProduct.setVatRate(oldOrderProduct.getVatRate());
                orderProduct.setVatPrice(oldOrderProduct.getVatPrice());
                orderProduct.setTotalVatPrice(oldOrderProduct.getTotalVatPrice());
                orderProduct.setExciseRate(oldOrderProduct.getExciseRate());
                orderProduct.setExciseSum(oldOrderProduct.getExciseSum());

                orderProduct.setPriceListPrice(oldOrderProduct.getPriceListPrice());

                orderProduct.setExpiryDate(oldOrderProduct.getExpiryDate());
                orderProduct.setCode1C(oldOrderProduct.getCode1C());
                orderProduct.setBasePrice(oldOrderProduct.getBasePrice());
                orderProduct.setProductSerialId(oldOrderProduct.getProductSerialId());
                orderProduct.setProductContractId(oldOrderProduct.getProductContractId());

                orderProduct.setMarkupPrice(oldOrderProduct.getMarkupPrice());
                orderProduct.setMarkupPercent(oldOrderProduct.getMarkupPercent());

                orderProduct.setCompanyCommittentId(oldOrderProduct.getCompanyCommittentId());
                orderProduct.setCommittentVatRegCode(oldOrderProduct.getCommittentVatRegCode());
                orderProduct.setCommittentVatRegStatus(oldOrderProduct.getCommittentVatRegStatus());
                orderProduct.setDiscountPrice(oldOrderProduct.getDiscountPrice());
                orderProduct.setDiscountPercent(oldOrderProduct.getDiscountPercent());
                orderProduct.setTotalDiscountPrice(oldOrderProduct.getTotalDiscountPrice());
                orderProduct.setDiscountProductUniqueId(oldOrderProduct.getDiscountProductUniqueId());
                orderProduct.setParentDiscountProductUniqueId(oldOrderProduct.getParentDiscountProductUniqueId());
                orderProduct.setDiscountId(oldOrderProduct.getDiscountId());
                orderProduct.setDiscountItemProductId(oldOrderProduct.getDiscountItemProductId());
                orderProduct.setLgotaVatSum(oldOrderProduct.getLgotaVatSum());
                if (OrderSource.MANUAL.equals(oldOrder.getOrderSource()) || OrderSource.PLAN_ROUTE.equals(oldOrder.getOrderSource())) {
                    companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(oldOrder.getCompanyId(), GenericSettingsType.WORK_WITH_RESERVATION)
                        .ifPresent(companySettings -> {
                            if (companySettings.isEnabled()) {
                                WarehouseProduct warehouseProduct = warehouseProductRepository
                                    .findByWarehouseIdAndProductIdAndUnitId(Optional.ofNullable(oldOrder.getWarehouseId()).orElse(oldOrderProduct.getWarehouseId()), oldOrderProduct.getProductId(), oldOrderProduct.getUnitId())
                                    .orElseGet(WarehouseProduct::new);
                                if (warehouseProduct.getId() == null) {
                                    warehouseProduct.setWarehouseId(oldOrder.getWarehouseId());
                                    warehouseProduct.setProductId(oldOrderProduct.getProductId());
                                    warehouseProduct.setUnitId(oldOrderProduct.getUnitId());
                                }
                                warehouseProduct.addReservationQty(Optional.ofNullable(oldOrderProduct.getQty()).orElse(BigDecimal.ZERO));
                                if (Optional.ofNullable(warehouseProduct.getQty()).orElse(BigDecimal.ZERO).subtract(warehouseProduct.getReservationQty()).compareTo(BigDecimal.ZERO) < 0) {
                                    throw new BadRequestException(localizationService.localize(MessageConstants.RESERVATION_QTY_MORE_THAN_QTY_IN_WAREHOUSE, new Object[]{oldOrderProduct.getProduct().getName(), Optional.ofNullable(oldOrderProduct.getProduct().getBarcode()).orElse("")}));
                                }
                                warehouseProductRepository.save(warehouseProduct);
                            }
                        });
                }

                order.addProduct(orderProduct);

                if (!CollectionUtils.isEmpty(packageQuantityService.getByCompositeId(oldOrderProduct))) {
                    packageQuantityService.create(orderProduct, packageQuantityService.getByCompositeId(oldOrderProduct));
                }
            });

        return order.getId();
    }

    @Transactional
    public Long update(Long id, MobileOrderDTO mobileOrderDTO) {
        /*try {
            log.info("--------------------Mobile order payload---------------");
            log.info("Payload: {}", objectMapper.writeValueAsString(mobileOrderDTO));
        } catch (JsonProcessingException e) {
            log.error("Writing object error");
        }*/
        if (SecurityUtils.isAgent()) {
            mobileOrderDTO.setBranchId(SecurityUtils.getCurrentBranchId());
        }
        mobileOrderDTO.setWarehouseId(Optional.ofNullable(mobileOrderDTO.getWarehouseId()).orElseGet(SecurityUtils::getCurrentWarehouseId));
        if (mobileOrderDTO.getBranchId() == null && mobileOrderDTO.getWarehouseId() != null) {
            warehouseRepository.findById(mobileOrderDTO.getWarehouseId()).ifPresent(warehouse -> mobileOrderDTO.setBranchId(warehouse.getBranchId()));
        }
        return this.update(id, orderMobileMapper.toDTO(mobileOrderDTO));
    }

    @Transactional
    public Long update(Long id, OrderDTO orderDTO) {
        validationContext.validate(orderDTO);
        Order order = orderRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
        if (!(OrderStatus.NEW.equals(order.getStatus()) || OrderStatus.DRAFT.equals(order.getStatus()))) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.YOU_CAN_UPDATE_ORDER_ONLY)).get();
        }
        Company customer = StringUtils.isNotEmpty(orderDTO.getCustomerTin()) ?
            companyRepository.findByTinAndDeletedIsFalse(orderDTO.getCustomerTin()).orElse(companyRepository.findFirstByTinAndDeletedIsFalse(orderDTO.getCustomerTin()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "tin", orderDTO.getCustomerTin()))) :
            companyRepository.findById(orderDTO.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", orderDTO.getCustomerId()));

        order.setCustomerId(customer.getId());
        order.setOrderDate(orderDTO.getOrderDate());
        if (orderDTO.getContractId() != null) {
            Contract contract = contractRepository.findById(orderDTO.getContractId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Contract.class.getSimpleName(), "id", orderDTO.getContractId()));
            order.setContract(contract);
            order.setContractId(contract.getId());
        }
        order.setDescription(orderDTO.getDescription());
        if (orderDTO.getBranchId() != null) {
            Branch branch = branchRepository.findById(orderDTO.getBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "id", orderDTO.getBranchId()));
            order.setBranch(branch);
            order.setBranchId(branch.getId());
        }


        if (orderDTO.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(orderDTO.getWarehouseId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Warehouse.class.getSimpleName(), "id", orderDTO.getWarehouseId()));
            order.setWarehouse(warehouse);
            order.setWarehouseId(warehouse.getId());
        }

        order.setVehicleId(orderDTO.getVehicleId());
        order.setStatus(orderDTO.getStatus());
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setTotalForeignPrice(orderDTO.getTotalForeignPrice());
        order.setExchangeRate(orderDTO.getExchangeRate());
        order.setCurrencyType(orderDTO.getCurrencyType());
        order.setPaymentType(orderDTO.getPaymentType());
        order.setTotalPriceWithoutVat(orderDTO.getTotalPriceWithoutVat());
        order.setTotalQty(orderDTO.getTotalQty());
        order.setTotalVatPrice(orderDTO.getTotalVatPrice());
        order.setSpecificationDate(orderDTO.getSpecificationDate());
        order.setSpecificationNumber(orderDTO.getSpecificationNumber());
        order.setExpeditorId(orderDTO.getExpeditorId());
        order.setTotalBrutto(orderDTO.getTotalBrutto());
        order.setTotalNetto(orderDTO.getTotalNetto());
        order.setTotalDiscountPrice(orderDTO.getTotalDiscountPrice());

        if (orderDTO.getDeliveryBranchId() != null) {
            CustomerBranch deliveryBranch = customerBranchRepository.findById(orderDTO.getDeliveryBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CustomerBranch.class.getSimpleName(), "id", orderDTO.getDeliveryBranchId()));
            order.setDeliveryBranch(deliveryBranch);
            order.setDeliveryBranchId(deliveryBranch.getId());
        }
        if (AppType.SMARTPOS.equals(orderDTO.getAppType())) {
            if (DeliveryType.DELIVERY.equals(orderDTO.getDeliveryType()) && orderDTO.getDeliveryAddress() != null) {
                Address deliveryAddress = addressMapper.toEntity(orderDTO.getDeliveryAddress());
                if (orderDTO.getDeliveryAddress().getRegionCode() != null) {
                    Region region = regionRepository.findByCode(orderDTO.getDeliveryAddress().getRegionCode()).orElseThrow(validationService.entityNotFoundExceptionThrow(Region.class.getSimpleName(), "code", orderDTO.getDeliveryAddress().getRegionCode()));
                    deliveryAddress.setRegionId(region.getId());
                }
                if (orderDTO.getDeliveryAddress().getDistrictCode() != null) {
                    District district = districtRepository.findByRegionCodeAndCode(orderDTO.getDeliveryAddress().getRegionCode(), orderDTO.getDeliveryAddress().getDistrictCode()).orElseThrow(validationService.entityNotFoundExceptionThrow(District.class.getSimpleName(), "code", orderDTO.getDeliveryAddress().getDistrictCode()));
                    deliveryAddress.setDistrictId(district.getId());
                }
                order.setDeliveryAddress(deliveryAddress);
            } else if (DeliveryType.SELF_PICKUP.equals(orderDTO.getDeliveryType())) {
                if (orderDTO.getBranchId() != null) {
                    branchRepository.findById(orderDTO.getBranchId()).ifPresent(branch -> order.setDeliveryAddress(branch.getAddress()));
                }
            }
        }
        order.setDeliveryDate(orderDTO.getDeliveryDate());
        order.setSaleType(orderDTO.getSaleType());
        if (isFromAdmin()) {
            order.setDeliveryType(orderDTO.getDeliveryType());
        }
        if (orderDTO.getAdvancePaymentPercent() != null && orderDTO.getTotalPrice() != null) {
            order.setAdvancePaymentPercent(orderDTO.getAdvancePaymentPercent());
            order.setAdvancePaymentAmount(orderDTO.getTotalPrice().multiply(orderDTO.getAdvancePaymentPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN));
        } else {
            order.setAdvancePaymentAmount(null);
            order.setAdvancePaymentPercent(null);
        }
        PriceList priceList = orderDTO.getPriceListId() == null ? null
            : priceListRepository.findById(orderDTO.getPriceListId()).orElseThrow(validationService.entityNotFoundExceptionThrow(PriceList.class.getSimpleName(), "id", orderDTO.getPriceListId()));
        order.setPriceList(priceList);
        order.setPriceListId(priceList == null ? null : priceList.getId());
        order.setSku(BigDecimal.valueOf(orderDTO.getProducts().size()));
        orderRepository.save(order);
        HashMap<Long, OrderProduct> orderProductsMap = new HashMap<>();
        boolean isCalculateReservation = false;
        if (OrderSource.MANUAL.equals(order.getOrderSource()) || OrderSource.PLAN_ROUTE.equals(order.getOrderSource())) {
            Optional<CompanySettings> optionalCompanySettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(order.getCompanyId(), GenericSettingsType.WORK_WITH_RESERVATION);
            if (optionalCompanySettings.isPresent()) {
                if (optionalCompanySettings.get().isEnabled()) {
                    isCalculateReservation = true;
                }
            }
        }
        int orderTotalQtyInPackage = 0;
        for (OrderProduct product : order.getProducts()) {
            if (isCalculateReservation) {
                WarehouseProduct warehouseProduct = warehouseProductRepository
                    .findByWarehouseIdAndProductIdAndUnitId(order.getWarehouseId(), product.getProductId(), product.getUnitId())
                    .orElse(null);
                if (warehouseProduct != null) {
                    warehouseProduct.subtractReservationQty(Optional.ofNullable(product.getQty()).orElse(BigDecimal.ZERO));
                    warehouseProductRepository.saveAndFlush(warehouseProduct);
                }
                orderProductsMap.put(product.getId(), product);
            }
            orderProductsMap.put(product.getId(), product);
        }

        for (OrderProductDTO orderProductDTO : orderDTO.getProducts()) {
            OrderProduct orderProduct;
            if (orderProductDTO.getOrderProductId() != null && orderProductsMap.get(orderProductDTO.getOrderProductId()) != null) {
                orderProduct = orderProductsMap.get(orderProductDTO.getOrderProductId());
            } else {
                orderProduct = new OrderProduct();
            }

            validateOrder(orderProductDTO.getId(), orderProductDTO);

            orderProduct.setProductId(orderProductDTO.getId());
            orderProduct.setWarehouseId(orderProductDTO.getWarehouseId());
            orderProduct.setUnitId(orderProductDTO.getUnitId());
            orderProduct.setQty(orderProductDTO.getQty());
            orderProduct.setPrice(orderProductDTO.getPrice());
            orderProduct.setForeignPrice(orderProductDTO.getForeignPrice());
            orderProduct.setPriceWithoutVat(orderProductDTO.getPriceWithoutVat());
            orderProduct.setTotalPrice(orderProductDTO.getTotalPrice());
            orderProduct.setTotalForeignPrice(orderProductDTO.getTotalForeignPrice());
            orderProduct.setTotalPriceWithoutVat(orderProductDTO.getTotalPriceWithoutVat());
            orderProduct.setVatRate(orderProductDTO.getVatRate());
            orderProduct.setVatPrice(orderProductDTO.getVatPrice());
            orderProduct.setTotalVatPrice(orderProductDTO.getTotalVatPrice());
            orderProduct.setPriceListPrice(orderProductDTO.getPriceListPrice());
            orderProduct.setExciseRate(orderProductDTO.getExciseRate());
            orderProduct.setExciseSum(orderProductDTO.getExciseSum());

            orderProduct.setMarkupPercent(orderProductDTO.getMarkupPercent());
            orderProduct.setMarkupPrice(orderProductDTO.getMarkupPrice());

            orderProduct.setCompanyCommittentId(orderProductDTO.getCompanyCommittentId());
            orderProduct.setProductSerialId(orderProductDTO.getProductSerialId());
            orderProduct.setProductContractId(orderProductDTO.getProductContractId());
            orderProduct.setExpiryDate(orderProductDTO.getExpiryDate());
            orderProduct.setCode1C(orderProductDTO.getCode1C());
            orderProduct.setBasePrice(orderProductDTO.getBasePrice());

            orderProduct.setLgotaId(orderProductDTO.getLgotaId());
            orderProduct.setLgotaVatSum(orderProductDTO.getLgotaVatSum());
            orderProduct.setTotalBrutto(orderProductDTO.getTotalBrutto());
            orderProduct.setTotalNetto(orderProductDTO.getTotalNetto());
            orderProduct.setDiscountId(orderProductDTO.getDiscountId());
            orderProduct.setDiscountItemProductId(orderProductDTO.getDiscountItemProductId());
            orderProduct.setDiscountPercent(orderProductDTO.getDiscountPercent());
            orderProduct.setDiscountPrice(orderProductDTO.getDiscountPrice());
            orderProduct.setTotalDiscountPrice(orderProductDTO.getTotalDiscountPrice());
            orderProduct.setDiscountProductUniqueId(orderProductDTO.getDiscountProductUniqueId());
            orderProduct.setParentDiscountProductUniqueId(orderProductDTO.getParentDiscountProductUniqueId());
            Product product = productRepository.findById(orderProductDTO.getId()).orElse(null);
            if (product != null && product.getQtyInPackage() != null && product.getQtyInPackage().signum() > 0) {
                int totalQtyInPackage = Optional.ofNullable(orderProduct.getQty()).orElse(BigDecimal.ZERO).divide(product.getQtyInPackage(), RoundingMode.FLOOR).intValue();
                orderTotalQtyInPackage = orderTotalQtyInPackage + totalQtyInPackage;
                orderProduct.setTotalQtyInPackage(totalQtyInPackage);
            }
            if (isCalculateReservation) {
                WarehouseProduct warehouseProduct = warehouseProductRepository
                    .findByWarehouseIdAndProductIdAndUnitId(Optional.ofNullable(order.getWarehouseId()).orElse(orderProduct.getWarehouseId()), orderProduct.getProductId(), orderProduct.getUnitId())
                    .orElseGet(WarehouseProduct::new);
                if (warehouseProduct.getId() == null) {
                    warehouseProduct.setWarehouseId(order.getWarehouseId());
                    warehouseProduct.setProductId(orderProduct.getProductId());
                    warehouseProduct.setUnitId(orderProduct.getUnitId());
                }
                warehouseProduct.addReservationQty(Optional.ofNullable(orderProduct.getQty()).orElse(BigDecimal.ZERO));
                if (Optional.ofNullable(warehouseProduct.getQty()).orElse(BigDecimal.ZERO).subtract(warehouseProduct.getReservationQty()).compareTo(BigDecimal.ZERO) < 0) {
                    if (product != null) {
                        throw new BadRequestException(localizationService.localize(MessageConstants.RESERVATION_QTY_MORE_THAN_QTY_IN_WAREHOUSE, new Object[]{product.getName(), Optional.ofNullable(product.getBarcode()).orElse("")}));
                    }
                }
                warehouseProductRepository.save(warehouseProduct);
            }
            orderProduct.setOrderId(order.getId());
            if (orderProduct.getId() != null) {
                orderProductsMap.remove(orderProduct.getId());
            }
            orderProductRepository.save(orderProduct);
            if (CollectionUtils.isEmpty(orderProductDTO.getPackageQuantities())) {
                packageQuantityService.deleteByCompositeId(orderProduct);
            } else {
                packageQuantityService.create(orderProduct, orderProductDTO.getPackageQuantities());
            }
        }
        order.setTotalQtyInPackage(orderTotalQtyInPackage);
        orderRepository.save(order);
        if (!orderProductsMap.isEmpty()) {
            orderProductRepository.deleteAllByIdInBatch(orderProductsMap.keySet());
        }
        return order.getId();
    }


    void releaseReservationOnCancellation(Order order) {
        if (order.isReservationReleased()) {
            return;
        }
        if (!(OrderSource.MANUAL.equals(order.getOrderSource()) || OrderSource.PLAN_ROUTE.equals(order.getOrderSource()))) {
            return;
        }
        companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(order.getCompanyId(), GenericSettingsType.WORK_WITH_RESERVATION)
            .ifPresent(companySettings -> {
                if (companySettings.isEnabled()) {
                    order.getProducts().forEach(orderProduct -> {
                        WarehouseProduct warehouseProduct = warehouseProductRepository
                            .findByWarehouseIdAndProductIdAndUnitId(order.getWarehouseId(), orderProduct.getProductId(), orderProduct.getUnitId())
                            .orElse(null);
                        if (warehouseProduct != null) {
                            warehouseProduct.subtractReservationQty(Optional.ofNullable(orderProduct.getQty()).orElse(BigDecimal.ZERO));
                            warehouseProductRepository.saveAndFlush(warehouseProduct);
                        }
                    });
                    order.setReservationReleased(true);
                }
            });
    }

    @Transactional
    public CheckStatusResponseDTO updateStatus(Long id, OrderStatusUpdateDTO orderStatusUpdateDTO) {
        final OrderStatus status = orderStatusUpdateDTO.getStatus();
        return orderRepository
            .findById(id)
            .map(order -> {
                if (orderStatusUpdateDTO.getLastModifiedDate() != null && !order.getUpdatedDate().equals(orderStatusUpdateDTO.getLastModifiedDate())) {
                    return new CheckStatusResponseDTO(false, 1);
                }
                if (OrderStatus.APPROVED.equals(status)) {
                    if (order.getBranchId() == null) {
                        throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.BRANCH_NOT_SELECTED)).get();
                    }
                    if (!order.getSupplierId().equals(order.getCustomerId()) && order.getContractId() == null) {
                        throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.CONTRACT_NOT_SELECTED)).get();
                    }
                }
                if (OrderSource.MANUAL.equals(order.getOrderSource()) || OrderSource.PLAN_ROUTE.equals(order.getOrderSource())) {
                    if (OrderStatus.REJECTED.equals(status)) {
                        releaseReservationOnCancellation(order);
                        order.setRejectReason(orderStatusUpdateDTO.getRejectReason());
                    }
                }
                if (AppType.SMARTPOS.equals(order.getAppType())) {
                    smartposClient.updateOrderStatus(id, new SmartPosOrderWebHookDTO(status));
                }
                if (OrderStatus.IN_DELIVERY.equals(status)) {
                    order.setStockOutId(null);
                    order.setStockOutNumber(null);
                }
                if (orderStatusUpdateDTO.getExpeditorId() != null) {
                    order.setExpeditorId(orderStatusUpdateDTO.getExpeditorId());
                }
                this.updateStatus(order, status);

                if (OrderStatus.COMPLETED.equals(orderStatusUpdateDTO.getStatus())) {
                    notificationProducer.send(TemplateType.ORDER_DELIVERED,
                            getCurrentCompanyId(), Map.of(TemplateTypeAlias.TRANSACTION_NUMBER, order.getOrderNumber()));
                }

                if ((order.isFrom1C() || order.isFromHippo()) && order.isCreateStockIn() && OrderStatus.APPROVED.equals(status)) {
                    stockInService.createByOrder(order);
                }
                return new CheckStatusResponseDTO(true, 0);
            })
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void assignExpeditor(Long id, Long expeditorId) {
        orderRepository.findById(id).ifPresentOrElse(order -> {
            this.updateStatus(id, new OrderStatusUpdateDTO(OrderStatus.APPROVED, order.getUpdatedDate(), expeditorId));
            orderRepository.save(order);
        }, () -> validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", id).get());
    }

    @Transactional
    public void updateStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Transactional
    public void updateStatus(List<OrderUpdateStatusDTO> orders, OrderStatus status) {
        orders.forEach(orderUpdateStatusDTO -> {
            if (orderUpdateStatusDTO.getId() == null) {
                throw validationService.badRequestExceptionThrow("Order id is required").get();
            }
            if (orderUpdateStatusDTO.getLastModifiedDate() == null) {
                throw validationService.badRequestExceptionThrow("Last modified date is required").get();
            }
            this.updateStatus(orderUpdateStatusDTO.getId(), new OrderStatusUpdateDTO(status, orderUpdateStatusDTO.getLastModifiedDate(), orderUpdateStatusDTO.getExpeditorId()));
        });
    }

    @Transactional
    public void updateNumber(Long id, String number) {
        if (orderRepository.existsByCompanyIdAndOrderNumberAndIdNotAndDeletedIsFalse(getCurrentCompanyId(), number, id)) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM, new Object[]{localizationService.localize(Order.class.getSimpleName()), localizationService.localize(MessageConstants.ORDER_NUMBER), number})).get();
        }
        orderRepository.findById(id).ifPresentOrElse(order -> {
            order.setOrderNumber(number);
            orderRepository.save(order);
        }, () -> validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", id).get());
    }

    @Transactional
    public void updateDeliveryDate(Long id, LocalDateTime deliveryDate) {
        orderRepository
            .findById(id)
            .ifPresentOrElse(order -> {
                if (order.getStatus() != OrderStatus.APPROVED) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.YOU_CAN_UPDATE_APPROVED_ORDER_ONLY)).get();
                }
                if (deliveryDate.toLocalDate().isBefore(LocalDateTime.now().toLocalDate())) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.DELIVERY_DATE_MUST_BE_GREATER_THAN_FROM_TODAY)).get();
                }
                order.setDeliveryDate(deliveryDate);
                orderRepository.save(order);
            }, () -> {
                throw validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", id).get();
            });
    }

    public ResultList<Order> getResultList(OrderFilter filter) {
        return orderRepository.getResultList(filter);
    }

    public Page<OrderListDTO> getList(OrderFilter filter) {
        ResultList<Order> resultList = orderRepository.getResultList(filter);
        List<OrderListDTO> result = resultList
                .getList()
                .stream().map(order -> {
                    OrderListDTO orderListDTO = new OrderListDTO();
                    orderListDTO.setId(order.getId());
                    orderListDTO.setLastModifiedDate(order.getUpdatedDate());
                    if (order.getCustomer() != null) {
                        orderListDTO.setCustomer(order.getCustomer().toCommonBaseDTO());
                    }
                    orderListDTO.setOrderDate(order.getOrderDate());
                    orderListDTO.setStatus(order.getStatus());
                    orderListDTO.setOrderNumber(order.getOrderNumber());
                    orderListDTO.setOrderType(order.getOrderType());
                    if (order.getBranch() != null) {
                        orderListDTO.setBranch(order.getBranch().toCommonDTO());
                    }
                    if (order.getStockOut() != null) {
                        orderListDTO.setStock(new CommonDateDTO(order.getStockOutId(), order.getStockOutNumber(), order.getStockOut().getTransferDate()));
                        orderListDTO.setStocks(order.getStockOuts().stream().map(stock -> new CommonDateDTO(stock.getId(), stock.getStockNumber(), stock.getTransferDate(), stock.getStatus())).collect(Collectors.toSet()));
                    }
                    if (order.getCreator() != null) {
                        orderListDTO.setCreator(order.getCreator().toCommonDTO());
                    }
                    orderListDTO.setSource(order.getOrderSource());
                    if (order.getTrip() != null) {
                        orderListDTO.setTrip(order.getTrip().toCommonDTO());
                    }
                    if (order.getPlanRoute() != null) {
                        orderListDTO.setPlanRoute(order.getPlanRoute().toCommonDTO());
                    }
                    orderListDTO.setCreatedDate(order.getCreatedDate());
                    orderListDTO.setUpdatedDate(order.getUpdatedDate());
                    orderListDTO.setPaymentType(order.getPaymentType());
                    orderListDTO.setContract(order.getContract().toLookUpDTO());
                    return orderListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<AgentTrackingOrderListDTO> getListForAgentTracking(List<Long> orderIds) {
        return orderRepository.findAllById(orderIds).stream().map(order -> {
            AgentTrackingOrderListDTO agentTrackingOrderListDTO = new AgentTrackingOrderListDTO();
            agentTrackingOrderListDTO.setId(order.getId());
            agentTrackingOrderListDTO.setOrderNumber(order.getOrderNumber());
            if (order.getDeliveryBranch() != null) {
                agentTrackingOrderListDTO.setCustomerBranch(order.getDeliveryBranch().toCommonDTO());
            }
            return agentTrackingOrderListDTO;
        }).toList();
    }

    public SalesAndCustomerSummaryDTO getSalesAndCustomerSummary(OrderFilter filter) {
        return orderRepository.getSalesAndCustomerSummary(filter);
    }

    public Page<SalesAndCustomerReportDTO> getSalesAndCustomerReport(OrderFilter filter) {
        return orderRepository.getSalesAndCustomerReport(filter);
    }

    @Transactional
    public void assignTripToOrders(OrderTripDTO orderTripDTO) {
        Trip trip = tripRepository.findById(orderTripDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Trip.class.getSimpleName(), "id", orderTripDTO.getId()));
        orderRepository.assignTrip(orderTripDTO.getOrderIds(), trip.getId());
        tripService.updateStatus(trip, TripStatus.IN_PROGRESS);
    }

    public Page<Order1CListDTO> getListFor1C(OrderFilter filter) {
        ResultList<Order> resultList = this.getResultList(filter);
        List<Order1CListDTO> result = resultList.getList().stream().map(order -> {
            Order1CListDTO order1CListDTO = new Order1CListDTO();
            order1CListDTO.setId(order.getId());
            order1CListDTO.setOrderDate(order.getOrderDate());
            order1CListDTO.setStatus(order.getStatus());
            order1CListDTO.setOrderNumber(order.getOrderNumber());
            order1CListDTO.setOrderType(order.getOrderType());
            order1CListDTO.setCreatedDate(order.getCreatedDate());
            if (order.getBranchId() != null) {
                order1CListDTO.setBranch(order.getBranch().toCommonDTO());
            }
            if (order.getVehicle() != null) {
                order1CListDTO.setVehicle(order.getVehicle().toCommonDTO());
            }
            if (order.getPlanRouteId() != null) {
                order1CListDTO.setPlanRoute(order.getPlanRoute().toCommonDTO());
            }
            if (order.getCustomerId() != null) {
                order1CListDTO.setCustomer(order.getCustomer().toCommonBaseDTO());
            }
            return order1CListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());

    }

    public Page<MobileOrderListDTO> getListForMobile(OrderFilter filter) {
        filter.setFetchDeleted(false);
        if (SecurityUtils.isAgent()) {
            filter.setCreatorId(getCurrentUserId());
        }
        ResultList<Order> resultList = orderRepository.getResultList(filter);
        List<MobileOrderListDTO> result = resultList
            .getList()
            .stream()
            .map(order -> {
                MobileOrderListDTO mobileOrderListDTO = new MobileOrderListDTO();
                mobileOrderListDTO.setId(order.getId());
                mobileOrderListDTO.setOrderNumber(order.getOrderNumber());
                if (order.getCustomerId() != null) {
                    mobileOrderListDTO.setCustomer(order.getCustomer().toCommonDTO());
                }
                mobileOrderListDTO.setOrderDate(order.getOrderDate());
                mobileOrderListDTO.setStatus(order.getStatus());
                mobileOrderListDTO.setTotalPrice(order.getTotalPrice());
                mobileOrderListDTO.setTotalDiscountPrice(order.getTotalDiscountPrice());
                mobileOrderListDTO.setDeliveryDate(order.getDeliveryDate());
                mobileOrderListDTO.setLastModifiedDate(order.getUpdatedDate());

                return mobileOrderListDTO;
            }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<TerminalOrderListDTO> getListForTerminal(OrderFilter filter) {
        WarehouseExpeditor warehouseExpeditor = warehouseExpeditorRepository.findFirstByExpeditorIdAndWarehouseIdOrderByTransferDateDesc(getCurrentUserId(), SecurityUtils.getCurrentWarehouseId()).orElse(null);
        if (warehouseExpeditor != null) {
            filter.setCarId(warehouseExpeditor.getCarId());
        }
        ResultList<Order> resultList = orderRepository.getResultListForTerminal(filter);
        List<TerminalOrderListDTO> result = resultList
            .getList()
            .stream()
            .map(order -> {
                TerminalOrderListDTO terminalOrderListDTO = new TerminalOrderListDTO();
                terminalOrderListDTO.setId(order.getId());
                terminalOrderListDTO.setOrderNumber(order.getOrderNumber());
                if (order.getCustomerId() != null) {
                    TerminalCustomerListDTO terminalCustomerListDTO = new TerminalCustomerListDTO();
                    terminalCustomerListDTO.setId(order.getCustomerId());
                    terminalCustomerListDTO.setName(order.getCustomer().getName());
                    terminalCustomerListDTO.setTin(order.getCustomer().getTin());
                    terminalCustomerListDTO.setBrand(order.getCustomer().getBrand());
                    if (order.getCustomer().getAddress() != null) {
                        terminalCustomerListDTO.setAddress(addressMapper.toDetailDTO(order.getCustomer().getAddress()));
                    }
                    terminalOrderListDTO.setCustomer(terminalCustomerListDTO);
                }
                terminalOrderListDTO.setOrderDate(order.getOrderDate());
                terminalOrderListDTO.setStatus(order.getStatus());
                terminalOrderListDTO.setTotalPrice(order.getTotalPrice());
                terminalOrderListDTO.setTotalQty(order.getTotalQty());
                terminalOrderListDTO.setTotalDiscountPrice(order.getTotalDiscountPrice());
                if (order.getStockTransfer() != null && order.getStockTransfer().getTrip() != null) {
                    terminalOrderListDTO.setTrip(order.getStockTransfer().getTrip().toCommonDTO());
                }
                return terminalOrderListDTO;
            }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        orderRepository.findById(id).map(order -> {
            if (AppType.SMARTPOS.equals(order.getAppType())) {
                if (OrderStatus.NEW.equals(order.getStatus()) || OrderStatus.DRAFT.equals(order.getStatus())) {
                    order.setStatus(OrderStatus.REJECTED);
                    orderRepository.save(order);
                    smartposClient.updateOrderStatus(id, new SmartPosOrderWebHookDTO(OrderStatus.REJECTED));
                }
                return order;
            }
            if ((OrderSource.MANUAL == order.getOrderSource() || OrderSource.PLAN_ROUTE == order.getOrderSource())
                    && (OrderStatus.DRAFT == order.getStatus() || OrderStatus.NEW == order.getStatus() || OrderStatus.APPROVED == order.getStatus())) {
                releaseReservationOnCancellation(order);
            }
            order.setStatus(OrderStatus.DELETED);
            order.setDeleted(true);
            orderRepository.save(order);
            orderProductRepository.deleteAllByOrderId(order.getId());
            return order;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long reverse(Long id) {
        return orderRepository.findById(id).map(order -> {
            if (!OrderStatus.DELETED.equals(order.getStatus())) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.YOU_CAN_ONLY_REVERSE_WITH_STATUS_DELETED)).get();
            }
            order.setDeleted(false);
            order.setStatus(OrderStatus.DELETED);
            orderRepository.save(order);
            orderProductRepository.reverseByOrderId(id);
            return order.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Order.class.getSimpleName(), "id", id));
    }

    public Page<OrderReportDTO> getReport(OrderReportFilter filter) {
        ResultList<OrderReportDTO> resultList = orderRepository.getResultListForReport(filter);
        return new PageImpl<>(resultList.getList(), filter.getSortedPageable(), resultList.getCount());
    }

    public List<CommonDTO> lookUp(OrderFilter filter) {
        return orderRepository.lookUp(filter).stream().map(Order::toCommonDTO).toList();
    }

    private void validateOrder(Long id, OrderProductDTO orderProductBaseDTO) {
        if (id == null) {
            throw validationService.badRequestExceptionThrow("Вам необходимо выбрать товар", "Product id null").get();
        }
        if (orderProductBaseDTO.getQty() == null || orderProductBaseDTO.getQty().compareTo(BigDecimal.ZERO) == 0) {
            throw validationService.badRequestExceptionThrow("Количество товара не должно быть равно нулю", "Product quantity must not be null").get();
        }
        if (orderProductBaseDTO.getDiscountId() == null && orderProductBaseDTO.getPrice() == null) {
            throw validationService.badRequestExceptionThrow("Цена продукта не может быть равна нулю", "Product price must not be null").get();
        }
        if (orderProductBaseDTO.getDiscountId() == null && orderProductBaseDTO.getTotalPrice() == null) {
            throw validationService.badRequestExceptionThrow("Общая стоимость продукта не может быть равна нулю", "Total cost must not be null").get();
        }
    }

    private void validateOrderHippo(OrderHippoDTO orderHippoDTO) {
        if (orderHippoDTO.getOrderNumber() == null) {
            throw validationService.badRequestExceptionThrow("Номер заказа отсутствует", "Order number is null").get();
        }
        if (orderHippoDTO.getMarketCode() == null) {
            throw validationService.badRequestExceptionThrow("Код торговой точки отсутствует", "Market code is null").get();
        }
        if (orderHippoDTO.getCustomerTin() == null) {
            throw validationService.badRequestExceptionThrow("ИНН клиента отсутствует", "Customer TIN is null").get();
        }
    }

    public List<ProductLookUpWithWarehouseQtyDTO> getProductLookUp(ProductFilter filter) {
        filter.setSortOrderType(SortOrderType.LIST.getCode());
        return productService.getProductLookUpWithWarehouseQty(filter);
    }

    public Page<ProductLookUpWithWarehouseQtyDTO> getProductLookUpForMobile(ProductFilter filter) {
        return new PageImpl<>(productService.getProductLookUpWithWarehouseQty(filter), filter.getSortedPageable(), filter.getSize());
    }

    public OrderStatisticsDTO getStatistics(OrderFilter filter) {
        return orderRepository.getStatistics(filter);
    }

    public MobileOrderStatisticsDTO getOrderStatisticsForMobile(BaseFilter filter) {
        MobileOrderStatisticsDTO mobileOrderStatisticsDTO = orderRepository.getOrderStatisticsForMobile(filter);
        planRouteRepository.findFirstByAgentIdAndStatusOrderByCreatedDateDesc(getCurrentUserId(), PlanRouteStatus.OPEN).ifPresent(planRoute -> {
            AtomicLong completedTotal = new AtomicLong(0L);
            AtomicLong total = new AtomicLong(0L);
            planRoute.getItems().
                forEach(planRouteItem -> planRouteItem.getPlanRouteCustomerBranches()
                    .forEach(planRouteCustomerBranch -> {
                        if (PlanRouteCustomerBranchStatus.VISITED
                            .equals(planRouteCustomerBranch.getStatus())
                        ) {
                            completedTotal.getAndIncrement();
                        }
                        total.getAndIncrement();
                    })
                );
            mobileOrderStatisticsDTO.setPlanRouteTotal(total.intValue());
            mobileOrderStatisticsDTO.setPlanRouteCompletedTotal(completedTotal.intValue());
        });
        return mobileOrderStatisticsDTO;
    }

    public ResponseEntity<?> getPDF(OrderFilter filter) {
        Context context = new Context(SecurityUtils.getLocale());
        context.setVariable("companyName", getCurrentCompany().getName());
        context.setVariable("heading", "Заказы");
        StringBuilder filterText = new StringBuilder();
        if (filter.getFrom() != null) {
            filterText.append("От: ").append(filter.getFrom().format(DateUtils.DAY_MONTH_YEAR_DOT_FORMATTER));
        }
        if (filter.getTo() != null) {
            filterText.append(" До: ").append(filter.getTo().format(DateUtils.DAY_MONTH_YEAR_DOT_FORMATTER));
        }
        context.setVariable("orders", orderRepository.getResultList(filter).getList());
        context.setVariable("filterText", filterText);

        String html = templateEngine.process("OrderTemplate", context);
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://localhost:8089");
        HtmlConverter.convertToPdf(html, pdfStream, converterProperties);
        byte[] bytes = pdfStream.toByteArray();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Orders")
            .contentType(MediaType.APPLICATION_PDF)
            .body(bytes);
    }

    public BigDecimal getRecommendedQty(Long productId) {
        return orderProductRepository.findFirstByOrder_Company_IdAndProductIdOrderByOrderIdDesc(getCurrentCompanyId(), productId).map(OrderProduct::getQty).orElse(BigDecimal.ZERO);
    }

    public Page<OrderProductQtyDetailListDTO> getProductQtyDetails(OrderFilter filter) {
        return orderRepository.getQtyDetails(filter);
    }

    private ProductExpeditorReportDTO generateAggregationTable(ProductExpeditorReportCreateDTO aggregationTableCreateDTO) {
        ProductExpeditorReportDTO aggregationTableDTO = new ProductExpeditorReportDTO();
        for (OrderProduct orderProduct : orderProductRepository.findAllByOrderIdInAndOrder_ExpeditorIdIsNotNullOrderByOrderIdDesc(aggregationTableCreateDTO.getOrderIds())) {
            CommonDTO entity;
            if (aggregationTableCreateDTO.getProductListType() != null && ProductListType.BY_CUSTOMER_BRANCH == aggregationTableCreateDTO.getProductListType()) {
                aggregationTableDTO.setProductListType(ProductListType.BY_CUSTOMER_BRANCH);
                entity = aggregationTableDTO.getCustomerBranch(orderProduct.getOrder().getDeliveryBranchId());
            } else {
                aggregationTableDTO.setProductListType(ProductListType.BY_CUSTOMER_BRANCH);
                entity = aggregationTableDTO.getExpeditor(orderProduct.getOrder().getExpeditorId());
            }
            if (entity == null) {
                if (aggregationTableCreateDTO.getProductListType() != null && ProductListType.BY_CUSTOMER_BRANCH == aggregationTableCreateDTO.getProductListType()) {
                    if (orderProduct.getOrder().getDeliveryBranchId() != null) {
                        entity = orderProduct.getOrder().getDeliveryBranch().toCommonDTO();
                    }
                } else {
                    if (orderProduct.getOrder().getExpeditorId() != null) {
                        entity = orderProduct.getOrder().getExpeditor().toCommonDTO();
                    }
                }
            }
            ProductListTableDTO product = aggregationTableDTO.getProduct(entity != null ? entity.getId() : null, orderProduct.getProductId());
            if (product == null) {
                product = new ProductListTableDTO();
                if (orderProduct.getDiscount() != null) {
                    product.setDiscount(orderProduct.getDiscount().toCommonDTO());
                }
                product.setProduct(orderProduct.getProduct().toListDTO());
                product.setPaymentType(orderProduct.getOrder().getPaymentType());
            }
            aggregationTableDTO.add(entity, product, orderProduct.getQty());
        }
        return aggregationTableDTO;
    }

    public ProductExpeditorResponseDTO generateProductExpeditorReport(ProductExpeditorReportCreateDTO aggregationTableCreateDTO) {
        ProductExpeditorReportDTO productExpeditorReportDTO = this.generateAggregationTable(aggregationTableCreateDTO);
        ProductExpeditorResponseDTO responseDTO = new ProductExpeditorResponseDTO();

        boolean enable = companySettingsRepository.existsByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(),GenericSettingsType.SORTING_PRODUCTS);
        Map<Long, Integer> productSortMap;

        if (enable) {
            productSortMap = entityManager.createQuery("""
            select ps.productId, ps.sortOrder
            from ProductSort ps
            where ps.environmentType = :environmentType
              and ps.sortOrderType = :sortOrderType
        """, Object[].class)
                    .setParameter("environmentType", EnvironmentType.WEB)
                    .setParameter("sortOrderType", SortOrderType.LIST)
                    .getResultList()
                    .stream()
                    .collect(Collectors.toMap(
                            productId -> (Long) productId[0],
                            sortOrder -> (Integer) sortOrder[1],
                            (existingSortOrder, newSortOrder) -> existingSortOrder));
        } else {
            productSortMap = Collections.emptyMap();
        }

        if (enable && !productSortMap.isEmpty()) {
            productExpeditorReportDTO.getProductExpeditorReports()
                    .replaceAll((commonDTO, productListDTOQtyMap) ->
                            productListDTOQtyMap.entrySet()
                                    .stream()
                                    .sorted(Comparator.comparingInt(e -> productSortMap.getOrDefault(
                                                    e.getKey().getProduct().getId(),
                                                    Integer.MAX_VALUE)))
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            Map.Entry::getValue,
                                            (a, b) -> a,
                                            LinkedHashMap::new
                                    ))
                    );
        }

        productExpeditorReportDTO.getProductExpeditorReports()
                .forEach((commonDTO, productListDTOQtyMap) ->
                        productListDTOQtyMap.forEach((productListDTO, qty) -> {
                            ProductExpeditorListDTO productExpeditorListDTO = new ProductExpeditorListDTO();
                            productExpeditorListDTO.setProduct(productListDTO.getProduct());
                            productExpeditorListDTO.setDiscount(productListDTO.getDiscount());
                            productExpeditorListDTO.setQty(qty);
                            productExpeditorListDTO.setTotalPrice(
                                    Optional.ofNullable(productListDTO.getProduct().getPrice())
                                            .orElse(BigDecimal.ZERO)
                                            .multiply(qty)
                            );

                            if (aggregationTableCreateDTO.getProductListType() != null && ProductListType.BY_CUSTOMER_BRANCH == aggregationTableCreateDTO.getProductListType()) {
                                productExpeditorListDTO.setCustomerBranch(commonDTO);
                            } else {
                                productExpeditorListDTO.setExpeditor(commonDTO);
                            }

                            productExpeditorListDTO.setPaymentType(productListDTO.getPaymentType());
                            responseDTO.getProducts().add(productExpeditorListDTO);
                        }));
        if (!responseDTO.getProducts().isEmpty()) {
            ProductExpeditorListDTO first = responseDTO.getProducts().getFirst();

            if (first.getExpeditor() != null) {
                responseDTO.setExpeditorName(first.getExpeditor().getName());
            }

            if (first.getCustomerBranch() != null) {
                responseDTO.setAgentName(first.getCustomerBranch().getName());
            }
        }

        if (StringUtils.isEmpty(responseDTO.getAgentName())) {
            responseDTO.setAgentName(aggregationTableCreateDTO.getAgentName());
        }
        if (StringUtils.isEmpty(responseDTO.getExpeditorName())) {
            responseDTO.setExpeditorName(aggregationTableCreateDTO.getExpeditorName());
        }

        return responseDTO;
    }


    public BigDecimal checkQtyInOrders(Long productId, Long orderId, Long warehouseId) {
        if (orderId == null) {
            return Optional.ofNullable(orderProductRepository.getQty(SecurityUtils.getCurrentUserId(), productId, List.of(OrderStatus.IN_DELIVERY, OrderStatus.UN_LOADING), warehouseId)).orElse(BigDecimal.ZERO);
        }
        return Optional.ofNullable(orderProductRepository.getQty(SecurityUtils.getCurrentUserId(), productId, List.of(OrderStatus.IN_DELIVERY, OrderStatus.UN_LOADING), orderId, warehouseId)).orElse(BigDecimal.ZERO);
    }

    @Transactional
    public void syncPlanRoutes() {
        List<Order> orders = new ArrayList<>();
        for (Order order : orderRepository.getOrdersToSync()) {
            Long planRouteId = order.getPlanRouteCustomerBranch().getPlanRouteItem().getPlanRouteId();
            order.setPlanRouteId(planRouteId);
            orders.add(order);
        }
        orderRepository.saveAll(orders);
    }

    public Page<MobileOrderListForExpeditorDTO> getListForExpeditor(OrderFilter filter) {
        filter.setExpeditorId(SecurityUtils.getCurrentUserId());
        filter.setPlanRouteIds(planRouteExpeditorRepository.findAllByExpeditorIdAndStatusAndDeletedIsFalse(filter.getExpeditorId(), PlanRouteStatus.OPEN));
        ResultList<Order> resultList = orderRepository.getResultListForExpeditor(filter);
        List<MobileOrderListForExpeditorDTO> result = resultList.getList().stream().map(order -> {
            MobileOrderListForExpeditorDTO orderList = new MobileOrderListForExpeditorDTO();
            orderList.setId(order.getId());
            orderList.setOrderNumber(order.getOrderNumber());
            orderList.setTotalQty(order.getTotalQty());
            if (order.getPlanRouteId() != null) {
                orderList.setPlanRoute(order.getPlanRoute().toCommonDTO());
            }
            orderList.setStatus(order.getStatus());
            orderList.setTotalPrice(order.getTotalPrice());
            orderList.setCustomer(order.getCustomer().toCommonDTO());
            if (order.getDeliveryBranchId() != null) {
                orderList.setCustomerBranch(new CustomerBranchAddressDTO(order.getDeliveryBranch().getId(), order.getDeliveryBranch().getName(),
                    addressMapper.toDetailDTO(order.getDeliveryBranch().getAddress()), order.getDeliveryBranch().getPhone()));
            }
            orderList.setOrderDate(order.getOrderDate());
            orderList.setTotalDiscountPrice(order.getTotalDiscountPrice());
            return orderList;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }


    private OrderDTO mapOfflineToOrderDTO(OfflineOrderDTO offlineOrderDTO) {

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOfflineOrderId(offlineOrderDTO.getId());
        orderDTO.setCustomerId(offlineOrderDTO.getCustomerId());
        orderDTO.setSupplierId(offlineOrderDTO.getSupplierId());
        orderDTO.setSupplierTin(offlineOrderDTO.getSupplierTin());
        orderDTO.setOrderDate(offlineOrderDTO.getOrderDate());
        orderDTO.setOrderNumber(offlineOrderDTO.getOrderNumber());
        orderDTO.setMarketCode(offlineOrderDTO.getMarketCode());
        orderDTO.setContractId(offlineOrderDTO.getContractId());
        orderDTO.setBranchId(offlineOrderDTO.getBranchId());
        orderDTO.setWarehouseId(offlineOrderDTO.getWarehouseId());
        orderDTO.setVehicleId(offlineOrderDTO.getVehicleId());
        orderDTO.setDescription(offlineOrderDTO.getDescription());
        orderDTO.setStatus(offlineOrderDTO.getStatus());
        orderDTO.setDeliveryBranchId(offlineOrderDTO.getDeliveryBranchId());
        orderDTO.setDeliveryAddress(offlineOrderDTO.getDeliveryAddress());
        orderDTO.setSaleType(offlineOrderDTO.getSaleType());
        orderDTO.setPaymentType(offlineOrderDTO.getPaymentType());
        orderDTO.setProducts(offlineOrderDTO.getProducts());
        orderDTO.setAdvancePaymentAmount(offlineOrderDTO.getAdvancePaymentAmount());
        orderDTO.setAdvancePaymentPercent(offlineOrderDTO.getAdvancePaymentPercent());
        orderDTO.setSpecificationNumber(offlineOrderDTO.getSpecificationNumber());
        orderDTO.setSpecificationDate(offlineOrderDTO.getSpecificationDate());
        orderDTO.setPriceListId(offlineOrderDTO.getPriceListId());
        orderDTO.setFrom1C(offlineOrderDTO.isFrom1C());
        orderDTO.setFromHippo(offlineOrderDTO.isFromHippo());
        orderDTO.setOrderType(offlineOrderDTO.getOrderType());
        orderDTO.setAppType(offlineOrderDTO.getAppType());
        orderDTO.setDeliveryType(offlineOrderDTO.getDeliveryType());
        orderDTO.setDeliveryDate(offlineOrderDTO.getDeliveryDate());
        orderDTO.setCreateStockIn(offlineOrderDTO.isCreateStockIn());
        orderDTO.setSource(offlineOrderDTO.getSource());
        orderDTO.setExpeditorId(offlineOrderDTO.getExpeditorId());
        orderDTO.setAgentId(offlineOrderDTO.getAgentId());
        orderDTO.setPlanRouteCustomerBranchId(offlineOrderDTO.getPlanRouteCustomerBranchId());
        orderDTO.setPlanRouteId(offlineOrderDTO.getPlanRouteId());
        orderDTO.setTotalBrutto(offlineOrderDTO.getTotalBrutto());
        orderDTO.setTotalNetto(offlineOrderDTO.getTotalNetto());
        orderDTO.setTotalQty(offlineOrderDTO.getTotalQty());
        orderDTO.setTotalPrice(offlineOrderDTO.getTotalPrice());
        orderDTO.setTotalPriceWithoutVat(offlineOrderDTO.getTotalPriceWithoutVat());
        orderDTO.setTotalVatPrice(offlineOrderDTO.getTotalVatPrice());
        orderDTO.setTotalDiscountPrice(offlineOrderDTO.getTotalDiscountPrice());
        orderDTO.setExchangeRate(offlineOrderDTO.getExchangeRate());
        orderDTO.setCurrencyType(offlineOrderDTO.getCurrencyType());
        orderDTO.setTotalForeignPrice(offlineOrderDTO.getTotalForeignPrice());

        return orderDTO;
    }


    private OrderDTO mapOfflineUpdateToOrderDTO(OrderUpdateDTO orderUpdateDTO) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderUpdateDTO.getId());
        orderDTO.setCustomerId(orderUpdateDTO.getCustomerId());
        orderDTO.setSupplierId(orderUpdateDTO.getSupplierId());
        orderDTO.setDeliveryBranchId(orderUpdateDTO.getDeliveryBranchId());
        orderDTO.setBranchId(orderUpdateDTO.getBranchId());
        orderDTO.setWarehouseId(orderUpdateDTO.getWarehouseId());
        orderDTO.setVehicleId(orderUpdateDTO.getVehicleId());
        orderDTO.setContractId(orderUpdateDTO.getContractId());
        orderDTO.setPriceListId(orderUpdateDTO.getPriceListId());
        orderDTO.setExpeditorId(orderUpdateDTO.getExpeditorId());
        orderDTO.setAgentId(orderUpdateDTO.getAgentId());
        orderDTO.setPlanRouteCustomerBranchId(orderUpdateDTO.getPlanRouteCustomerBranchId());
        orderDTO.setPlanRouteId(orderUpdateDTO.getPlanRouteId());
        orderDTO.setOrderDate(orderUpdateDTO.getOrderDate());
        orderDTO.setOrderNumber(orderUpdateDTO.getOrderNumber());
        orderDTO.setMarketCode(orderUpdateDTO.getMarketCode());
        orderDTO.setDescription(orderUpdateDTO.getDescription());
        orderDTO.setStatus(orderUpdateDTO.getStatus());
        orderDTO.setSaleType(orderUpdateDTO.getSaleType());
        orderDTO.setPaymentType(orderUpdateDTO.getPaymentType());
        orderDTO.setOrderType(orderUpdateDTO.getOrderType());
        orderDTO.setAppType(orderUpdateDTO.getAppType());
        orderDTO.setDeliveryType(orderUpdateDTO.getDeliveryType());
        orderDTO.setDeliveryDate(orderUpdateDTO.getDeliveryDate());
        orderDTO.setCreateStockIn(orderUpdateDTO.isCreateStockIn());
        orderDTO.setSource(orderUpdateDTO.getSource());
        orderDTO.setFrom1C(orderUpdateDTO.isFrom1C());
        orderDTO.setFromHippo(orderUpdateDTO.isFromHippo());
        orderDTO.setTotalQty(orderUpdateDTO.getTotalQty());
        orderDTO.setTotalPrice(orderUpdateDTO.getTotalPrice());
        orderDTO.setTotalForeignPrice(orderUpdateDTO.getTotalForeignPrice());
        orderDTO.setExchangeRate(orderUpdateDTO.getExchangeRate());
        orderDTO.setCurrencyType(orderUpdateDTO.getCurrencyType());
        orderDTO.setTotalPriceWithoutVat(orderUpdateDTO.getTotalPriceWithoutVat());
        orderDTO.setTotalVatPrice(orderUpdateDTO.getTotalVatPrice());
        orderDTO.setTotalDiscountPrice(orderUpdateDTO.getTotalDiscountPrice());
        orderDTO.setTotalBrutto(orderUpdateDTO.getTotalBrutto());
        orderDTO.setTotalNetto(orderUpdateDTO.getTotalNetto());
        orderDTO.setAdvancePaymentAmount(orderUpdateDTO.getAdvancePaymentAmount());
        orderDTO.setAdvancePaymentPercent(orderUpdateDTO.getAdvancePaymentPercent());
        orderDTO.setSpecificationNumber(orderUpdateDTO.getSpecificationNumber());
        orderDTO.setSpecificationDate(orderUpdateDTO.getSpecificationDate());
        orderDTO.setDeliveryAddress(orderUpdateDTO.getDeliveryAddress());
        orderDTO.setProducts(orderUpdateDTO.getProducts());
        return orderDTO;
    }

    public Page<OrderExpeditorReportDTO> getOrderExpeditorReport(OrderFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        ResultList<OrderExpeditorReportDTO> resultList = orderRepository.getOrderExpeditorReport(filter);
        return new PageImpl<>(resultList.getList(),filter.getSortedPageable(),resultList.getCount());
    }

    public OrderExpeditorTotalQtyReportDTO getOrderExpeditorTotalQtyReport(OrderFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        return orderRepository.getOrderExpeditorTotalQtyReport(filter);
    }

    public TerminalOrderStatisticsDTO getOrderStatisticsForTerminal(OrderFilter filter) {
        filter.setCompanyId(SecurityUtils.getCompanyId());
        if(filter.getExpeditorId() == null) {
            filter.setExpeditorId(SecurityUtils.getCurrentUserId());
        }
        return orderRepository.getOrderStatisticsForTerminal(filter);
    }
}
