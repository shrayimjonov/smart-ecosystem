package uz.uzkassa.silen.export;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.BatchAggregation;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.pg.BatchAggregationRepository;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonBatchAggregationExportHandler implements IExportHandler {

    private final AggregationRepository aggregationRepository;
    private final ObjectMapper objectMapper;
    private final BatchAggregationRepository batchAggregationRepository;

    protected void writeContent(AggregationFilter filter, JsonGenerator jsonGenerator) {
        boolean hasNext;
        int pageNumber = 0;
        filter.setSize(1000);
        do {
            filter.setPage(pageNumber++);
            Page<Aggregation> allByFilter = aggregationRepository.findAllByFilter(filter);
            allByFilter.stream()
                    .map(Aggregation::getCode)
                    .forEach(code -> {
                        try {
                            jsonGenerator.writeString(code);
                        } catch (IOException e) {
                            log.info("Code: {}, error occured on time writing", e.getMessage());
                        }
                    });
            log.info("Page number: {}, size {}", pageNumber, allByFilter.getTotalElements());
            hasNext = allByFilter.hasNext();
        } while (hasNext);
    }

    @Override
    public void writeToStream(ExportReport exportReport, OutputStream outputStream) {
        AggregationFilter filter;
        try {
            filter = objectMapper.readValue(exportReport.getFilters(), AggregationFilter.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        BatchAggregation batchAggregation = batchAggregationRepository.findById(exportReport.getBatchId()).orElse(null);
        // Create the JsonGenerator to write to our OutputStream
        JsonFactory factory = new JsonFactory();
        try (JsonGenerator generator = factory.createGenerator(outputStream, JsonEncoding.UTF8)) {

            generator.writeStartObject();
            generator.writeFieldName("meta");
            generator.writeStartObject("meta");
            generator.writeStringField("number", batchAggregation.getNumber());
            generator.writeNumberField("quantity", batchAggregation.getQuantity());
            generator.writeStringField("packageType", PackageType.fromCode(batchAggregation.getUnit()).getNameRu());
            generator.writeStringField("createdDate", batchAggregation.getCreatedDate().toString());
            generator.writeEndObject();
            generator.writeFieldName("codes");
            generator.writeStartArray("codes");

            writeContent(filter, generator);

            generator.writeEndArray();
            generator.writeEndObject();

            generator.flush();  // Flush buffered JSON to the output stream
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExportReportType getReportType() {
        return ExportReportType.BATCH_JSON_CONTENT;
    }
}
