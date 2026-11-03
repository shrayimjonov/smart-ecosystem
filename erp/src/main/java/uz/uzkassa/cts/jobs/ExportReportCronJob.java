package uz.uzkassa.cts.jobs;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.repository.ExportReportRepository;
import uz.uzkassa.cts.service.FileUploadService;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static lombok.AccessLevel.PRIVATE;


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
            FileUpload file = report.getFile();
            if (file != null) {
                report.setFile(null);
                report.setFileId(null);
                report.setDeleted(true);
                exportReportRepository.save(report);
                fileUploadService.remove(file);
            }
        });
    }
}
