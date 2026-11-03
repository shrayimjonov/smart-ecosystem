package uz.uzkassa.smartpossupply.service.handlers.excel;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.AgentPerformanceReportFilter;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.handlers.excel.export.AgentPerformanceReportExcelHandler;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AgentPerformanceReportStrategy extends AbstractExportStrategy {

    AgentPerformanceReportExcelHandler excelHandler;

    public AgentPerformanceReportStrategy(AgentPerformanceReportExcelHandler excelHandler) {
        this.excelHandler = excelHandler;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.AGENT_PERFORMANCE_REPORT;
    }

    @Override
    public CustomMultipartFile generateReport(Object filter, FileType fileType) {
        AgentPerformanceReportFilter agentPerformanceReportExcelHandler = objectMapper.convertValue(filter, AgentPerformanceReportFilter.class);
        return switch (fileType) {
            case EXCEL -> new CustomMultipartFile(
                    excelHandler.generate(agentPerformanceReportExcelHandler),
                    excelHandler.getFileName(agentPerformanceReportExcelHandler),
                    CONTENT_TYPE_FOR_EXCEL
            );
            case CSV -> null;
        };
    }
}
