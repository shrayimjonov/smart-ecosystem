package uz.uzkassa.smartpos.service.fashion;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductCompositeId;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionRegister;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.domain.postgres.receipt.ReceiptDetail;
import uz.uzkassa.smartpos.dto.analytics.RegisterStatsDTO;
import uz.uzkassa.smartpos.dto.fashion.ColorGroupItemsRegisterDto;
import uz.uzkassa.smartpos.dto.fashion.FashionBranchColorGroupRegisterDto;
import uz.uzkassa.smartpos.dto.fashion.FashionItemRegisterDto;
import uz.uzkassa.smartpos.dto.fashion.FashionRegisterCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionColorGroupRegisterDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionRegisterDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionReportDto;
import uz.uzkassa.smartpos.filter.FashionRegisterFilter;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductItemRepository;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionRegisterRepository;
import uz.uzkassa.smartpos.repository.postgres.receipt.ReceiptRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.service.SmartposService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FashionRegisterService extends SmartposService {
    private final FashionRegisterRepository repository;
    private final ReceiptRepository receiptRepository;
    private final ProductItemRepository productItemRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final FashionBalanceHistoryService fashionBalanceHistoryService;
    private final FashionRegisterRepository fashionRegisterRepository;

    @Transactional
    public void save(FashionRegisterCreateDto dto) {
        FashionRegister fashionRegister;
        if (dto.getProductItemId() != null) {
            fashionRegister = repository.findByBranchIdAndProductIdAndCityIdAndProductItemId(dto.getBranchId(), dto.getProductId(), dto.getCityId(), dto.getProductItemId())
                    .orElseGet(FashionRegister::new);
        } else {
            fashionRegister = repository.findByBranchIdAndProductIdAndCityIdAndProductItemIdIsNull(dto.getBranchId(), dto.getProductId(), dto.getCityId())
                    .orElseGet(FashionRegister::new);
        }
        BigDecimal qty = dto.getQty();
        if (!dto.getRegisterOperation().increase()) {
            qty = qty.multiply(BigDecimal.valueOf(-1));
        }
        fashionRegister.setCompanyId(dto.getCompanyId());
        fashionRegister.setBranchId(dto.getBranchId());
        fashionRegister.setProductId(dto.getProductId());
        fashionRegister.setCityId(dto.getCityId());
        fashionRegister.setProductItemId(dto.getProductItemId());
        fashionRegister.setUnitId(dto.getUnitId());
        fashionRegister.setCostPrice(dto.getCostPrice());
        fashionRegister.setSalesPrice(dto.getSalesPrice());
        fashionRegister.setQty(fashionRegister.getQty().add(qty));
        fashionRegister.setTotalCostPrice(dto.getCostPrice().multiply(dto.getQty()));
        fashionRegister.setTotalSalesPrice(dto.getSalesPrice().multiply(dto.getQty()));
        fashionRegister.setProfit(fashionRegister.getTotalSalesPrice().subtract(fashionRegister.getTotalCostPrice()));
        if (BigDecimal.ZERO.equals(fashionRegister.getQty())) {
            fashionRegister.setProfitPercentage(BigDecimal.ZERO);
        } else {
            fashionRegister.setProfitPercentage(fashionRegister.getProfit().divide(fashionRegister.getTotalCostPrice(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
        }
        repository.save(fashionRegister);
        fashionBalanceHistoryService.createHistory(dto);
    }

    public void sendReceiptWarehouseDataToKafka(Long id) {
        Receipt receipt = receiptRepository.findById(id).
                orElseThrow(notFound(localizationService.getMessage("receipt")));
        receipt.getReceiptDetails().stream().filter(ReceiptDetail::sendRegister).forEach(receiptDetail -> {
            try {
                FashionReportDto reportDto = new FashionReportDto();
                reportDto.setDateTime(receipt.getReceiptDateTime());
                reportDto.setProductId(receiptDetail.getProductId());
                reportDto.setProductName(receiptDetail.getProductName());
                reportDto.setBranchId(receipt.getBranchId());
                reportDto.setBranchName(receipt.getBranch().getName());
                reportDto.setCompanyId(receipt.getCompanyId());
                reportDto.setCompanyName(receipt.getCompany().getName());
                reportDto.setCityId(receipt.getBranch().getCityKeyId());
                if (receiptDetail.getExtraDetail() != null && receiptDetail.getExtraDetail().getProductItemId() != null) {
                    productItemRepository.findById(new ProductCompositeId(receiptDetail.getExtraDetail().getProductItemId(), receipt.getBranchId()))
                            .ifPresent(productItem -> {
                                reportDto.setColorGroupId(productItem.getColorGroupId());
                                if (productItem.getColorGroup() != null) {
                                    reportDto.setColorCode(productItem.getColorGroup().getColorCode());
                                    reportDto.setColorName(productItem.getColorGroup().getColorName());
                                    reportDto.setPrintName(productItem.getColorGroup().getPrintName());
                                }
                                reportDto.setItemId(productItem.getGenericId());
                                reportDto.setSizeCode(productItem.getSizeCode());
                                reportDto.setSizeValue(productItem.getSizeValue());
                            });
                }
                reportDto.setCategoryId(receiptDetail.getCategoryId());
                reportDto.setCategoryName(receiptDetail.getCategoryName());
                reportDto.setUnitId(receiptDetail.getUnitId());
                reportDto.setUnitName(receiptDetail.getUnitName());
                reportDto.setSalesPrice(receiptDetail.getPrice());
                reportDto.setQty(receiptDetail.getQty());
                reportDto.setTotalDiscount(receiptDetail.getDiscount());
                reportDto.setTotalVat(receiptDetail.getNds());
                reportDto.setTotalSumma(receiptDetail.getAmount());
                reportDto.setReceiptId(receipt.getId());
                reportDto.setStatus(receipt.getStatus());
                reportDto.setUserId(receipt.getCreatedById());
                if (receipt.getCreatedBy() != null) {
                    reportDto.setUserFullName(receipt.getCreatedBy().getFullNameStr());
                }
                kafkaTemplate.send("fashion-report", objectMapper.writeValueAsString(reportDto));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }

    public RegisterStatsDTO getStats(FashionRegisterFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return repository.getStats(filter);
    }

    public Page<FashionRegisterDto> findAllByFilter(FashionRegisterFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return repository.findAllFashionRegister(filter);
    }

    public ByteArrayResource findAllToExcel(FashionRegisterFilter filter) {
        filter.setPage(0);
        filter.setSize(10000);
        Page<FashionRegisterDto> page = findAllByFilter(filter);
        try (var workbook = new XSSFWorkbook(); var outputStream = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet();
            var boldFont = workbook.createFont();
            boldFont.setBold(true);
            var headerRow = sheet.createRow(0);
            var headers = new String[]{"Продукция", "Кол-во", "Ед. изм-я", "Себестоимость, UZS шт", "Себестоимость, UZS Общая", "Продажная цена, UZS шт", "Продажная цена, UZS Общая", "Потенциальная прибыль, UZS", "Маржа, %"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
                headerRow.getCell(i).getCellStyle().setFont(boldFont);
            }
            for (int i = 0; i < page.getContent().size(); i++) {
                var row = sheet.createRow(i + 1);
                var register = page.getContent().get(i);
                row.createCell(0).setCellValue(register.getProductName());
                row.createCell(1).setCellValue(register.getQty().toString());
                row.createCell(2).setCellValue(register.getUnit().getName());
                row.createCell(3).setCellValue(register.getCostPrice().toString());
                row.createCell(4).setCellValue(register.getTotalCostPrice().toString());
                row.createCell(5).setCellValue(register.getSalesPrice().toString());
                row.createCell(6).setCellValue(register.getTotalSalesPrice().toString());
                row.createCell(7).setCellValue((register.getTotalSalesPrice().subtract(register.getTotalCostPrice())).toString());
                row.createCell(8).setCellValue(register.getProfitPercentage().toString());
            }
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<FashionColorGroupRegisterDto> colorGroupRegisters(FashionRegisterFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return repository.colorGroupRegisters(filter);
    }

    public Page<FashionBranchColorGroupRegisterDto> branchColorGroupRegisters(FashionRegisterFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return repository.branchColorGroupRegisters(filter);
    }

    public List<ColorGroupItemsRegisterDto> colorGroupItemsRegisters(FashionRegisterFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        List<FashionRegister> fashionRegisters = repository.colorGroupItemsRegisters(filter);
        Map<Long, ColorGroupItemsRegisterDto> map = new HashMap<>();
        fashionRegisters.forEach(register -> {
            if (register.getProductItem() != null && register.getProductItem().getColorGroup() != null) {
                map.putIfAbsent(register.getProductItem().getColorGroup().getGenericId(), new ColorGroupItemsRegisterDto(register));
                ColorGroupItemsRegisterDto registerDto = map.get(register.getProductItem().getColorGroup().getGenericId());
                registerDto.getItems().add(new FashionItemRegisterDto(register));
            }
        });
        return new ArrayList<>(map.values());
    }

    public BigDecimal getQty(Long branchId, Long productItemId) {
        return fashionRegisterRepository.getQty(branchId, productItemId);
    }


    public BigDecimal getQtyByProduct(Long branchId, Long productId) {
        return fashionRegisterRepository.getQtyByProduct(branchId, productId);
    }
}
