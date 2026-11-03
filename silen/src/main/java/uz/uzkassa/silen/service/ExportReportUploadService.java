package uz.uzkassa.silen.service;

import io.minio.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.config.MinioConfiguration;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.exceptions.EntityNotFoundException;
import uz.uzkassa.silen.repository.pg.ExportReportRepository;
import uz.uzkassa.silen.security.SecurityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Transactional
@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ExportReportUploadService {
    MinioClient minioClient;
    ExportReportRepository exportReportRepository;
    MinioConfiguration minioConfiguration;

    public String fileUpload(MultipartFile file, String fileName, String exportReportId) {
        try {
            return uploadToMinio(file.getBytes(), fileName, exportReportId, file.getContentType());
        } catch (Exception e) {
            log.error("Close uploaded file error: {}", e.getMessage());
        }
        return null;
    }

    private String uploadToMinio(byte[] file, String fileName, String exportReportId, String contentType) {
        String filename = encodeFileName(fileName);
        String objectName = this.getObjectName(fileName, exportReportId, false);
        log.info("FILENAME: {} -------------------------------------", filename);
        log.info("objectName: {} -------------------------------------", objectName);
        log.info("contentType: {} -------------------------------------", contentType);
        try (ByteArrayInputStream stream = new ByteArrayInputStream(file)) {
            this.uploadWithPutObject(
                PutObjectArgs
                    .builder()
                    .bucket(minioConfiguration.getBucket())
                    .object(objectName)
                    .stream(stream, file.length, -1)
                    .contentType(contentType)
                    .build()
            );
            return String.format("%s/%s", minioConfiguration.getBucket(), objectName);
        } catch (Exception e) {
            log.error("Close uploaded file error: {}", e.getMessage());
        }
        return null;
    }

    void uploadWithPutObject(PutObjectArgs objectArgs) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(objectArgs.bucket()).build())) {
                throw new BadRequestException("Bucket does not exist");
            }
            Optional.ofNullable(this.minioClient.putObject(objectArgs)).map(ObjectWriteResponse::etag);
        } catch (Exception e) {
            log.error("Error upload file: {}", e.getMessage());
            throw new BadRequestException("Error upload file");
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> download(Long id) {
        ExportReport exportReport = exportReportRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(""));
        return this.downloadById(exportReport.getPath());
    }

    public ResponseEntity<?> download1c(Long id, HttpServletResponse response) {
        ExportReport exportReport = exportReportRepository.findFirstByOrganizationIdAndId(SecurityUtils.getCurrentOrganizationId(), id)
                .orElseThrow(() -> new EntityNotFoundException(""));

        InputStream inputStream = this.getObject(exportReport.getPath());
        if (inputStream == null) {
            throw new BadRequestException("Object is not found", "Object is not found");
        }
        try (OutputStream outputStream = response.getOutputStream()) {
            log.info(" mino path {}", id);
            outputStream.write(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            log.error("IOException e mino ", e);
            throw new RuntimeException(e);
        }
        return null;
    }

    public ResponseEntity<?> downloadById(String id) {
        InputStream inputStream = this.getObject(id);
        if (inputStream == null) {
            throw new BadRequestException("Object is not found", "Object is not found");
        }
        try {
            log.info(" mino path {}", id);
            return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", id))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            log.error("IOException e mino ", e);
            throw new RuntimeException(e);
        }
    }

    public GetObjectResponse getObject(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        try {
            return this.minioClient.getObject(
                GetObjectArgs
                    .builder()
                    .bucket(minioConfiguration.getBucket())
                    .object(path.replaceFirst(minioConfiguration.getBucket() + "/", ""))
                    .build()
            );
        } catch (Exception e) {
            log.error("Error download file from minio: {}", e.getMessage());
        }
        return null;
    }

    public String getObjectName(String fileName, String reportId, boolean isPublic) {
        String correctedName = encodeFileName(fileName);
        String filename = FilenameUtils.getBaseName(correctedName);
        String extension = FilenameUtils.getExtension(fileName);
        if (isPublic) {
            filename = String.format("public/%s", filename);
        }
        return filename.concat("-").concat(reportId).concat(StringUtils.isEmpty(extension) ? "" : '.' + extension);
    }


    public String encodeFileName(String originalFilename) {
        String fileName = null;
        try {
            fileName = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8);
            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return fileName;
    }


}

