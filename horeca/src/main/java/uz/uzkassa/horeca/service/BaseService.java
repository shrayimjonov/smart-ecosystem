package uz.uzkassa.horeca.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.config.ApplicationProperties;
import uz.uzkassa.horeca.constants.Constants;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.domain.receipt.Receipt;
import uz.uzkassa.horeca.domain.receipt.ReceiptDetail;
import uz.uzkassa.horeca.domain.receipt.ReceiptItem;
import uz.uzkassa.horeca.domain.unit.MultiUnit;
import uz.uzkassa.horeca.domain.unit.Unit;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.dto.menu.MenuItemDetailItemDTO;
import uz.uzkassa.horeca.dto.product.ProductCommonDTO;
import uz.uzkassa.horeca.dto.product.ProductLookUpDTO;
import uz.uzkassa.horeca.dto.product.ProductPriceDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalFullNameDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalReceiptDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalReceiptDetailDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalUserDTO;
import uz.uzkassa.horeca.dto.unit.MultiUnitListDTO;
import uz.uzkassa.horeca.enums.ReceiptPaymentType;
import uz.uzkassa.horeca.executor.AfterCommitExecutor;
import uz.uzkassa.horeca.mappers.AddressMapper;
import uz.uzkassa.horeca.repository.*;
import uz.uzkassa.horeca.utils.BeanUtils;
import uz.uzkassa.horeca.utils.CalcUtils;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.web.rest.errors.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Component
@Slf4j
public abstract class BaseService implements Constants {

    @Autowired
    protected LocalizationService localizationService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected CompanyRepository companyRepository;

    @Autowired
    protected UnitRepository unitRepository;

    @Autowired
    protected BranchRepository branchRepository;

    @Autowired
    protected ApplicationProperties applicationProperties;

//    @Autowired
//    protected CacheManager cacheManager;

    @Autowired
    protected AddressMapper addressMapper;

    @Autowired
    protected CacheService cacheService;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UsagePlanRepository usagePlanRepository;

    @Autowired
    protected AfterCommitExecutor afterCommitExecutor;

    @Value("${security.authentication.jwt.token-validity-in-seconds-for-remember-me}")
    private Long tokenValidityInSecondsForRememberMe;

    protected static DecimalFormat COMMON_NUMBERING_FORMAT = new DecimalFormat("000000");

    protected static Long getCurrentUserId() {
        return HeaderUtils.getCurrentUserId();
    }

    protected static Long getCurrentCompanyId() {
        return HeaderUtils.getCurrentCompanyId();
    }

    protected static Long getCurrentCompanyId(Long companyId) {
        Long id = Optional.ofNullable(companyId).orElse(getCurrentCompanyId());
        if (id == null) {
            throw unauthorizedExceptionThrow().get();
        }
        return id;
    }

    protected static Long getCurrentBranchId() {
        return HeaderUtils.getCurrentBranchId();
    }

    protected static Long getCurrentBranchId(Long branchId) {
        return getCurrentBranchId(branchId, false);
    }

    protected static Long getCurrentBranchId(Long branchId, boolean throwException) {
        Long id = Optional.ofNullable(branchId).orElse(getCurrentBranchId());
        if (id == null && throwException) {
            throw badRequestExceptionThrow("Branch is required").get();
        }
        return id;
    }

    protected static Long getCurrentDepartmentId() {
        return HeaderUtils.getCurrentDepartmentId();
    }

    protected static Long getCurrentDepartmentId(Long departmentId) {
        Long id = Optional.ofNullable(departmentId).orElse(getCurrentDepartmentId());
        if (id == null) {
            throw badRequestExceptionThrow("Department is required").get();
        }
        return id;
    }

    protected User getCurrentUser() {
        String login = HeaderUtils.getCurrentUserLogin().orElseThrow(unauthorizedExceptionThrow());
        return userRepository
                .findFirstByLoginAndDeletedIsFalse(login)
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "login", login));
    }

    protected Unit getUnit() {
        return unitRepository.findFirstByCode(Unit.PC);
    }

    protected static boolean isFromAdmin() {
        return HeaderUtils.isFromAdmin();
    }

    protected Company getCurrentCompany() {
        Long companyId = HeaderUtils.getCurrentCompanyId();
        if (companyId == null) {
            throw unauthorizedExceptionThrow().get();
        }
        return companyRepository.findById(companyId).orElseThrow(notFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));
    }

    protected Company getCurrentCompany(Long companyId) {
        if (companyId != null) {
            return companyRepository
                    .findById(companyId)
                    .orElseThrow(notFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));
        }
        return this.getCurrentCompany();
    }

    protected Branch getCurrentBranch() {
        Long branchId = HeaderUtils.getCurrentBranchId();
        if (branchId == null) {
            throw unauthorizedExceptionThrow().get();
        }
        return branchRepository.findById(branchId).orElseThrow(notFoundExceptionThrow(Branch.class.getSimpleName(), "id", branchId));
    }

    protected String getCurrentTerminalSerialNumber() {
        String terminalSerialNumber = HeaderUtils.getCurrentTerminalSnByHeader();
        if (StringUtils.isEmpty(terminalSerialNumber)) {
            throw new AppGlobalException("Terminal serial number is null", "Terminal serial number is null", HttpStatus.BAD_REQUEST);
        }
        return terminalSerialNumber;
    }

    protected Branch getCurrentBranch(Long branchId) {
        if (branchId != null) {
            return branchRepository.findById(branchId).orElseThrow(notFoundExceptionThrow(Branch.class.getSimpleName(), "id", branchId));
        }
        return this.getCurrentBranch();
    }

    protected String getCurrentCompanyTin() {
        return getCurrentCompany().getTin();
    }

    protected static Supplier<NotFoundException> notFoundExceptionThrow(String entityName, String param, Object value) {
        LocalizationService localizationService = BeanUtils.getBean(LocalizationService.class);
        return () ->
                new NotFoundException(
                        BeanUtils
                                .getBean(LocalizationService.class)
                                .localize("entity.not.found.with.client", new Object[]{localizationService.localize(entityName, entityName)}),
                        localizationService.localize("entity.not.found.with.dev", new Object[]{entityName, param, value})
                );
    }

    protected Supplier<NotFoundException> notFoundExceptionSupplier(Class<?> tClass, String param, Object value) {
        return () ->
                new NotFoundException(
                        localize("entity.not.found.with.client", tClass.getSimpleName()),
                        localize("entity.not.found.with.dev", tClass.getSimpleName(), param, value)
                );
    }

    protected static Supplier<NotFoundException> notFoundExceptionThrow(String message) {
        return notFoundExceptionThrow(null, message);
    }

    protected static Supplier<NotFoundException> notFoundExceptionThrow(String title, String message) {
        return () -> new NotFoundException(title, message);
    }

    protected static Supplier<BadRequestException> badRequestExceptionThrow(String message) {
        return () -> new BadRequestException(message);
    }

    protected static Supplier<UserNotActivatedException> userNotActivationExceptionThrow(String message) {
        return () -> new UserNotActivatedException(message);
    }

    protected static Supplier<ConflictException> conflictExceptionThrow(String message) {
        return () -> new ConflictException(message);
    }

    protected static Supplier<UnauthorizedException> unauthorizedExceptionThrow() {
        LocalizationService localizationService = BeanUtils.getBean(LocalizationService.class);
        return () -> new UnauthorizedException(localizationService.localize("Session expired"));
    }

    protected void calculateFeeAmount(Order order) {
        BigDecimal feePercent = Optional.ofNullable(order.getTable())
                .flatMap(hallItem -> Optional.of(hallItem.getFeePercent()))
                .orElse(BigDecimal.ZERO);

        order.setFeePercent(feePercent);
        order.setFeeAmount(CalcUtils.calcCateringServiceAmount(feePercent, order.getTotalPrice()));
    }

    public Long getTokenTTL() {
        return tokenValidityInSecondsForRememberMe;
    }
    public ProductLookUpDTO toProductLookUpDTO(Product product) {
        return ProductLookUpDTO
                .builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .packageCode(product.getPackageCode())
                .packageName(product.getPackageName())
                .catalogCode(product.getCatalogCode())
                .catalogName(product.getCatalogName())
                .vat(product.getVat())
                .price(product.getPrice())
                .unit(product.getUnitId() != null ? product.getUnit().toCommonDTO() : null)
                .hasMark(product.isHasMark())
                .origin(product.getOrigin())
                .category(product.getCategory() != null ? product.getCategory().toCommonDTO() : null)
                .productType(product.getProductType())
                .hasTechCard(product.isHasTechCard())
                .build();
    }

    public ProductPriceDTO toProductPriceDTO(Product product) {
        return new ProductPriceDTO(product.getId(), product.getName(), product.getPrice());
    }

    public ProductCommonDTO toProductCommonDTO(Product product) {
        return ProductCommonDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .barcode(product.getBarcode())
                .catalogCode(product.getCatalogCode())
                .vat(product.getVat())
                .origin(product.getOrigin())
                .hasMark(product.isHasMark())
                .packageCode(product.getPackageCode())
                .packageName(product.getPackageName())
                .category(product.getCategory() != null ? product.getCategory().toCommonDTO() : null)
                .build();
    }

    protected MenuItemDetailItemDTO toMenuItemDetailDTO(MenuItem menuItem) {
        MenuItemDetailItemDTO menuItemDetailProductDTO = new MenuItemDetailItemDTO();
        menuItemDetailProductDTO.setId(menuItem.getId());
        menuItemDetailProductDTO.setName(menuItem.getName());
        menuItemDetailProductDTO.setMenu(menuItem.getMenu().toCommonDTO());
        if (menuItem.getProduct() != null) {
            menuItemDetailProductDTO.setDepartment(menuItem.getProduct().getDepartment().toCommonDTO());
            ProductCommonDTO productCommonDTO = toProductCommonDTO(menuItem.getProduct());
            menuItemDetailProductDTO.setProduct(productCommonDTO);
        }
        return menuItemDetailProductDTO;
    }
    public CompanyCommonDTO toCompanyBaseDTO(Company company) {
        return new CompanyCommonDTO(company.getId(), company.getName(), company.getTin());
    }

    public MultiUnitListDTO toMultiUnitListDTO(MultiUnit multiUnit) {
        MultiUnitListDTO multiUnitItemDTO = new MultiUnitListDTO();
        multiUnitItemDTO.setId(multiUnit.getId());
        multiUnitItemDTO.setPrice(multiUnit.getPrice());
        multiUnitItemDTO.setWeight(multiUnit.getWeight());
        if (multiUnit.getTechCardId() != null) {
            multiUnitItemDTO.setTechCard(multiUnit.getTechCard().toCommonDTO());
            multiUnitItemDTO.setUnit(multiUnit.getTechCard().getUnit().toCommonDTO());
        } else {
            if (multiUnit.getMenuItem().getProduct().getUnit() != null) {
                multiUnitItemDTO.setUnit(multiUnit.getMenuItem().getProduct().getUnit().toCommonDTO());
            }
        }
        return multiUnitItemDTO;
    }

    public TerminalReceiptDTO getTerminalReceiptDTO(Receipt receipt, boolean noFiscal, ReceiptDetail receiptDetail) {
        TerminalReceiptDTO receiptDTO = new TerminalReceiptDTO();

        receiptDTO.setUid(receipt.getUid());
        receiptDTO.setId(receipt.getId());
        receiptDTO.setLat(receiptDetail.getLatitude());
        receiptDTO.setLon(receiptDetail.getLongitude());
        if (!noFiscal) {
            receiptDTO.setFiscalSign(receipt.getFiscalSign());
            receiptDTO.setFiscalUrl(receipt.getFiscalUrl());
        }
        receiptDTO.setCreatedDateTime(receipt.getCreatedDate());
        receiptDTO.setReceiptDateTime(receipt.getReceiptDate());
        receiptDTO.setTotalCash(receipt.getTotalCash());
        receiptDTO.setTotalCard(receipt.getTotalCard());
        receiptDTO.setTotalCost(receipt.getTotalCost());
        receiptDTO.setTotalPaid(receipt.getTotalPaid());
        receiptDTO.setTotalDiscount(receipt.getTotalDiscount());
        receiptDTO.setTotalExcise(receipt.getTotalExcise());
        receiptDTO.setDiscountPercent(receipt.getDiscountPercent());
        receiptDTO.setCustomerName(receiptDetail.getCustomerName());
        receiptDTO.setCustomerPhone(receiptDetail.getCustomerPhone());
        receiptDTO.setReadOnly(receipt.isReadOnly());
        receiptDTO.setForceToPrint(receipt.isForceToPrint());
        receiptDTO.setTerminalId(receipt.getTerminalId());
        receiptDTO.setOrderId(receipt.getOrderId());
        if (receipt.getOrderId() != null) {
            receiptDTO.setOrder(receipt.getOrder().toCommonDTO());
            receiptDTO.setOrderStatus(receipt.getOrder().getStatus());
            if (receipt.getOrder().getTableId() != null) {
                receiptDTO.setTable(receipt.getOrder().getTable().toCommonDTO());
            }
        }

        if (receipt.getTerminalId() != null) {
            receiptDTO.setTerminalSN(receipt.getTerminal().getSerialNumber());
        }
        receiptDTO.setBranchId(receipt.getBranchId());
        if (receipt.getBranchId() != null) {
            receiptDTO.setBranchName(receipt.getBranch().getName());
            receiptDTO.setBranchAddress(receipt.getBranch().getAddress().getFullAddress());
        }
        receiptDTO.setCompanyId(receipt.getCompanyId());
        if (receipt.getCompanyId() != null) {
            receiptDTO.setCompanyName(receipt.getCompany().getName());
            receiptDTO.setCompanyINN(receipt.getCompany().getTin());
        }
        if (!CollectionUtils.isEmpty(receipt.getItems())) {
            LinkedList<TerminalReceiptDetailDTO> details = new LinkedList<>();
            for (ReceiptItem receiptItem : receipt.getItems()) {
                details.add(toReceiptItemDTO(receiptItem));
            }
            receiptDTO.setReceiptDetails(details);
        }
        receiptDTO.setStatus(receipt.getStatus());
        receiptDTO.setShiftNo(receipt.getShiftNumber());
        receiptDTO.setTotalNds(receipt.getTotalVat());
        if (receipt.getCreatorId() != null) {
            TerminalUserDTO userDTO = new TerminalUserDTO();
            userDTO.setId(receipt.getCreator().getId());
            userDTO.setFullName(new TerminalFullNameDTO(receipt.getCreator().getFirstName(), receipt.getCreator().getLastName(), receipt.getCreator().getPatronymic()));
            receiptDTO.setUser(userDTO);
        }
        if (!CollectionUtils.isEmpty(receipt.getPayments())) {
            Map<ReceiptPaymentType, BigDecimal> payments = new HashMap<>();
            receipt.getPayments().forEach(receiptPayment -> payments.put(receiptPayment.getPaymentType(), receiptPayment.getPrice()));
            receiptDTO.setPayments(payments);
        }

        receiptDTO.setAdditional(receiptDetail.getAdditional());
        receiptDTO.setBillId(receipt.getBillId());
        receiptDTO.setFacturaId(receipt.getFacturaId());
        receiptDTO.setSendSms(receiptDetail.isSendSms());
        return receiptDTO;
    }

    protected TerminalReceiptDetailDTO toReceiptItemDTO(ReceiptItem receiptItem) {
        TerminalReceiptDetailDTO detail = new TerminalReceiptDetailDTO();
        detail.setId(receiptItem.getId());
        detail.setProductId(receiptItem.getProductId());
        detail.setProductName(receiptItem.getProductName());
        detail.setReceiptId(receiptItem.getReceipt().getId());
        if (StringUtils.isNotEmpty(receiptItem.getBarcode())) {
            if (receiptItem.getBarcode().contains("$")) {
                detail.setProductBarcode(StringUtils.split(receiptItem.getBarcode(), "$")[0]);
            } else {
                detail.setProductBarcode(receiptItem.getBarcode());
            }
        }
        if (StringUtils.isNotEmpty(receiptItem.getCatalogCode())) {
            detail.setVatBarcode(receiptItem.getCatalogCode());
        } else if (StringUtils.isNotEmpty(receiptItem.getBarcode()) && receiptItem.getBarcode().contains("$")) {
            detail.setVatBarcode(StringUtils.split(receiptItem.getBarcode(), "$")[1]);
        }
        detail.setUnitId(receiptItem.getUnitId());
        detail.setUnitName(receiptItem.getUnitName());
        detail.setCategoryId(receiptItem.getCategoryId());
        detail.setCategoryName(receiptItem.getCatalogName());
        detail.setExcise(receiptItem.getExcise());
        detail.setExciseRate(receiptItem.getExciseRate());
        detail.setPrice(receiptItem.getPrice());
        detail.setQty(receiptItem.getQty());
        detail.setAmount(receiptItem.getTotalPrice());
        detail.setDiscount(receiptItem.getDiscount());
        detail.setDiscountPercent(receiptItem.getDiscountPercent());
        detail.setNds(receiptItem.getVatPrice());
        detail.setNdsPercent(receiptItem.getVatRate());
        if (!CollectionUtils.isEmpty(receiptItem.getMarks())) {
            detail.setMarks(receiptItem.getMarks());
        }
        detail.setOrigin(receiptItem.getOrigin());
        detail.setHasMark(receiptItem.getProduct().isHasMark());
        detail.setExtraDetail(receiptItem.getExtraDetail());
        return detail;
    }

    public String localize(String code, Object... params) {
        return localizationService.localize(code, params, HeaderUtils.getLocale());
    }
}
