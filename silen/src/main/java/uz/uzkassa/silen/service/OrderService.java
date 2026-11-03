package uz.uzkassa.silen.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.domain.*;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.OrderFilter;
import uz.uzkassa.silen.dto.marking.*;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.integration.TuronClient;
import uz.uzkassa.silen.integration.turon.CheckOrderStatusResponse;
import uz.uzkassa.silen.integration.turon.CreateOrderResponse;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.pg.OrderProductRepository;
import uz.uzkassa.silen.repository.pg.OrderRepository;
import uz.uzkassa.silen.repository.pg.PartyRepository;
import uz.uzkassa.silen.repository.pg.ProductRepository;
import uz.uzkassa.silen.utils.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Order}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService extends BaseService {
    OrderRepository orderRepository;
    OrderProductRepository orderProductRepository;
    ProductRepository productRepository;
    MarkRepository markRepository;
    TuronClient turonClient;
    PartyRepository partyRepository;

    @Transactional
    public void save(final OrderDTO orderDTO) {
        final Organization organization = getCurrentOrganization();
        if (StringUtils.isAnyEmpty(organization.getOmsId(), organization.getTuronToken())) {
            throw badRequest("В данном организации OMS_ID или TOKEN отсутствует").get();
        }
        for (OrderProductItemDTO orderProductItemDTO : orderDTO.getProducts()) {
            if (SerialNumberType.SELF_MADE == orderProductItemDTO.getSerialNumberType()) {
                if (CollectionUtils.isEmpty(orderProductItemDTO.getSerialNumbers())) {
                    throw badRequest("В данном тип заказа должен быть заполнен коды").get();
                }
                if (orderProductItemDTO.getSerialNumbers().size() != orderProductItemDTO.getQuantity()) {
                    throw badRequest("Количество не правилно").get();
                }
            }
        }
        Order order = new Order();
        order.setOrganizationId(organization.getId());
        Integer maxIntNumber = orderRepository.getMaxIntNumber(organization.getId());
        maxIntNumber = maxIntNumber == null ? 1 : maxIntNumber + 1;
        order.setIntNumber(maxIntNumber);
        order.setNumber("ORDER-" + maxIntNumber);
        order.setCreateMethodType(CreateMethodType.SELF_MADE);//only for Alcohol
        order.setReleaseMethodType(Optional.ofNullable(orderDTO.getReleaseMethodType()).orElse(ReleaseMethodType.PRODUCTION));
        order.setDescription(order.getDescription());
        TuronOrderDTO turonOrderDTO = new TuronOrderDTO();
        turonOrderDTO.setContactPerson(organization.getContactPerson());
        turonOrderDTO.setCreateMethodType(order.getCreateMethodType());
        turonOrderDTO.setReleaseMethodType(order.getReleaseMethodType());
        if (ProductGroup.Tobacco == organization.getProductGroup()) {
            turonOrderDTO.setFactoryCountry(organization.getFactoryCountry());
            turonOrderDTO.setFactoryId(organization.getFactoryId());
            turonOrderDTO.setProductionLineId(organization.getTin());
            turonOrderDTO.setProductCode(organization.getTin());
            turonOrderDTO.setProductDescription(order.getNumber());
        }

        for (OrderProductItemDTO dto : orderDTO.getProducts()) {
            Product product = productRepository.findById(dto.getProductId()).orElse(null);
            if (product == null) continue;

            final OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProductId(product.getId());
            orderProduct.setProduct(product);
            orderProduct.setRateType(Optional.ofNullable(dto.getRateType()).orElse(RateType.STANDARD));
            orderProduct.setQuantity(dto.getQuantity());
            orderProduct.setRemaining(dto.getQuantity());
            orderProduct.setCisType(Optional.ofNullable(dto.getCisType()).orElse(CisType.UNIT));
            orderProduct.setSerialNumberType(Optional.ofNullable(dto.getSerialNumberType()).orElse(SerialNumberType.OPERATOR));
            if (SerialNumberType.UPLOAD.equals(dto.getSerialNumberType())) {
                orderProduct.setBufferStatus(BufferStatus.ACTIVE);
                order.addProduct(orderProduct);
                continue;
            }
            order.addProduct(orderProduct);

            TuronOrderProductDTO turonOrderProductDTO = new TuronOrderProductDTO();
            turonOrderProductDTO.setRateType(dto.getRateType().getCode());
            turonOrderProductDTO.setCisType(orderProduct.getCisType());
            turonOrderProductDTO.setQuantity(orderProduct.getQuantity());
            turonOrderProductDTO.setSerialNumberType(orderProduct.getSerialNumberType());
            if (SerialNumberType.SELF_MADE == orderProduct.getSerialNumberType()) {
                turonOrderProductDTO.setSerialNumbers(dto.getSerialNumbers());
            }
            if (CisType.GROUP.equals(orderProduct.getCisType())) {
                turonOrderProductDTO.setGtin(Utils.fillProductBarcode(product.getParentBarcode()));
                turonOrderProductDTO.setTemplateId(product.getType().getBlockTemplateId());
            } else {
                turonOrderProductDTO.setGtin(Utils.fillProductBarcode(product.getBarcode()));
                turonOrderProductDTO.setTemplateId(product.getType().getTemplateId());
            }
            turonOrderDTO.addProduct(turonOrderProductDTO);
        }

        if (!CollectionUtils.isEmpty(turonOrderDTO.getProducts())) {
            CreateOrderResponse responseDTO = turonClient.createOrder(turonOrderDTO, organization.getProductGroup().getExtension(), organization.getTuronToken(), organization.getOmsId());
            order.setTuronId(responseDTO.getOrderId());
            order.setExpectedCompleteTimestamp(responseDTO.getExpectedCompleteTimestamp());
        }
        orderRepository.save(order);
    }

    public OrderProductDTO findById(String id) {
        OrderProduct orderProduct = orderProductRepository.findById(id).orElseThrow(notFound());
        OrderProductDTO dto = orderProduct.toDTO();
        dto.setParties(orderProduct.getParties()
                .stream().map(Party::toDto).collect(Collectors.toSet()));

        return dto;
    }

    public Page<OrderProductDTO> findAll(OrderFilter filter) {
        if (filter.checkSearchIsEmpty() && Utils.isMark(filter.getSearch())) {
            Optional<Mark> mark = markRepository.findFirstByCode(Utils.clearPrintCode(filter.getSearch()));
            mark.ifPresent(value -> filter.setOrderProductId(value.getOrderProductId()));
        }
        return orderProductRepository.findAllByFilter(filter).map(OrderProduct::toDTO);
    }

    public List<SelectItem> getItems(OrderFilter filter) {
        filter.setStatusIn(Arrays.asList(BufferStatus.ACTIVE, BufferStatus.CLOSED));
        return orderProductRepository.findAllByFilter(filter)
                .map(OrderProduct::toSelectItem)
                .getContent();
    }

    @Transactional
    public void checkStatus(final List<OrderProduct> list) {
        for (OrderProduct orderProduct : list) {
            checkStatus(orderProduct);
        }
    }

    @Transactional
    public void checkStatus(final String orderProductId) {
        final OrderProduct orderProduct = orderProductRepository.findById(orderProductId).orElseThrow(notFound());
        checkStatus(orderProduct);
    }

    public void checkStatus(final OrderProduct orderProduct) {
        final String orderId = orderProduct.getOrder() != null ? orderProduct.getOrder().getTuronId() : null;
        final Order order = orderProduct.getOrder();
        if (order == null) return;
        final Organization organization = order.getOrganization() != null ? order.getOrganization() : null;
        if (organization == null) {
            return;
        }
        if (StringUtils.isAnyEmpty(organization.getOmsId(), organization.getTuronToken())) {
            final StringBuilder message = new StringBuilder("OMS OR TOKEN REQUIRED").append("\n");
            message.append("TIN: ").append(organization.getTin()).append("\n");
            message.append("NAME: ").append(organization.getName()).append("\n");
            message.append("TOKEN: ").append(organization.getTuronToken()).append("\n");
            message.append("OMS ID: ").append(organization.getOmsId()).append("\n");
            log.error(message.toString());
        } else if (StringUtils.isNotEmpty(orderId)) {
            try {
                final String extension = orderProduct.getProduct().getType().getProductGroup().getExtension();
                final boolean isBock = Optional.ofNullable(orderProduct.getCisType()).orElse(CisType.UNIT).equals(CisType.GROUP);
                final String barcode = Utils.fillProductBarcode(isBock ? orderProduct.getProduct().getParentBarcode() : orderProduct.getProduct().getBarcode());
                final CheckOrderStatusResponse responseDTO = turonClient.checkStatus(extension, orderId, barcode, organization.getTuronToken(), organization.getOmsId());
                if (responseDTO != null && responseDTO.getBufferStatus() != null) {
                    orderProduct.setBufferStatus(responseDTO.getBufferStatus());
                    orderProduct.setErrorMessage(responseDTO.getMessage());
                } else {
                    orderProduct.setBufferStatus(BufferStatus.CLOSED);
                }
                orderProductRepository.save(orderProduct);
            } catch (Exception e) {
                log.error("Something went wrong", e);
            }
        }
    }

    public Set<String> readFromFile(MultipartFile file) throws IOException, CsvValidationException {
        Set<String> codes = new LinkedHashSet<>();
        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
        String[] values;
        while ((values = csvReader.readNext()) != null) {
            codes.add(values[0]);
        }
        return codes;
    }

    public Collection<OrderProductStatsDTO> generateReport(OrderFilter filter) {
        Map<String, OrderProductStatsDTO> statsDTOMap = new LinkedHashMap<>();
        boolean hasNext = true;
        int pageNumber = 0;
        while (hasNext) {
            filter.setPage(pageNumber++);
            Page<OrderProduct> allByFilter = orderProductRepository.findAllByFilter(filter);
            for (OrderProduct orderProduct : allByFilter) {
                final String productId = orderProduct.getProductId();
                OrderProductStatsDTO orderProductStatsDTO = Optional.ofNullable(statsDTOMap.get(productId)).orElse(new OrderProductStatsDTO());
                List<String> partyIds = partyRepository.getPartyIdsByOrderProductId(orderProduct.getId());
                Long unused = markRepository.countAllByPartyIdInAndUsedIsFalse(partyIds);
                orderProductStatsDTO.setUnusedQuantity(Optional.ofNullable(orderProductStatsDTO.getUnusedQuantity()).orElse(0L) + unused);
                orderProductStatsDTO.setQuantity(Optional.ofNullable(orderProductStatsDTO.getQuantity()).orElse(0) + orderProduct.getQuantity());
                if (orderProductStatsDTO.getProductId() == null) {
                    orderProductStatsDTO.setProductId(productId);
                    orderProductStatsDTO.setProductName(orderProduct.getProduct().getVisibleName());
                }
                if (unused > 0) {
                    orderProductStatsDTO.getParties().addAll(partyIds);
                }
                orderProductStatsDTO.setUsedQuantity(orderProductStatsDTO.getQuantity() - orderProductStatsDTO.getUnusedQuantity());
                statsDTOMap.put(productId, orderProductStatsDTO);
            }
            hasNext = allByFilter.hasNext();
        }
        return statsDTOMap.values();
    }
}

