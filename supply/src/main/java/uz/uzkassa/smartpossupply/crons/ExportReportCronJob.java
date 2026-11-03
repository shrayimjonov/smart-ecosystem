package uz.uzkassa.smartpossupply.crons;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.repository.ExportReportRepository;
import uz.uzkassa.smartpossupply.service.FileUploadService;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 30.01.2023 17:23
 */
@Component
@Profile("prod")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ExportReportCronJob {

    ExportReportRepository exportReportRepository;
    FileUploadService fileUploadService;

    @Scheduled(cron = "0 0 0 ? * *") // every day at 12:00
    public void delete() {
        exportReportRepository.findAllByExpireDateBefore(LocalDateTime.now().with(LocalTime.MAX)).forEach(report -> {
            FileUpload attachment = report.getFile();
            if (attachment != null) {
                report.setFile(null);
                report.setFileId(null);
                report.setDeleted(true);
                exportReportRepository.save(report);
                fileUploadService.delete(attachment);
            }
        });
    }
}
