package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
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
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.dto.file.FileUploadDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileSizeType;
import uz.uzkassa.smartpossupply.repository.FileUploadRepository;
import uz.uzkassa.smartpossupply.utils.FileUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileUploadService extends BaseService {

    FileUploadRepository fileUploadRepository;
    MinioClient minioClient;

    @Transactional
    public FileUpload fileUpload(MultipartFile file) {
        return this.fileUpload(file, true);
    }

    @Transactional
    public FileUpload fileUpload(MultipartFile file, boolean isPublic) {
        return this.fileUpload(file, null, null, isPublic);
    }

    @Transactional
    public FileUpload fileUpload(MultipartFile file, Long entityId, EntityTypeEnum entityType, boolean isPublic) {
        this.validateFile(file);
        try {
            FileUploadDTO fileUploadDTO = uploadToMinio(file.getBytes(), file.getOriginalFilename(), file.getContentType(), isPublic);
            if (fileUploadDTO == null) {
                throw validationService.badRequestExceptionThrow("An unaccepted error has occurred while uploading file", "File upload error").get();
            }
            FileUpload fileUpload = wrap(file, entityId, entityType, fileUploadDTO, isPublic);
            fileUploadRepository.save(fileUpload);
            return fileUpload;
        } catch (Exception e) {
            log.error("An unaccepted error has occurred while uploading file: ", e);
            throw validationService.badRequestExceptionThrow("An unaccepted error has occurred while uploading file", e.getMessage()).get();
        }
    }

    @Transactional
    public List<FileDTO> fileUpload(List<MultipartFile> files, Long entityId, EntityTypeEnum entityType, boolean isPublic, boolean resize) {
        List<FileDTO> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            this.validateFile(file);
            try {
                FileUploadDTO fileUploadDTO = uploadToMinio(file.getBytes(), file.getOriginalFilename(), file.getContentType(), isPublic);
                if (fileUploadDTO == null) {
                    throw validationService.badRequestExceptionThrow("An unaccepted error has occurred while uploading file", "File upload error").get();
                }
                FileUpload fileUpload = wrap(file, entityId, entityType, fileUploadDTO, isPublic);
                fileUploadRepository.save(fileUpload);
                uploadedFiles.add(fileUpload.toFileDTO());
                if (resize) {
                    Map<FileSizeType, MultipartFile> resizedFiles = FileUtils.resizePhoto(file);
                    resizedFiles.forEach((fileSizeType, childFile) -> {
                        try {
                            FileUploadDTO childPhoto = uploadToMinio(childFile.getBytes(), childFile.getOriginalFilename(), childFile.getContentType(), isPublic);
                            if (childPhoto == null) {
                                throw validationService.badRequestExceptionThrow("An unaccepted error has occurred while uploading file", "File upload error").get();
                            }
                            childPhoto.setParentId(fileUpload.getId());
                            childPhoto.setFileSizeType(fileSizeType);
                            FileUpload childPhotoUpload = wrap(childFile, entityId, entityType, childPhoto, isPublic);
                            fileUploadRepository.save(childPhotoUpload);
                        } catch (IOException e) {
                            log.error("An unaccepted error has occurred while uploading file: ", e);
                        }
                    });
                }
            } catch (Exception e) {
                log.error("An unaccepted error has occurred while uploading file: ", e);
                throw validationService.badRequestExceptionThrow("An unaccepted error has occurred while uploading file", e.getMessage()).get();
            }
        }
        return uploadedFiles;
    }

    public ResponseEntity<?> download(Long id) {
        FileUpload fileUpload = fileUploadRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(FileUpload.class.getSimpleName(), "id", id));
        InputStream inputStream = this.getObject(fileUpload.getPath());
        if (inputStream == null) {
            throw validationService.notFoundExceptionThrow("Object is not found").get();
        }
        try {
            return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", fileUpload.getName()))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            log.error("Error download file from minio: {}", e.getMessage());
            throw new BadRequestException("An unaccepted error has occurred while downloading file", e.getMessage());
        }
    }

    private GetObjectResponse getObject(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        try {
            return this.minioClient.getObject(
                GetObjectArgs
                    .builder()
                    .bucket(applicationProperties.getMinioStorageConfig().getApplication())
                    .object(path.replaceFirst(applicationProperties.getMinioStorageConfig().getApplication() + "/", ""))
                    .build()
            );
        } catch (Exception e) {
            log.error("Error download file from minio: {}", e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        fileUploadRepository.findById(id).ifPresent(this::delete);
    }

    @Transactional
    public void deleteByIds(List<Long> ids) {
        ids.forEach(this::deleteById);
    }

    @Transactional
    public void delete(FileUpload fileUpload) {
        try {
            minioClient.removeObject(RemoveObjectArgs
                .builder()
                .bucket(applicationProperties.getMinioStorageConfig().getApplication())
                .object(fileUpload.getObjectName()).build());
            fileUpload.setDeleted(true);
            fileUploadRepository.save(fileUpload);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e.getMessage(), e);
        }
    }

    private FileUploadDTO uploadToMinio(byte[] file, String fileName, String contentType, boolean isPublic) {
        UUID uuid = UUID.randomUUID();
        String filename = encodeFileName(fileName);
        String objectName = this.getObjectName(fileName, uuid, isPublic);
        ByteArrayInputStream stream = new ByteArrayInputStream(file);
        log.info("FILENAME: {} -------------------------------------", filename);
        log.info("objectName: {} -------------------------------------", objectName);
        log.info("contentType: {} -------------------------------------", contentType);
        try {
            this.uploadWithPutObject(
                PutObjectArgs
                    .builder()
                    .bucket(applicationProperties.getMinioStorageConfig().getApplication())
                    .object(objectName)
                    .stream(stream, file.length, -1)
                    .contentType(contentType)
                    .build()
            );

            FileUploadDTO uploadDTO = new FileUploadDTO();
            uploadDTO.setFileId(uuid.toString());
            uploadDTO.setName(filename);
            uploadDTO.setObjectName(objectName);
            uploadDTO.setPath(String.format("%s/%s", applicationProperties.getMinioStorageConfig().getApplication(), objectName));
            if (isPublic) {
                uploadDTO.setUrl(applicationProperties.getMinioStorageConfig().getWithBaseUrl(uploadDTO.getPath()));
            }
            return uploadDTO;
        } catch (Exception e) {
            log.error("Close uploaded file error: {}", e.getMessage());
        } finally {
            try {
                stream.close();
            } catch (Exception e) {
                log.error("Close uploaded file error: {}", e.getMessage());
            }
        }
        return null;
    }

    private void uploadWithPutObject(PutObjectArgs objectArgs) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(objectArgs.bucket()).build())) {
                throw validationService.badRequestExceptionThrow("Bucket does not exist", "Bucket does not exist").get();
            }
            Optional.ofNullable(this.minioClient.putObject(objectArgs)).map(ObjectWriteResponse::etag);
        } catch (Exception e) {
            log.error("Error upload file: {}", e.getMessage());
            throw validationService.badRequestExceptionThrow("Error upload file", "Error upload file").get();
        }
    }

    private String getObjectName(String fileName, UUID uuid, boolean isPublic) {
        String correctedName = encodeFileName(fileName);
        String filename = FilenameUtils.getBaseName(correctedName);
        String extension = FilenameUtils.getExtension(fileName);
        if (isPublic) {
            filename = String.format("public/%s", filename);
        }
        return filename.concat("-").concat(uuid.toString()).concat(StringUtils.isEmpty(extension) ? "" : '.' + extension);
    }

    private String encodeFileName(String originalFilename) {
        String fileName = null;
        try {
            fileName = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8);
            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return fileName;
    }

    private FileUpload wrap(MultipartFile file, Long entityId, EntityTypeEnum entityType, FileUploadDTO fileUploadDTO, boolean isPublic) {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setContentType(file.getContentType());
        fileUpload.setSize(file.getSize());
        fileUpload.setFileId(fileUploadDTO.getFileId());
        fileUpload.setName(fileUploadDTO.getName());
        fileUpload.setObjectName(fileUploadDTO.getObjectName());
        fileUpload.setPath(fileUploadDTO.getPath());
        fileUpload.setUrl(fileUploadDTO.getUrl());
        fileUpload.setEntityId(entityId);
        fileUpload.setEntityType(entityType);
        fileUpload.setPublic(isPublic);
        fileUpload.setParentId(fileUploadDTO.getParentId());
        fileUpload.setFileSizeType(Optional.ofNullable(fileUploadDTO.getFileSizeType()).orElse(FileSizeType.ORIGINAL));
        return fileUpload;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.REQUIRED, new Object[]{localizationService.localize("file")}), "Требуется файл").get();
        }

        if (StringUtils.isEmpty(file.getOriginalFilename())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.REQUIRED, new Object[]{localizationService.localize(MessageConstants.NAME)}), "Укажите имя файла").get();
        }
    }
}
