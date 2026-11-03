package uz.uzkassa.silen.export;

import org.springframework.beans.factory.annotation.Autowired;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.service.ExportReportService;

import java.io.OutputStream;

public interface IExportHandler {

    /**
     * This method will receive export report requirements and by this requirements
     * will generate file which of {@link uz.uzkassa.silen.enumeration.FileType#CSV} or {@link uz.uzkassa.silen.enumeration.FileType#EXCEL}
     * <p>
     * !Note
     * For db queries method gives filtering info from {@link ExportReport#getFilters()}
     * which is converted string filter properties
     *
     * @param exportReport {@link ExportReport} report requirements
     * @param outputStream {@link OutputStream} stream for write
     */
    void writeToStream(ExportReport exportReport, OutputStream outputStream);


    /**
     * This method must override from child
     * !Note
     * This method must return unique inside {@link ExportReportService#exportHandlers}
     * which is stores all implementors
     *
     * @return {@link ExportReportType}
     */
    ExportReportType getReportType();

    /**
     * This method registers all implementation children to {@link ExportReportService} registrar.
     * this method is not required implement from children
     *
     * @param exportReportService {@link ExportReportService}
     */
    @Autowired
    default void registerMySelf(ExportReportService exportReportService) {
        exportReportService.registrar(getReportType(), this);
    }
}
