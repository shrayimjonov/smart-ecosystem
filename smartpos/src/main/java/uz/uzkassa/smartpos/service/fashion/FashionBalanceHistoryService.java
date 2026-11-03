package uz.uzkassa.smartpos.service.fashion;

import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.fashion.FashionBalanceHistory;
import uz.uzkassa.smartpos.dto.fashion.FashionRegisterCreateDto;
import uz.uzkassa.smartpos.dto.fashion.balance.FashionBalanceHistoryDto;
import uz.uzkassa.smartpos.dto.fashion.balance.FashionBalanceHistoryStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.balance.FashionColorGroupHistoryDto;
import uz.uzkassa.smartpos.filter.FashionBalanceHistoryFilter;
import uz.uzkassa.smartpos.repository.postgres.fashion.FashionBalanceHistoryRepository;
import uz.uzkassa.smartpos.service.SmartposService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class FashionBalanceHistoryService extends SmartposService {
    private final FashionBalanceHistoryRepository fashionBalanceHistoryRepository;

    @Transactional
    public void createHistory(FashionRegisterCreateDto payload) {
        final LocalDate today = LocalDate.now();
        Optional<FashionBalanceHistory> oldBalanceHistory;
        if (payload.getProductItemId() != null) {
            oldBalanceHistory = fashionBalanceHistoryRepository.findFirstByBranchIdAndProductItemIdOrderByIdDesc(payload.getBranchId(), payload.getProductItemId());
        } else {
            oldBalanceHistory = fashionBalanceHistoryRepository.findFirstByBranchIdAndProductIdOrderByIdDesc(payload.getBranchId(), payload.getProductId());
        }
        FashionBalanceHistory newBalanceHistory;
        if (oldBalanceHistory.isEmpty() || !oldBalanceHistory.get().getBalanceDate().isEqual(today)) {
            newBalanceHistory = new FashionBalanceHistory();
            newBalanceHistory.setBalanceDate(today);
            newBalanceHistory.setCompanyId(payload.getCompanyId());
            newBalanceHistory.setBranchId(payload.getBranchId());
            newBalanceHistory.setCityId(payload.getCityId());
            newBalanceHistory.setProductId(payload.getProductId());
            newBalanceHistory.setProductItemId(payload.getProductItemId());
            oldBalanceHistory.ifPresent(old -> {
                newBalanceHistory.setBalanceStart(old.getBalanceEnd());
                newBalanceHistory.setBalanceEnd(old.getBalanceEnd());
            });
        } else {
            newBalanceHistory = oldBalanceHistory.get();
        }
        if (payload.getRegisterOperation().increase()) {
            newBalanceHistory.setBalanceEnd(newBalanceHistory.getBalanceEnd().add(payload.getQty()));
        } else {
            newBalanceHistory.setBalanceEnd(newBalanceHistory.getBalanceEnd().subtract(payload.getQty()));
        }
        switch (payload.getRegisterOperation()) {
            case INCOME:
                newBalanceHistory.setIncome(newBalanceHistory.getIncome().add(payload.getQty()));
                break;
            case SALE:
                newBalanceHistory.setSale(newBalanceHistory.getSale().add(payload.getQty()));
                break;
            case BACK:
                newBalanceHistory.setBack(newBalanceHistory.getBack().add(payload.getQty()));
                break;
            case TRANSFER_IN:
                newBalanceHistory.setTransferIn(newBalanceHistory.getTransferIn().add(payload.getQty()));
                break;
            case TRANSFER_OUT:
                newBalanceHistory.setTransferOut(newBalanceHistory.getTransferOut().add(payload.getQty()));
                break;
            case STOCK_ADJUSTMENT:
                newBalanceHistory.setStockAdjustment(newBalanceHistory.getStockAdjustment().add(payload.getQty()));
                break;
        }
        fashionBalanceHistoryRepository.save(newBalanceHistory);
    }

    public Page<FashionBalanceHistoryDto> findAllByFilter(FashionBalanceHistoryFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return fashionBalanceHistoryRepository.findAllByFilter(filter);
    }

    public ByteArrayResource findAllByFilterExcel(FashionBalanceHistoryFilter filter) {
        filter.setPage(0);
        filter.setSize(10000);
        Page<FashionBalanceHistoryDto> page = findAllByFilter(filter);
        try (var workbook = new XSSFWorkbook(); var out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet();
            var boldFont = workbook.createFont();
            boldFont.setBold(true);
            var headerRow = sheet.createRow(0);
            var headers = new String[]{"Продукция","Дата", "Количество начало дня " + filter.getFrom(), "Приход", "Возврат", "Продажа", "Списание", "Трансфер в", "Трансфер из", "Количество конец дня " + filter.getTo()};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
                headerRow.getCell(i).getCellStyle().setFont(boldFont);
            }
            for (int i = 0; i < page.getContent().size(); i++) {
                var row = sheet.createRow(i + 1);
                var item = page.getContent().get(i);
                row.createCell(0).setCellValue(item.getProduct().getName());
                row.createCell(1).setCellValue(item.getBalanceDate().toString());
                row.createCell(2).setCellValue(item.getBalanceStart().doubleValue());
                row.createCell(3).setCellValue(item.getIncome().doubleValue());
                row.createCell(4).setCellValue(item.getBack().doubleValue());
                row.createCell(5).setCellValue(item.getSale().doubleValue());
                row.createCell(6).setCellValue(item.getStockAdjustment().doubleValue());
                row.createCell(7).setCellValue(item.getTransferIn().doubleValue());
                row.createCell(8).setCellValue(item.getTransferOut().doubleValue());
                row.createCell(9).setCellValue(item.getBalanceEnd().doubleValue());
            }
            IntStream.range(0, headers.length).forEach(sheet::autoSizeColumn);
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FashionBalanceHistoryStatisticsDto getStatisticsByFilter(FashionBalanceHistoryFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return fashionBalanceHistoryRepository.getStatisticsByFilter(filter);
    }

    public List<FashionColorGroupHistoryDto> findAllColorGroupByFilter(FashionBalanceHistoryFilter filter) {
        if (filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return fashionBalanceHistoryRepository.findAllColorGroupByFilter(filter);
    }
}
