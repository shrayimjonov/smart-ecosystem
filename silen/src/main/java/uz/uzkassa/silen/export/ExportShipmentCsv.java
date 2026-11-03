package uz.uzkassa.silen.export;

import com.opencsv.CSVWriter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.domain.ShipmentItem;
import uz.uzkassa.silen.domain.mongo.Block;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.domain.mongo.ShipmentMark;
import uz.uzkassa.silen.dto.ShipmentExportDTO;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.repository.mongo.BlockRepository;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.mongo.ShipmentMarkRepository;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.pg.ShipmentItemRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Getter
@RequiredArgsConstructor
public class ExportShipmentCsv extends BaseCsvExport implements IExportHandler {
    private final ShipmentMarkRepository shipmentMarkRepository;
    private final AggregationRepository aggregationRepository;
    private final BlockRepository blockRepository;
    private final MarkRepository markRepository;
    private final ShipmentItemRepository shipmentItemRepository;

    @Override
    public ExportReportType getReportType() {
        return ExportReportType.CSV_SHIPMENT;
    }

    @Override
    protected void writeHeader(ExportReport exportReport, CSVWriter csvWriter) {

    }

    @Override
    protected void writeContent(ExportReport exportReport, CSVWriter csvWriter) {
        final ShipmentExportDTO shipmentExportDTO = readFilter(exportReport.getFilters(), ShipmentExportDTO.class);
        List<Long> list = new LinkedList<>(shipmentExportDTO.getShipmentItems());
        List<ShipmentItem> allByIdIn = shipmentItemRepository.findAllByIdIn(list);

        for (ShipmentItem item : allByIdIn) {
            final String productName = item.getProduct() != null ? Optional.ofNullable(item.getProduct().getVisibleName()).orElse(item.getProduct().getName()) + "(" + item.getProduct().getBarcode() + ")" : item.getProductId();
            csvWriter.writeNext(new String[]{productName}, false);

            final List<ShipmentMark> allByShipmentItemId = shipmentMarkRepository.findAllByShipmentItemId(item.getId());
            if (CollectionUtils.isEmpty(allByShipmentItemId)) continue;

            final Map<PackageType, List<ShipmentMark>> groupByUnit = allByShipmentItemId.stream().collect(Collectors.groupingBy(shipmentMark -> shipmentMark.getUnit() != null ? PackageType.fromCode(shipmentMark.getUnit()) : PackageType.BOTTLE));
            if (MapUtils.isEmpty(groupByUnit)) continue;

            if (groupByUnit.containsKey(PackageType.PALLET)) {
                List<ShipmentMark> shipmentMarks = groupByUnit.get(PackageType.PALLET);
                if (CollectionUtils.isEmpty(shipmentMarks)) continue;

                List<String> palletCodes = shipmentMarks.stream().map(ShipmentMark::getCode).toList();
                // collect pallet children
                Map<String, List<String>> palletChildren = new HashMap<>();
                List<Aggregation> allByParentCodeIn = aggregationRepository.findAllByParentCodeIn(palletCodes);
                if (CollectionUtils.isNotEmpty(allByParentCodeIn)) {
                    for (Aggregation aggregation : allByParentCodeIn) {
                        if (!palletChildren.containsKey(aggregation.getParentCode())) {
                            palletChildren.put(aggregation.getParentCode(), new ArrayList<>());
                        }
                        palletChildren.get(aggregation.getParentCode()).add(aggregation.getCode());
                    }

                } else {
                    List<Block> blockList = blockRepository.findAllByParentCodeIn(palletCodes);
                    if (CollectionUtils.isNotEmpty(blockList)) {
                        for (Block block : blockList) {
                            if (!palletChildren.containsKey(block.getParentCode())) {
                                palletChildren.putIfAbsent(block.getParentCode(), new ArrayList<>());
                            }
                            palletChildren.get(block.getParentCode()).add(block.getCode());
                        }
                    }
                }

                for (String palletCode : palletCodes) {
                    csvWriter.writeNext(new String[]{palletCode}, false);
                    if (palletChildren.containsKey(palletCode)) {
                        List<String> children = palletChildren.get(palletCode);
                        if (CollectionUtils.isNotEmpty(children)) {
                            for (String child : children) {
                                csvWriter.writeNext(new String[]{"\t", child}, false);
                                List<Mark> marks = markRepository.findAllByParentCode(child);
                                if (CollectionUtils.isNotEmpty(marks)) {
                                    for (Mark mark : marks) {
                                        csvWriter.writeNext(new String[]{"\t", "\t", mark.getCode()}, false);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            byUnit(PackageType.BOX, groupByUnit, csvWriter);
            byUnit(PackageType.BLOCK, groupByUnit, csvWriter);

            if (groupByUnit.containsKey(PackageType.BOTTLE)) {
                List<ShipmentMark> shipmentMarks = groupByUnit.get(PackageType.BOTTLE);
                if (CollectionUtils.isNotEmpty(shipmentMarks)) {
                    for (ShipmentMark mark : shipmentMarks) {
                        csvWriter.writeNext(new String[]{mark.getCode()}, false);
                    }
                }
            }
        }
    }

    private void byUnit(PackageType packageType, Map<PackageType, List<ShipmentMark>> groupByUnit, CSVWriter csvWriter) {
        if (groupByUnit.containsKey(packageType)) {
            List<ShipmentMark> shipmentMarks = groupByUnit.get(packageType);
            if (CollectionUtils.isNotEmpty(shipmentMarks)) {
                for (ShipmentMark parent : shipmentMarks) {
                    csvWriter.writeNext(new String[]{parent.getCode()}, false);
                    List<Mark> marks = markRepository.findAllByParentCode(parent.getCode());
                    for (Mark mark : marks) {
                        csvWriter.writeNext(new String[]{"\t", mark.getCode()}, false);
                    }
                }
            }
        }
    }
}
