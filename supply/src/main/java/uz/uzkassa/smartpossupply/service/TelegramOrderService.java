package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.domain.order.OrderProduct;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.customer.CustomerTelegramDTO;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderProductDTO;
import uz.uzkassa.smartpossupply.dto.order.telegram.TelegramOrderDTO;
import uz.uzkassa.smartpossupply.dto.order.telegram.TelegramOrderDetailDTO;
import uz.uzkassa.smartpossupply.dto.order.telegram.TelegramOrderListDTO;
import uz.uzkassa.smartpossupply.dto.product.TelegramProductListDTO;
import uz.uzkassa.smartpossupply.dto.telegram.TelegramCategoryListDTO;
import uz.uzkassa.smartpossupply.dto.telegram.TelegramTopProductListDTO;
import uz.uzkassa.smartpossupply.enums.OrderSource;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.OrderType;
import uz.uzkassa.smartpossupply.enums.TelegramBotType;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.filters.TelegramCategoryFilter;
import uz.uzkassa.smartpossupply.repository.CategoryRepository;
import uz.uzkassa.smartpossupply.repository.CustomerBranchRepository;
import uz.uzkassa.smartpossupply.repository.OrderProductRepository;
import uz.uzkassa.smartpossupply.repository.OrderRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.repository.TelegramIntegrationRepository;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TelegramOrderService extends BaseService {

    OrderService orderService;
    CustomerBranchRepository customerBranchRepository;
    CategoryRepository categoryRepository;
    ProductRepository productRepository;
    TelegramIntegrationRepository telegramIntegrationRepository;
    OrderRepository orderRepository;
    OrderProductRepository orderProductRepository;

    @Transactional
    public Long create(TelegramOrderDTO telegramOrderDTO, String token, Long userId) {
        Long customerId = telegramOrderDTO.getCustomerId() != null ? telegramOrderDTO.getCustomerId() : userId;
        Long supplierId;
        Long branchId;
        Long warehouseId;
        Long customerBranchId;

        if (telegramOrderDTO.getCustomerId() == null) {
            CustomerBranch customerBranch = customerBranchRepository.getMain(customerId);
            if (customerBranch == null) {
                throw validationService.badRequestExceptionThrow("Customer branch not found").get();
            }
            supplierId = customerBranch.getBranch().getCompanyId();
            branchId = customerBranch.getBranchId();
            warehouseId = customerBranch.getWarehouseId();
            customerBranchId = customerBranch.getId();
        } else {
            Long tokenCompanyId = telegramIntegrationRepository.getCompanyIdByBotToken(token);
            if (tokenCompanyId == null) {
                throw validationService.badRequestExceptionThrow("Telegram integration not found for token").get();
            }
            supplierId = tokenCompanyId;
            CustomerBranch employeeBranch = customerBranchRepository.getMain(userId);
            if (employeeBranch == null) {
                throw validationService.badRequestExceptionThrow("Employee branch not found").get();
            }
            branchId = employeeBranch.getBranchId();
            warehouseId = employeeBranch.getWarehouseId();
            customerBranchId = null;
        }

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setSupplierId(supplierId);
        orderDTO.setCustomerId(customerId);
        orderDTO.setBranchId(branchId);
        orderDTO.setWarehouseId(warehouseId);
        orderDTO.setDeliveryBranchId(customerBranchId);
        orderDTO.setOrderDate(LocalDateTime.now());
        orderDTO.setOrderType(OrderType.INCOMING);
        orderDTO.setDescription(telegramOrderDTO.getDescription());
        orderDTO.setSource(OrderSource.TELEGRAM.getCode());
        orderDTO.setStatus(OrderStatus.NEW);
        orderDTO.setTotalQty(telegramOrderDTO.getTotalQty());
        orderDTO.setTotalPrice(telegramOrderDTO.getTotalPrice());
        orderDTO.setTotalForeignPrice(telegramOrderDTO.getTotalForeignPrice());
        orderDTO.setExchangeRate(telegramOrderDTO.getExchangeRate());
        orderDTO.setCurrencyType(telegramOrderDTO.getCurrencyType());
        orderDTO.setTotalPriceWithoutVat(telegramOrderDTO.getTotalPriceWithoutVat());
        orderDTO.setTotalVatPrice(telegramOrderDTO.getTotalVatPrice());
        orderDTO.setTotalDiscountPrice(telegramOrderDTO.getTotalDiscountPrice());
        List<OrderProductDTO> orderProducts = telegramOrderDTO.getProducts().stream().map(telegramOrderProductDTO -> {
            OrderProductDTO orderProductDTO = new OrderProductDTO();
            orderProductDTO.setId(telegramOrderProductDTO.getId());
            orderProductDTO.setUnitId(telegramOrderProductDTO.getUnitId());
            orderProductDTO.setQty(telegramOrderProductDTO.getQty());
            orderProductDTO.setPrice(telegramOrderProductDTO.getPrice());
            orderProductDTO.setForeignPrice(telegramOrderProductDTO.getForeignPrice());
            orderProductDTO.setPriceWithoutVat(telegramOrderProductDTO.getPriceWithoutVat());
            orderProductDTO.setTotalPrice(telegramOrderProductDTO.getTotalPrice());
            orderProductDTO.setTotalForeignPrice(telegramOrderProductDTO.getTotalForeignPrice());
            orderProductDTO.setTotalPriceWithoutVat(telegramOrderProductDTO.getTotalPriceWithoutVat());
            orderProductDTO.setVatRate(telegramOrderProductDTO.getVatRate());
            orderProductDTO.setVatPrice(telegramOrderProductDTO.getVatPrice());
            orderProductDTO.setTotalVatPrice(telegramOrderProductDTO.getTotalVatPrice());
            orderProductDTO.setPriceListPrice(telegramOrderProductDTO.getPriceListPrice());
            orderProductDTO.setExciseRate(telegramOrderProductDTO.getExciseRate());
            orderProductDTO.setExciseSum(telegramOrderProductDTO.getExciseSum());
            return orderProductDTO;
        }).toList();
        orderDTO.setProducts(orderProducts);
        return orderService.create(orderDTO);
    }

    public Page<TelegramOrderListDTO> getList(OrderFilter filter) {
        Long supplierId = telegramIntegrationRepository.getCompanyIdByBotToken(filter.getToken());
        if (supplierId == null) {
            throw validationService.notFoundExceptionThrow("Telegram is not integrated for this company").get();
        }
        if (TelegramBotType.CUSTOMER.getCode().equals(filter.getBotType())) {
            Long customerId = companyRepository.findFirstByTelegramIdAndSupplierId(supplierId, filter.getTelegramUserId());
            if (customerId == null) {
                throw validationService.notFoundExceptionThrow("Customer not found").get();
            }
            filter.setCustomerId(customerId);
            filter.setSupplierId(supplierId);
            filter.setCompanyId(supplierId);
        }
        if (TelegramBotType.SUPPLIER.getCode().equals(filter.getBotType())) {
            if (filter.getCreatorId() == null) {
                throw validationService.badRequestExceptionThrow("Creator id is not specified").get();
            }
            filter.setSupplierId(supplierId);
            filter.setCompanyId(supplierId);
        }
        filter.setSource(OrderSource.TELEGRAM.getCode());

        ResultList<Order> resultList = orderRepository.getResultList(filter);
        List<TelegramOrderListDTO> result = resultList
                .getList()
                .stream()
                .map(order -> {
                    TelegramOrderListDTO telegramOrderListDTO = new TelegramOrderListDTO();
                    telegramOrderListDTO.setId(order.getId());
                    telegramOrderListDTO.setOrderNumber(order.getOrderNumber());
                    telegramOrderListDTO.setOrderDate(order.getCreatedDate());
                    telegramOrderListDTO.setStatus(order.getStatus());
                    telegramOrderListDTO.setTotalPrice(order.getTotalPrice());
                    return telegramOrderListDTO;
                }).toList();

        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public TelegramOrderDetailDTO get(Long id) {
        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderId(id);
        TelegramOrderDetailDTO telegramOrderDetailDTO = new TelegramOrderDetailDTO();
        telegramOrderDetailDTO.setId(id);
        List<TelegramProductListDTO> products = orderProducts.stream().map(orderProduct -> {
            TelegramProductListDTO telegramProductListDTO = new TelegramProductListDTO();
            telegramProductListDTO.setId(orderProduct.getId());
            telegramProductListDTO.setName(orderProduct.getName());
            telegramProductListDTO.setQty(orderProduct.getQty());
            telegramProductListDTO.setUnitPrice(orderProduct.getPrice());
            telegramProductListDTO.setTotalPrice(orderProduct.getTotalPrice());
            return telegramProductListDTO;

        }).toList();
        telegramOrderDetailDTO.setProducts(products);
        return telegramOrderDetailDTO;
    }

    public FileDTO getCompanyBackgroundPhoto(String token) {
        return companyRepository.findByIdWithBackgroundImage(getCompanyId(token)).map(company -> company.getBackgroundImage().toFileDTO()).orElse(null);
    }

    public FileDTO getCompanyLogo(String token) {
        return companyRepository.findByIdWithLogo(getCompanyId(token)).map(company -> company.getLogo().toFileDTO()).orElse(null);
    }

    public List<TelegramCategoryListDTO> getCategoryList(TelegramCategoryFilter filter) {
        filter.setBranchId(getBranchId(filter.getToken()));
        return categoryRepository.getListForTelegram(filter);
    }

    public List<TelegramTopProductListDTO> getTopProducts(String token) {
        return productRepository.getTopProductsForTelegram(getBranchId(token));
    }

    public Long checkCustomer(String token, Long userId, String botType) {
        if (botType.equals(TelegramBotType.CUSTOMER.getCode())) {
            return companyRepository.findFirstByTelegramIdAndSupplierId(getCompanyId(token), userId);
        }
        if (botType.equals(TelegramBotType.SUPPLIER.getCode())) {
            return userRepository.findIdByCompanyIdAndTelegramUserId(getCompanyId(token), userId);
        }
        throw validationService.badRequestExceptionThrow("Bot type is not specified or incorrect").get();
    }

    @Transactional
    public Long checkEmployee(CustomerTelegramDTO customerTelegramDTO) {
        Long companyId = getCompanyId(customerTelegramDTO.getToken());

        User user = userRepository.findFirstByCompanyIdAndPhone(companyId, customerTelegramDTO.getPhone())
                .orElseThrow(() -> validationService.badRequestExceptionThrow("Сотрудник не найден", "Сотрудник с номером " + customerTelegramDTO.getPhone() + " не найден в данной компании").get());

        Long userId = user.getId();
        Long telegramUserId = user.getTelegramUserId();
        Long incomingTelegramUserId = customerTelegramDTO.getUserId();

        if (telegramUserId == null) {
            user.setTelegramUserId(incomingTelegramUserId);
            userRepository.save(user);
            return userId;
        }

        if (telegramUserId.equals(incomingTelegramUserId)) {
            return userId;
        }

        throw validationService.badRequestExceptionThrow("Телеграм ID не совпадает", "Этот номер уже привязан к другому Telegram пользователю").get();
    }

    private Long getBranchId(String token) {
        return this.getBranchId(this.getCompanyId(token));
    }

    private Long getBranchId(Long companyId) {
        final Long branchId = branchRepository.getMainBranchId(companyId);
        if (branchId == null) {
            throw validationService.badRequestExceptionThrow("Company main branch not found").get();
        }
        return branchId;
    }

    private Long getCompanyId(String token) {
        final Long companyId = telegramIntegrationRepository.getCompanyIdByBotToken(token);
        if (companyId == null) {
            throw validationService.badRequestExceptionThrow("Telegram is not integrated for this company").get();
        }
        return companyId;
    }
}
