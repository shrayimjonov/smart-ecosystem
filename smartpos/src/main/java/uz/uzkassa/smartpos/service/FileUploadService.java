package uz.uzkassa.smartpos.service;

import io.minio.*;
import io.minio.errors.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.enums.EntityType;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.integration.apay.FileDTO;
import uz.uzkassa.smartpos.repository.postgres.FileUploadRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
@Slf4j
@AllArgsConstructor
public class FileUploadService extends BaseService {

    private MinioClient minioClient;
    private FileUploadRepository fileUploadRepository;
    private ApplicationProperties applicationProperties;


    //    @Scheduled(cron = "10 10 16 * * *")
    public void removeFiles() {
        List<FileUpload> files = fileUploadRepository.findAllByEntityTypeIsNull();
        fileUploadRepository.deleteAll(files);
        for (FileUpload fileUpload : files) {
            removeObject(fileUpload);
        }
    }

    public void removeObject(FileUpload fileUpload) {
        try {
            fileUploadRepository.delete(fileUpload);
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(applicationProperties.getMinio().getApplication())
                    .object(fileUpload.getObjectName())
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e.getMessage(), e);
        }
    }

    public FileUpload fileUpload(MultipartFile file, Long entityId, EntityType entityType, boolean isPublic) {
        this.validateFile(file);

        FileDTO fileDTO;
        try {
            fileDTO = uploadToStorageServer(file.getBytes(), file.getOriginalFilename(), file.getContentType(), entityId, entityType, isPublic);
        } catch (Exception e) {
            log.error("An unaccepted error has occurred while uploading file: ", e);
            throw new BadRequestException("An unaccepted error has occurred while uploading file");
        }
        FileUpload fileUpload = wrap(fileDTO, entityId, entityType);
        fileUploadRepository.save(fileUpload);
        return fileUpload;
    }

    private FileUpload wrap(FileDTO fileDTO, Long entityId, EntityType entityType) {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setUrl(fileDTO.getUrl());
        fileUpload.setObjectName(fileDTO.getObjectName());
        fileUpload.setEntityId(entityId);
        fileUpload.setEntityType(entityType);
        return fileUpload;
    }


    public FileDTO uploadToStorageServer(byte[] file, String fileName, String contentType, Long entityId, EntityType entityType, boolean isPublic) {
        String objectName = this.getObjectName(fileName, isPublic);
        try (ByteArrayInputStream stream = new ByteArrayInputStream(file)) {

            log.info("FILENAME: {} -------------------------------------", fileName);
            log.info("objectName: {} -------------------------------------", objectName);
            log.info("contentType: {} -------------------------------------", contentType);
            log.info("entityId: {} -------------------------------------", entityId);
            log.info("entityType: {} -------------------------------------", entityType);
            this.uploadWithPutObject(
                    PutObjectArgs
                            .builder()
                            .bucket(applicationProperties.getMinio().getApplication())
                            .object(objectName)
                            .stream(stream, file.length, -1)
//                            .contentType(contentType)
                            .build()
            );

            FileDTO uploadDTO = new FileDTO();
            uploadDTO.setUrl(StringUtils.join(applicationProperties.getMinio().getHost(), "/", applicationProperties.getMinio().getApplication(), "/", objectName));
            uploadDTO.setObjectName(objectName);
            log.info("Image uploaded successfully");
            return uploadDTO;
        } catch (Exception e) {
            log.error("Close uploaded file error: {}", e.getMessage());
            throw new RuntimeException("File upload error", e);
        }
    }

    private void uploadWithPutObject(PutObjectArgs objectArgs) {
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

    private String getObjectName(final String fileName, final boolean isPublic) {
        final UUID uuid = UUID.randomUUID();
        final String extension = FilenameUtils.getExtension(fileName);
        return StringUtils.join(isPublic ? "public/" : "", uuid.toString(), StringUtils.isEmpty(extension) ? "" : '.' + extension);
    }

    protected void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(localizationService.getMessage("required", localizationService.getMessage("file")));
        }

        if (StringUtils.isEmpty(file.getOriginalFilename())) {
            throw new BadRequestException(localizationService.getMessage("required", localizationService.getMessage("filename")));
        }
    }

    public void deleteFile(Long id) {
        FileUpload fileUpload = fileUploadRepository.findById(id).orElseThrow(notFound("File"));
        fileUploadRepository.delete(fileUpload);
    }
}
