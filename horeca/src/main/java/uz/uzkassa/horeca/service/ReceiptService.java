package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.Terminal;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.hall.HallItem;
import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.domain.order.OrderItem;
import uz.uzkassa.horeca.domain.order.OrderPayment;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.domain.receipt.Receipt;
import uz.uzkassa.horeca.domain.receipt.ReceiptDetail;
import uz.uzkassa.horeca.domain.receipt.ReceiptItem;
import uz.uzkassa.horeca.domain.receipt.ReceiptPayment;
import uz.uzkassa.horeca.dto.receipt.ReceiptStatisticDTO;
import uz.uzkassa.horeca.dto.receipt.admin.ReceiptListAdminDTO;
import uz.uzkassa.horeca.dto.receipt.cabinet.ReceiptDetailCabinetDTO;
import uz.uzkassa.horeca.dto.receipt.cabinet.ReceiptItemCabinetDTO;
import uz.uzkassa.horeca.dto.receipt.cabinet.ReceiptListCabinetDTO;
import uz.uzkassa.horeca.dto.reservation.CustomerDetailDTO;
import uz.uzkassa.horeca.dto.sms.SmsRequestDTO;
import uz.uzkassa.horeca.dto.terminal.*;
import uz.uzkassa.horeca.enums.*;
import uz.uzkassa.horeca.filters.ReceiptFilter;
import uz.uzkassa.horeca.integration.ApayClient;
import uz.uzkassa.horeca.repository.*;
import uz.uzkassa.horeca.utils.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Primary
@Slf4j
public class ReceiptService extends BaseService {

    ReceiptRepository receiptRepository;
    ReceiptDetailRepository receiptDetailRepository;
    ReceiptItemRepository receiptItemRepository;
    OrderRepository orderRepository;
    PushkassaNotificationService notificationService;
    TerminalRepository terminalRepository;
    ReceiptPaymentRepository receiptPaymentRepository;
    TelegramService telegramService;
    SmsService smsService;
    ApayClient apayClient;
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ShiftRepository shiftRepository;
    HallItemRepository hallItemRepository;

    public ReceiptDetailCabinetDTO getForCabinet(Long id) {
        return receiptRepository.findById(id).map(receipt -> {
            ReceiptDetailCabinetDTO receiptDetailCabinetDTO = new ReceiptDetailCabinetDTO();
            receiptDetailCabinetDTO.setId(receipt.getId());
            receiptDetailCabinetDTO.setFiscalSign(receipt.getFiscalSign());
            receiptDetailCabinetDTO.setFiscalUrl(receipt.getFiscalUrl());
            Map<ReceiptPaymentType, BigDecimal> payments = new LinkedHashMap<>();
            receipt.getPayments().forEach(receiptPayment -> payments.put(receiptPayment.getPaymentType(), receiptPayment.getPrice()));
            receiptDetailCabinetDTO.setTotalCash(receipt.getTotalCash());
            receiptDetailCabinetDTO.setTotalCard(receipt.getTotalCard());
            receiptDetailCabinetDTO.setTotalCost(receipt.getTotalCost());
            receiptDetailCabinetDTO.setTotalDiscount(receipt.getTotalDiscount());
            receiptDetailCabinetDTO.setDiscountPercent(receipt.getDiscountPercent());
            receiptDetailCabinetDTO.setTotalPaid(receipt.getTotalPaid());
            receiptDetailCabinetDTO.setTotalVat(receipt.getTotalVat());
            receiptDetailCabinetDTO.setBranch(receipt.getBranch().toCommonDTO());
            receiptDetailCabinetDTO.setTerminal(receipt.getTerminal().toTerminalDTO());
            receiptDetailCabinetDTO.setCompany(toCompanyBaseDTO(receipt.getCompany()));
            receiptDetailCabinetDTO.setShiftNo(receipt.getShiftNumber());
            receiptDetailCabinetDTO.setNumberInShift(receipt.getNumberInShift());
            receiptDetailCabinetDTO.setReceiptSeq(receipt.getReceiptSeq());
            receiptDetailCabinetDTO.setItemsCount(receipt.getItemsCount());
            if (receipt.getCreatorId() != null) {
                receiptDetailCabinetDTO.setUser(receipt.getCreator().toCommonDTO());
            }
            receiptDetailCabinetDTO.setStatus(receipt.getStatus());

            receiptDetailRepository.findFirstByReceiptId(receipt.getId()).ifPresent(receiptDetail -> {
                receiptDetailCabinetDTO.setCustomer(new CustomerDetailDTO(receiptDetail.getCustomerName(), receiptDetail.getCustomerPhone()));
                receiptDetailCabinetDTO.setSource(receiptDetail.getSource());
            });
            receiptDetailCabinetDTO.setReceiptDateTime(receipt.getReceiptDate());
            receiptDetailCabinetDTO.setUid(receipt.getUid());
            if (receipt.getOrderId() != null) {
                receiptDetailCabinetDTO.setTable(receipt.getOrder().toCommonDTO());
                receiptDetailCabinetDTO.setTable(receipt.getOrder().getTable().toCommonDTO());
            }
            receiptDetailCabinetDTO.setPayments(payments);
            receipt.getItems().forEach(receiptItem -> {
                ReceiptItemCabinetDTO receiptItemCabinetDTO = new ReceiptItemCabinetDTO();
                receiptItemCabinetDTO.setId(receiptItem.getId());
                receiptItemCabinetDTO.setProduct(toProductCommonDTO(receiptItem.getProduct()));
                if (receiptItem.getUnitId() != null) {
                    receiptItemCabinetDTO.setUnit(receiptItem.getUnit().toCommonDTO());
                }
                if (receiptItem.getCategoryId() != null) {
                    receiptItemCabinetDTO.setCategory(receiptItem.getCategory().toCommonDTO());
                }
                receiptItemCabinetDTO.setPrice(receiptItem.getPrice());
                receiptItemCabinetDTO.setSalesPrice(receiptItem.getTotalPrice());
                receiptItemCabinetDTO.setQty(receiptItem.getQty());
                receiptItemCabinetDTO.setDiscountPercent(receiptItem.getDiscountPercent());
                receiptItemCabinetDTO.setDiscount(receiptItem.getDiscount());
                receiptItemCabinetDTO.setVatPrice(receiptItem.getVatPrice());
                receiptItemCabinetDTO.setVatRate(receiptItem.getVatRate());
                receiptItemCabinetDTO.setMarks(receiptItem.getMarks());
                receiptItemCabinetDTO.setOrigin(receiptItem.getOrigin());
                receiptDetailCabinetDTO.addItem(receiptItemCabinetDTO);
            });
            return receiptDetailCabinetDTO;
        }).orElseThrow(notFoundExceptionThrow(Receipt.class.getSimpleName(), "id", id));
    }

    @Transactional
    public TerminalReceiptDTO create(TerminalReceiptDTO receiptDTO) {
        this.validate(receiptDTO);
        Company company = companyRepository.getReferenceById(Optional.ofNullable(receiptDTO.getCompanyId()).orElseGet(BaseService::getCurrentCompanyId));
        Branch branch = branchRepository.getReferenceById(Optional.ofNullable(receiptDTO.getBranchId()).orElseGet(BaseService::getCurrentBranchId));
        User user = userRepository.getReferenceById(Optional.ofNullable(receiptDTO.getUserId()).orElseGet(BaseService::getCurrentUserId));
        Receipt receipt = Optional.ofNullable(receiptDTO.getId()).flatMap(receiptRepository::findById).orElseGet(Receipt::new);
        if (receipt.getId() != null && receipt.getStatus().isFiscalized()) {
            throw badRequestExceptionThrow("Чек уже фискализирован").get();
        }

        if (receiptDTO.getStatus() == null) {
            receiptDTO.setStatus(ReceiptStatus.DRAFT);
        }

        receiptDTO.setId(receipt.getId());
        receiptDTO.setCompanyId(company.getId());
        receiptDTO.setBranchId(branch.getId());
        receiptDTO.setUserId(user.getId());

        TerminalUserDTO userDTO = new TerminalUserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(new TerminalFullNameDTO(user.getFirstName(), user.getLastName(), user.getPatronymic()));
        receiptDTO.setUser(userDTO);

        if (StringUtils.isNotEmpty(receipt.getUid())) {
            receiptDTO.setUid(receipt.getUid());
        } else {
            if (StringUtils.isNotBlank(receiptDTO.getOriginUid()) && ReceiptStatus.PAID.equals(receiptDTO.getStatus())) {
                receiptDTO.setUid(receiptDTO.getOriginUid());
            }
        }
        receipt.setCompanyId(company.getId());
        receipt.setCompany(company);
        receipt.setBranchId(branch.getId());
        receipt.setBranch(branch);
        receipt.setCreatorId(user.getId());
        receipt.setCreator(user);
        if (receiptDTO.getOrderId() != null) {
            receipt.setOrderId(receiptDTO.getOrderId());
        }

        receiptDTO.getReceiptDetails().forEach(detail -> {
            if (!CollectionUtils.isEmpty(detail.getMarks())) {
                detail.setMarks(detail.getMarks().stream().map(MarkUtils::clearCode).collect(Collectors.toSet()));
            }
        });
        final String terminalSn = Optional.ofNullable(receiptDTO.getTerminalSN()).orElse(HeaderUtils.getCurrentTerminalSnByHeader());
        if (StringUtils.isNotEmpty(terminalSn)) {
            Terminal terminal = terminalRepository.findFirstBySerialNumber(terminalSn).orElseThrow(badRequestExceptionThrow("Кассовый аппарат отсутствует в этом филиале"));
            receiptDTO.setTerminalSN(terminalSn);
            receipt.setTerminal(terminal);
            receipt.setTerminalId(terminal.getId());
        }
        return saveAndReturn(receiptDTO, receipt);
    }

    @Transactional
    public TerminalReceiptDTO createFromMobile(TerminalReceiptDTO receiptDTO) {
        this.validate(receiptDTO);
        final Long orderId = Optional.ofNullable(receiptDTO.getOrderId()).orElseThrow(badRequestExceptionThrow("Заказ не выбран"));
        Order order = orderRepository.getReferenceById(orderId);
        if (order.getReceipt() != null && order.getReceipt().getStatus().isFiscalized()) {
            throw badRequestExceptionThrow("Чек уже фискализирован").get();
        }
        Company company = companyRepository.getReferenceById(Optional.ofNullable(receiptDTO.getCompanyId()).orElseGet(BaseService::getCurrentCompanyId));
        Branch branch = branchRepository.getReferenceById(Optional.ofNullable(receiptDTO.getBranchId()).orElseGet(BaseService::getCurrentBranchId));
        User user = userRepository.getReferenceById(Optional.ofNullable(receiptDTO.getUserId()).orElseGet(BaseService::getCurrentUserId));

        Receipt receipt = Optional.ofNullable(order.getReceipt()).orElseGet(Receipt::new);
        if (receipt.getId() != null && receipt.getStatus().isFiscalized()) {
            throw badRequestExceptionThrow("Чек уже фискализирован").get();
        }
        receipt.setCreatorId(user.getId());
        receipt.setCreator(user);
        receipt.setBranch(branch);
        receipt.setBranchId(branch.getId());
        receipt.setCompany(company);
        receipt.setCompanyId(company.getId());

        if (branch.getId() != null) {
            shiftRepository.findFirstByUserIdAndStatusOrderByCreatedDateDesc(user.getId(), ShiftStatus.OPEN).ifPresent(shift -> {
                receiptDTO.setShiftNo(shift.getIncrementNumber());
                receipt.setShiftNumber(shift.getIncrementNumber());
            });
        }
//        receiptDTO.setStatus(ReceiptStatus.PAID);
        receiptDTO.setSource(TerminalSource.HORECA);
        receiptDTO.setCompanyId(company.getId());
        receiptDTO.setBranchId(branch.getId());
        receiptDTO.setReceiptDateTime(Optional.ofNullable(receiptDTO.getReceiptDateTime()).orElse(LocalDateTime.now()));
        receiptDTO.setUid(ReceiptUtils.generateReceiptUid(user.getId()));
        receiptDTO.setPositionCount(order.getItemsCount());
        receiptDTO.setForceToPrint(false);// by default false if in branch terminal found then will true

        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalNds = BigDecimal.ZERO;
        final List<OrderItem> orderItems = order.getItems();
        for (OrderItem orderItem : orderItems) {
            final MenuItem menuItem = orderItem.getMenuItem();
            final Product product = menuItem.getProduct();
            TerminalReceiptDetailDTO terminalReceiptDetailDTO = new TerminalReceiptDetailDTO();
            terminalReceiptDetailDTO.setProductId(product.getId());
            terminalReceiptDetailDTO.setProductName(product.getName());
            terminalReceiptDetailDTO.setVatBarcode(product.getCatalogCode());
            terminalReceiptDetailDTO.setCategoryId(product.getCategoryId());
            terminalReceiptDetailDTO.setPrice(orderItem.getPrice());
            terminalReceiptDetailDTO.setQty(orderItem.getQty());
            terminalReceiptDetailDTO.setAmount(orderItem.getTotalPrice());
            terminalReceiptDetailDTO.setMenuItemId(menuItem.getId());
            terminalReceiptDetailDTO.setMultiUnitId(orderItem.getMultiUnitId());
            terminalReceiptDetailDTO.setNds(orderItem.getVatPrice());
            terminalReceiptDetailDTO.setNdsPercent(orderItem.getVatRate());
            terminalReceiptDetailDTO.setProductBarcode(product.getBarcode());
            terminalReceiptDetailDTO.setUnitId(product.getUnitId());
            if (product.getUnit() != null) {
                terminalReceiptDetailDTO.setUnitName(product.getUnit().getName());
            }
            terminalReceiptDetailDTO.setOrigin(1);
            terminalReceiptDetailDTO.setDiscount(BigDecimal.ZERO);
            TerminalReceiptExtraDetailDTO extraDetailDTO = new TerminalReceiptExtraDetailDTO();
            extraDetailDTO.setPackageCode(product.getPackageCode());
            extraDetailDTO.setPackageName(product.getPackageName());
            terminalReceiptDetailDTO.setExtraDetail(extraDetailDTO);
            terminalReceiptDetailDTO.setMarks(orderItem.getMarks().stream().map(MarkUtils::clearCode).collect(Collectors.toSet()));
            receiptDTO.addReceiptDetail(terminalReceiptDetailDTO);

            totalPrice = totalPrice.add(orderItem.getTotalPrice());
            totalNds = totalNds.add(orderItem.getVatPrice());

        }
        // add catering service
        final TerminalReceiptDetailDTO cateringReceiptItem = createCateringReceiptItem(order.getTableId(), totalPrice);
        if (cateringReceiptItem != null) {
            receiptDTO.addReceiptDetail(cateringReceiptItem);
            totalNds = totalNds.add(cateringReceiptItem.getNds());
            totalPrice = totalPrice.add(cateringReceiptItem.getPrice());
        }
        receiptDTO.setTotalCost(totalPrice);
        receiptDTO.setTotalNds(totalNds);

        receiptDTO.getPayments().forEach((receiptPaymentType, bigDecimal) -> {
            if (ReceiptPaymentType.CARD == receiptPaymentType) {
                BigDecimal totalPaid = Optional.ofNullable(receiptDTO.getTotalPaid()).orElse(BigDecimal.ZERO);
                BigDecimal totalCard = Optional.ofNullable(receiptDTO.getTotalCard()).orElse(BigDecimal.ZERO);
                receiptDTO.setTotalCard(totalCard.add(bigDecimal));
                receiptDTO.setTotalPaid(totalPaid.add(bigDecimal));
            }
            if (ReceiptPaymentType.CASH == receiptPaymentType) {
                BigDecimal totalCash = Optional.ofNullable(receiptDTO.getTotalCash()).orElse(BigDecimal.ZERO);
                BigDecimal totalPaid = Optional.ofNullable(receiptDTO.getTotalPaid()).orElse(BigDecimal.ZERO);
                receiptDTO.setTotalCash(totalCash.add(bigDecimal));
                receiptDTO.setTotalPaid(totalPaid.add(bigDecimal));
            }
        });

        final String terminalSn = Optional.ofNullable(receiptDTO.getTerminalSN()).orElse(HeaderUtils.getCurrentTerminalSnByHeader());
        receiptDTO.setTerminalSN(terminalSn);
        if (StringUtils.isNotEmpty(terminalSn)) {
            Terminal terminal = terminalRepository.findFirstBySerialNumber(terminalSn).orElseThrow(badRequestExceptionThrow("Кассовый аппарат отсутствует в этом филиале"));
            receipt.setTerminalId(terminal.getId());
            receipt.setTerminal(terminal);
            receiptDTO.setForceToPrint(true);
        } else {
            terminalRepository.findFirstByBranchIdAndDeletedIsFalse(branch.getId()).ifPresent(terminal -> {
                receipt.setTerminal(terminal);
                receipt.setTerminalId(terminal.getId());
                receiptDTO.setForceToPrint(true);
            });
        }
        if (order.getClient() != null) {
            receiptDTO.setCustomerName(order.getClient().getGuestName());
            receiptDTO.setCustomerPhone(order.getClient().getPhone());
            receiptDTO.setCustomerId(order.getClient().getId());
            receiptDTO.setSendSms(true);
        }
        return saveAndReturn(receiptDTO, receipt);
    }

    public TerminalReceiptDetailDTO createCateringReceiptItem(Long tableId, BigDecimal totalCost) {
        HallItem hallItem = hallItemRepository.findById(tableId).orElse(null);
        if (hallItem == null) return null;

        final BigDecimal feePercent = hallItem.getFeePercent();
        if (feePercent.compareTo(BigDecimal.ZERO) == 0) return null;
        final BigDecimal receiptItemPrice = CalcUtils.calcCateringServiceAmount(feePercent, totalCost);
        final Product product = categoryRepository.findFirstByCategoryTypeAndDeletedIsFalse(CategoryType.CATERING_SERVICE)
                .flatMap(category -> productRepository.findFirstByCategoryIdAndDeletedIsFalse(category.getId())).orElse(null);
        if (product == null) return null;

        TerminalReceiptDetailDTO terminalReceiptDetailDTO = new TerminalReceiptDetailDTO();
        terminalReceiptDetailDTO.setProductId(product.getId());
        terminalReceiptDetailDTO.setProductName(product.getName());
        terminalReceiptDetailDTO.setProductBarcode(product.getBarcode());
        terminalReceiptDetailDTO.setCategoryId(product.getCategoryId());
        terminalReceiptDetailDTO.setVatBarcode(product.getCatalogCode());
        terminalReceiptDetailDTO.setPrice(receiptItemPrice);
        terminalReceiptDetailDTO.setAmount(receiptItemPrice);
        terminalReceiptDetailDTO.setQty(BigDecimal.ONE);
        terminalReceiptDetailDTO.setOrigin(ProductOrigin.SERVICE.getId());
        terminalReceiptDetailDTO.setUnitId(product.getUnitId());
        terminalReceiptDetailDTO.setDiscount(BigDecimal.ZERO);
        terminalReceiptDetailDTO.setUnitName(product.getUnit().getName());
        final Vat productVat = product.getVat();
        if (Vat.VAT_12 == productVat) {
            terminalReceiptDetailDTO.setNdsPercent(productVat.getAmount());
            terminalReceiptDetailDTO.setNds(CalcUtils.calcVat(receiptItemPrice, productVat.getAmount()));
        } else {
            terminalReceiptDetailDTO.setNds(BigDecimal.ZERO);
            terminalReceiptDetailDTO.setNdsPercent(BigDecimal.ZERO);
        }
        return terminalReceiptDetailDTO;
    }

    public void validate(TerminalReceiptDTO receiptDTO) {
        if (receiptDTO.getOrderId() != null) {
            if (receiptDTO.getId() == null) {
                if (receiptRepository.findFirstByOrderIdAndDeletedIsFalse(receiptDTO.getOrderId()).isPresent()) {
                    if (!receiptDTO.isFromBatch()) {
                        throw badRequestExceptionThrow("Receipt already created for this order").get();
                    } else {
                        receiptRepository.findFirstByOrderIdAndDeletedIsFalse(receiptDTO.getOrderId()).ifPresent(receipt -> {
                            receiptDTO.setOldReceiptId(receipt.getId());
                        });
                    }
                }
            } else {
                if (receiptRepository.findFirstByOrderIdAndDeletedIsFalseAndIdNot(receiptDTO.getOrderId(), receiptDTO.getId()).isPresent()) {
                    if (!receiptDTO.isFromBatch()) {
                        throw badRequestExceptionThrow("Receipt already created for this order").get();
                    } else {
                        receiptRepository.findFirstByOrderIdAndDeletedIsFalseAndIdNot(receiptDTO.getOrderId(), receiptDTO.getId()).ifPresent(receipt -> {
                            receiptDTO.setOldReceiptId(receipt.getId());
                        });
                    }
                }
            }
        }
    }

    @Transactional
    public TerminalReceiptDTO saveAndReturn(TerminalReceiptDTO receiptDTO, Receipt receipt) {
        boolean isDraft = receiptDTO.getStatus() == null || receiptDTO.getStatus() == ReceiptStatus.DRAFT;
        boolean isNew;
        ReceiptDetail receiptDetail;
        if (receipt == null) {
            receipt = receiptRepository.findFirstByUidAndDeletedIsFalse(receiptDTO.getUid()).orElseGet(Receipt::new);
        }
        isNew = receipt.getId() == null;
        if (receipt.getId() == null) {
            receiptDetail = new ReceiptDetail();
        } else {
            receiptDetail = receiptDetailRepository.findFirstByReceiptId(receipt.getId()).orElseGet(ReceiptDetail::new);
        }

        receipt.setBranchId(receiptDTO.getBranchId());
        receipt.setCompanyId(receiptDTO.getCompanyId());
        if (receipt.getCreatorId() == null) {
            receipt.setCreatorId(receiptDTO.getUserId());
        }
        if (receipt.getCreatedDate() == null) {
            receipt.setCreatedDate(LocalDateTime.now());
        }
        Order order = null;
        if (receiptDTO.getOrderId() != null) {
            order = orderRepository.findById(receiptDTO.getOrderId()).orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", receiptDTO.getOrderId()));
            receipt.setOrder(order);
            receipt.setOrderId(order.getId());
        }
        if (StringUtils.isEmpty(receipt.getUid())) {
            receipt.setUid(Optional.ofNullable(order != null ? order.getUid() : null).orElse(receiptDTO.getUid()));
        }

        receipt.setFiscalSign(receiptDTO.getFiscalSign());
        receipt.setFiscalUrl(receiptDTO.getFiscalUrl());
        receipt.setShiftNumber(receiptDTO.getShiftNo());
        if (receiptDTO.getAdditional() != null) {
            receipt.setNumberInShift(receiptDTO.getAdditional().getNumberInShift());
            receipt.setReceiptSeq(receiptDTO.getAdditional().getReceiptSeq());
        }
        if (StringUtils.isNotEmpty(receiptDTO.getTerminalSN())) {
            Optional<Terminal> terminalOptional = terminalRepository.findFirstBySerialNumberAndBranchIdAndDeletedIsFalse(receiptDTO.getTerminalSN(), receiptDTO.getBranchId());
            if (terminalOptional.isPresent()) {
                receipt.setTerminalId(terminalOptional.get().getId());
                receipt.setTerminal(terminalOptional.get());
            }
        }
        receipt.setReceiptDate(receiptDTO.getReceiptDateTime() != null ? receiptDTO.getReceiptDateTime() : LocalDateTime.now());
        receipt.setTotalCash(receiptDTO.getTotalCash());
        receipt.setTotalCard(receiptDTO.getTotalCard());
        receipt.setTotalCost(receiptDTO.getTotalCost());
        receipt.setTotalPaid(receiptDTO.getTotalPaid());
        receipt.setTotalDiscount(receiptDTO.getTotalDiscount());
        receipt.setDiscountPercent(receiptDTO.getDiscountPercent());
        receipt.setTotalVat(receiptDTO.getTotalNds());
        receipt.setReadOnly(receiptDTO.isReadOnly());
        receipt.setForceToPrint(receiptDTO.isForceToPrint());
        receipt.setStatus(receiptDTO.getStatus());
        receipt.setBillId(receiptDTO.getBillId());
        receipt.setFacturaId(receiptDTO.getFacturaId());

        receiptRepository.save(receipt);
        receiptDTO.setId(receipt.getId());

        //Receipt details
        receiptDetail.setReceiptId(receipt.getId());
        receiptDetail.setReceipt(receipt);
        receiptDetail.setLongitude(receiptDTO.getLat());
        receiptDetail.setLatitude(receiptDTO.getLon());
        receiptDetail.setCustomerName(receiptDTO.getCustomerName());
        receiptDetail.setCustomerPhone(receiptDTO.getCustomerPhone());
        if (receiptDTO.getSource() != null && receiptDetail.getSource() == null) {
            receiptDetail.setSource(receiptDTO.getSource());
        }
        receiptDetail.setAdditional(receiptDTO.getAdditional());
        receiptDetail.setSendSms(receiptDTO.isSendSms());


        //Receipt payments
        if (!CollectionUtils.isEmpty(receiptDTO.getPayments())) {
            if (!isNew) {
                receiptPaymentRepository.deleteAllByReceiptId(receipt.getId());
            }
            for (Map.Entry<ReceiptPaymentType, BigDecimal> entry : receiptDTO.getPayments().entrySet()) {
                ReceiptPayment receiptPayment = new ReceiptPayment();
                receiptPayment.setReceiptId(receipt.getId());
                receiptPayment.setReceipt(receipt);
                receiptPayment.setPaymentType(entry.getKey());
                receiptPayment.setPrice(entry.getValue());
                receipt.addPayment(receiptPayment);
            }
        }

        //Receipt items
        BigDecimal exciseTotal = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(receiptDTO.getReceiptDetails())) {
            if (!isNew) {
                receiptItemRepository.deleteAllByReceiptIdAndDeletedIsFalse(receipt.getId());
            }
            for (TerminalReceiptDetailDTO detailDTO : receiptDTO.getReceiptDetails()) {
                Product product = productRepository.findById(detailDTO.getProductId()).orElseThrow(notFoundExceptionThrow(Product.class.getSimpleName(), "id", detailDTO.getProductId()));
                ReceiptItem receiptItem = new ReceiptItem();
                receiptItem.setProductId(detailDTO.getProductId());
                receiptItem.setProduct(product);
                receiptItem.setProductName(detailDTO.getProductName());
                if (StringUtils.isNotEmpty(detailDTO.getProductBarcode()) && detailDTO.getProductBarcode().contains("$")) {
                    receiptItem.setBarcode(StringUtils.split(detailDTO.getProductBarcode(), "$")[0]);
                } else {
                    receiptItem.setBarcode(detailDTO.getProductBarcode());
                }
                if (StringUtils.isNotEmpty(detailDTO.getVatBarcode())) {
                    receiptItem.setCatalogCode(detailDTO.getVatBarcode());
                } else if (StringUtils.isNotEmpty(detailDTO.getProductBarcode()) && detailDTO.getProductBarcode().contains("$")) {
                    receiptItem.setCatalogName(StringUtils.split(detailDTO.getProductBarcode(), "$")[1]);
                }
                receiptItem.setUnitId(detailDTO.getUnitId());
                receiptItem.setUnitName(detailDTO.getUnitName());
                receiptItem.setCategoryId(detailDTO.getCategoryId());
                receiptItem.setCatalogName(detailDTO.getCategoryName());

                receiptItem.setExcise(detailDTO.getExcise());
                if (detailDTO.getExcise() != null) {
                    exciseTotal = exciseTotal.add(detailDTO.getExcise());
                }
                receiptItem.setExciseRate(detailDTO.getExciseRate());
                receiptItem.setOrigin(detailDTO.getOrigin());
                receiptItem.setPrice(detailDTO.getPrice());
                receiptItem.setQty(detailDTO.getQty());
                receiptItem.setTotalPrice(detailDTO.getAmount());
                receiptItem.setDiscount(detailDTO.getDiscount());
                receiptItem.setDiscountPercent(detailDTO.getDiscountPercent());
                receiptItem.setVatPrice(detailDTO.getNds());
                receiptItem.setVatRate(detailDTO.getNdsPercent());
                if (!CollectionUtils.isEmpty(detailDTO.getMarks())) {
                    receiptItem.setMarks(detailDTO.getMarks().stream().filter(StringUtils::isNotEmpty).collect(Collectors.toSet()));
                    if (ReceiptStatus.DRAFT.equals(receipt.getStatus()) && !CollectionUtils.isEmpty(receiptItem.getMarks())) {
                        receiptItem.setMarks(receiptItem.getMarks().stream().map(MarkUtils::clearCode).collect(Collectors.toSet()));
                    }
                }
                receiptItem.setExtraDetail(detailDTO.getExtraDetail());
                receipt.addItem(receiptItem);
            }
        }

        if (receiptDTO.getTotalExcise() != null && BigDecimal.ZERO.compareTo(receiptDTO.getTotalExcise()) < 0) {
            receipt.setTotalExcise(receiptDTO.getTotalExcise());
        } else if (BigDecimal.ZERO.compareTo(exciseTotal) < 0) {
            receipt.setTotalExcise(exciseTotal);
        }
        receipt.setItemsCount(BigDecimal.valueOf(receiptDTO.getPositionCount()));
        receiptRepository.save(receipt);
        receiptDTO.setId(receipt.getId());

        if (receiptDTO.isForceToPrint() && (isDraft || List.of(ReceiptStatus.ADVANCE, ReceiptStatus.CREDIT).contains(receiptDTO.getStatus()))) {
            if (order != null) {
                receiptDTO.setUid(order.getUid());
            }
            notificationService.sendNotification(receiptDTO, "smartpos", "receipt", receiptDTO.getTerminalSN());
        }

        if (EnumSet.of(ReceiptStatus.PAID, ReceiptStatus.RETURNED).contains(receipt.getStatus())) {
            if (receiptDetail.isSendSms() && SecurityUtils.checkLogin(receiptDetail.getCustomerPhone())) {
                this.sendSms(receipt, receiptDetail.getCustomerPhone(), receiptDTO.getLanguage());
            }
            if (!receipt.isFiscal()) {
                this.sendToTelegram(receipt);
            }
        }

        if (order != null) {
            order.setReceiptId(receipt.getId());
            if (!CollectionUtils.isEmpty(receipt.getPayments())) {
                order.getPayments().clear();
                for (ReceiptPayment payment : receipt.getPayments()) {
                    OrderPayment orderPayment = new OrderPayment();
                    orderPayment.setPaymentType(payment.getPaymentType());
                    orderPayment.setPrice(payment.getPrice());
                    order.addPayment(orderPayment);
                }
            }
//            if (ReceiptStatus.PAID.equals(receipt.getStatus()) && receipt.isFiscal()) {
//                order.setStatus(OrderStatus.CLOSED);
//                if (order.getReservationId() != null) {
//                    Reservation reservation = order.getReservation();
//                    reservation.setStatus(ReservationStatus.CLOSED);
//                    reservationRepository.save(reservation);
//                }
//            }
            orderRepository.save(order);
        }

        receiptDetailRepository.save(receiptDetail);
        if (receiptDTO.getOldReceiptId() != null) {
            receiptRepository.findById(receiptDTO.getOldReceiptId()).ifPresent(oldReceipt -> {
                oldReceipt.setDeleted(true);
                receiptRepository.save(oldReceipt);
            });
        }
        return getTerminalReceiptDTO(receipt, true, receiptDetail);
    }

    private void sendToTelegram(Receipt receipt) {
        StringBuilder message = new StringBuilder();
        message.append("<b>!!!!! Не фискальный чек !!!!!</b>\n");
        message.append("------------------------------------\n\n");
        message.append("<b>ИНН:</b> ").append(receipt.getCompany().getTin()).append("\n");
        message.append("<b>КОМПАНИЯ:</b> ").append(receipt.getCompany().getName()).append("\n");
        message.append("<b>ФИЛИАЛ:</b> ").append(receipt.getBranch().getName()).append("\n");
        message.append("<b>КАССИР:</b> ").append(receipt.getCreator().getName()).append("\n");
        message.append("<b>ККМ:</b> ").append(receipt.getTerminal().getName()).append("\n");
        message.append("<b>ДАТА:</b> ").append(DateUtils.dateTimeFormatter.format(receipt.getReceiptDate())).append("\n");
        message.append("<b>СУММА:</b> ").append(CalcUtils.decimalFormat.format(receipt.getTotalPaid())).append("\n");
        message.append("<b>СТАТУС:</b> ").append(receipt.getStatus().getName()).append("\n");
        message.append("<b>UID:</b> ").append(receipt.getUid()).append("\n");

        telegramService.send(message.toString());
    }

    @Transactional
    public void sendSms(TerminalReceiptSmsRequestDTO receiptSmsRequestDTO) {
        Receipt receipt;
        if (receiptSmsRequestDTO.getReceiptId() != null) {
            receipt = receiptRepository.findById(receiptSmsRequestDTO.getReceiptId()).orElseThrow(notFoundExceptionThrow(Receipt.class.getSimpleName(), "id", receiptSmsRequestDTO.getReceiptId()));
        } else if (StringUtils.isNotBlank(receiptSmsRequestDTO.getUid())) {
            receipt = receiptRepository.findFirstByUidAndDeletedIsFalse(receiptSmsRequestDTO.getUid()).orElseThrow(notFoundExceptionThrow(Receipt.class.getSimpleName(), "uid", receiptSmsRequestDTO.getUid()));
        } else if (StringUtils.isNotBlank(receiptSmsRequestDTO.getFiscalUrl())) {
            receipt = receiptRepository.findFirstByFiscalUrlAndDeletedIsFalse(receiptSmsRequestDTO.getFiscalUrl()).orElseThrow(notFoundExceptionThrow(Receipt.class.getSimpleName(), "fiscalUrl", receiptSmsRequestDTO.getFiscalUrl()));
        } else {
            throw badRequestExceptionThrow("Идентификатор или UID является обязательным").get();
        }
        if (StringUtils.isBlank(receipt.getFiscalUrl())) {
            throw badRequestExceptionThrow("Чек не фискальный").get();
        }
        this.sendSms(receipt, receiptSmsRequestDTO.getPhone(), receiptSmsRequestDTO.getLanguage());
    }

    private void sendSms(Receipt receipt, String phone, final String language) {
        try {
            StringBuilder content = new StringBuilder();
            if ("uz".equals(language)) {
                if (receipt.getStatus().equals(ReceiptStatus.PAID)) {
                    content.append(receipt.getCompany().getName()).append(" \nTo'lov: ").append(receipt.getTotalPaid().setScale(2, RoundingMode.HALF_UP)).append(" so'm \n");
                } else if (receipt.getStatus().equals(ReceiptStatus.RETURNED)) {
                    content.append(receipt.getCompany().getName()).append(" \nQaytarish: ").append(receipt.getTotalPaid().setScale(2, RoundingMode.HALF_UP)).append(" so'm \n");
                }
                content.append("https://internal.example.com/").append(Base64.getEncoder().encodeToString(receipt.getUid().getBytes())).append(" \n \n50 000 sovg'aga! ").append("\nA-Pay ilovasini yuklab oling  https://getapp.a-pay.uz");
            } else {
                if (receipt.getStatus().equals(ReceiptStatus.PAID)) {
                    content.append(receipt.getCompany().getName()).append(" \nOplata: ").append(receipt.getTotalPaid().setScale(2, RoundingMode.HALF_UP)).append(" sum \n");
                } else if (receipt.getStatus().equals(ReceiptStatus.RETURNED)) {
                    content.append(receipt.getCompany().getName()).append(" \nVozvrat: ").append(receipt.getTotalPaid().setScale(2, RoundingMode.HALF_UP)).append(" sum \n");
                }
                content.append("https://internal.example.com/").append(Base64.getEncoder().encodeToString(receipt.getUid().getBytes())).append(" \n \nDarim 50 000! ").append("\nSkachayte prilojeniye A-Pay https://getapp.a-pay.uz");
            }
            if (StringUtils.isNotBlank(phone)) {
                smsService.send(SmsRequestDTO.builder().phone(phone).code(content.toString()).build());
            }
        } catch (Exception e) {
            log.error("Send receipt via sms: {}", e.getMessage());
        }
    }

    @Transactional
    public Long update(Long id, TerminalReceiptDTO receiptDTO) {
        return receiptRepository.findById(id).map(receipt -> {
            if (!ReceiptStatus.DRAFT.equals(receipt.getStatus())) {
                throw badRequestExceptionThrow("You can not edit receipt which is already paid").get();
            }
            receiptDTO.setStatus(ReceiptStatus.DRAFT);
            saveAndReturn(receiptDTO, receipt);
            return receipt.getId();
        }).orElseThrow(notFoundExceptionThrow(Receipt.class.getSimpleName(), "id", id));
    }

    public Page<ReceiptListAdminDTO> getListForAdmin(ReceiptFilter filter) {
        return receiptRepository.getResultList(filter)
                .map(receipt -> {
                    ReceiptListAdminDTO receiptListDTO = new ReceiptListAdminDTO();
                    receiptListDTO.setId(receipt.getId());
                    receiptListDTO.setReceiptDate(receipt.getReceiptDate());
                    receiptListDTO.setTotalCost(receipt.getTotalCost());
                    receiptListDTO.setTotalVat(receipt.getTotalVat());
                    receiptListDTO.setTotalCash(receipt.getTotalCash());
                    receiptListDTO.setStatus(receipt.getStatus());
                    if (receipt.getTerminalId() != null) {
                        receiptListDTO.setTerminalSerialNumber(receipt.getTerminal().getSerialNumber());
                    }
                    if (receipt.getOrderId() != null && receipt.getOrder().getTable() != null && receipt.getOrder().getTable().getHallId() != null) {
                        receiptListDTO.setHall(receipt.getOrder().getTable().getHall().toCommonDTO());
                    }
                    if (receipt.getOrderId() != null && receipt.getOrder().getTable() != null) {
                        receiptListDTO.setTable(receipt.getOrder().getTable().toCommonDTO());
                    }
                    Map<ReceiptPaymentType, BigDecimal> paymentsMap = new HashMap<>();
                    receipt.getPayments().forEach(receiptPayment -> paymentsMap.merge(receiptPayment.getPaymentType(), receiptPayment.getPrice(), BigDecimal::add));
                    receiptListDTO.setPayments(paymentsMap);
                    return receiptListDTO;
                });
    }

    public Page<ReceiptListCabinetDTO> getListForCabinet(ReceiptFilter filter) {
        Page<Receipt> resultList = receiptRepository.getResultList(filter);
        return resultList.map(receipt -> {
            ReceiptListCabinetDTO receiptListDTO = new ReceiptListCabinetDTO();
            receiptListDTO.setId(receipt.getId());
            receiptListDTO.setReceiptDate(receipt.getReceiptDate());
            receiptListDTO.setTotalCost(receipt.getTotalCost());
            receiptListDTO.setTotalVat(receipt.getTotalVat());
            receiptListDTO.setTotalCash(receipt.getTotalCash());
            receiptListDTO.setStatus(receipt.getStatus());
            receiptListDTO.setShiftNumber(receipt.getShiftNumber());
            if (receipt.getOrderId() != null && receipt.getOrder().getTable() != null && receipt.getOrder().getTable().getHallId() != null) {
                receiptListDTO.setHall(receipt.getOrder().getTable().getHall().toCommonDTO());
            }
            if (receipt.getOrderId() != null && receipt.getOrder().getTable() != null) {
                receiptListDTO.setTable(receipt.getOrder().getTable().toCommonDTO());
            }
            if (receipt.getTerminalId() != null) {
                receiptListDTO.setTerminalSerialNumber(receipt.getTerminal().getSerialNumber());
            }
            Map<ReceiptPaymentType, BigDecimal> paymentsMap = new HashMap<>();
            receipt.getPayments().forEach(receiptPayment -> paymentsMap.merge(receiptPayment.getPaymentType(), receiptPayment.getPrice(), BigDecimal::add));
            receiptListDTO.setPayments(paymentsMap);
            return receiptListDTO;
        });
    }

    public List<TerminalReceiptDTO> getListForTerminal(ReceiptFilter filter) {
        filter.setSize(1000);
        filter.setPage(0);
        String serialNumber = getCurrentTerminalSerialNumber();
        Terminal terminal = terminalRepository.findFirstBySerialNumberAndDeletedIsFalse(serialNumber).orElseThrow(notFoundExceptionThrow(Terminal.class.getSimpleName(), "serialNumber", serialNumber));
        if (terminal.getBranch() == null || !terminal.getBranch().getCompanyId().equals(getCurrentCompanyId())) {
            return new ArrayList<>();
        }
        filter.setTerminalId(terminal.getId());
        filter.setStatus(ReceiptStatus.DRAFT.getCode());

        Page<Receipt> resultList = receiptRepository.getResultList(filter);
        List<TerminalReceiptDTO> result = new ArrayList<>();
        resultList.forEach(receipt -> result.add(getTerminalReceiptDTO(receipt, false, receiptDetailRepository.findFirstByReceiptId(receipt.getId()).orElseThrow(notFoundExceptionThrow(ReceiptDetail.class.getSimpleName(), "receiptId", receipt.getId())))));
        return result;
    }

    public ReceiptStatisticDTO getStatistics(ReceiptFilter filter) {
        return receiptRepository.getStatistics(filter);
    }

    public void attachReceipt(TerminalApayAttachCheckRequest request) {
        apayClient.attachReceipt(request);
    }

    public void print(TerminalReceiptDTO terminalReceiptDTO) {
        terminalReceiptDTO.setCompanyId(getCurrentCompanyId());
        terminalReceiptDTO.setBranchId(getCurrentBranchId());
        TerminalUserDTO userDTO = new TerminalUserDTO();
        userDTO.setId(getCurrentUser().getId());
        userDTO.setFullName(new TerminalFullNameDTO(getCurrentUser().getFirstName(), getCurrentUser().getLastName(), getCurrentUser().getPatronymic()));
        terminalReceiptDTO.setUser(userDTO);
        terminalReceiptDTO.setUserId(getCurrentUserId());
        terminalReceiptDTO.setSource(TerminalSource.CASHBOX);
        terminalReceiptDTO.setStatus(ReceiptStatus.DRAFT);
        if (terminalReceiptDTO.getTerminalId() == null && StringUtils.isEmpty(terminalReceiptDTO.getTerminalSN())) {
            terminalRepository.findFirstByBranchIdAndDeletedIsFalse(getCurrentBranchId()).ifPresent(terminal -> terminalReceiptDTO.setTerminalSN(terminal.getSerialNumber()));
        }
        if (terminalReceiptDTO.getOrderId() != null) {
            orderRepository.findById(terminalReceiptDTO.getOrderId()).map(order -> {
                terminalReceiptDTO.setUid(order.getUid());
                order.getBills().stream().findFirst().ifPresent(bill -> terminalReceiptDTO.setEpsQrCode(bill.getEpsQrCode()));
                return order;
            }).orElseThrow(notFoundExceptionThrow(Order.class.getSimpleName(), "id", terminalReceiptDTO.getOrderId()));
        }
        notificationService.sendNotification(terminalReceiptDTO, "smartpos", "receipt", terminalReceiptDTO.getTerminalSN());
    }

    @Transactional
    public void delete(Long id) {
        receiptRepository.findById(id).map(receipt -> {
            this.delete(receipt);
            return receipt;
        }).orElseThrow(notFoundExceptionThrow(Receipt.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void delete(String uid) {
        receiptRepository.findFirstByUidAndDeletedIsFalse(uid).map(receipt -> {
            this.delete(receipt);
            return receipt;
        }).orElseThrow(notFoundExceptionThrow(Receipt.class.getSimpleName(), "uid", uid));
    }

    @Transactional
    public void delete(Receipt receipt) {
        if (!ReceiptStatus.DRAFT.equals(receipt.getStatus())) {
            throw badRequestExceptionThrow("Receipt with status DRAFT can be deleted").get();
        }
        receipt.setDeleted(true);
        receiptRepository.save(receipt);
        if (receipt.getOrderId() != null) {
            Order order = receipt.getOrder();
            order.setReceiptId(null);
            order.setReceipt(null);
            orderRepository.save(order);
        }
    }
}
