package uz.uzkassa.horeca.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.config.AmazonConfiguration;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.dto.file.PrivateFileDTO;
import uz.uzkassa.horeca.enums.EntityTypeEnum;
import uz.uzkassa.horeca.enums.FileType;
import uz.uzkassa.horeca.enums.FolderType;
import uz.uzkassa.horeca.enums.PhotoType;
import uz.uzkassa.horeca.repository.FileUploadRepository;
import uz.uzkassa.horeca.utils.SecurityUtils;
import uz.uzkassa.horeca.utils.WordUtils;
import uz.uzkassa.horeca.web.rest.errors.BadRequestException;

@Service
@Slf4j

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AmazonS3Service extends BaseService {

    FileUploadRepository fileUploadRepository;
    AmazonConfiguration amazonConfiguration;

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
           throw badRequestExceptionThrow(localizationService.localize("required", new Object[]{localizationService.localize("file")})).get();
        }

        if (StringUtils.isEmpty(file.getOriginalFilename())) {
            throw badRequestExceptionThrow(localizationService.localize("required", new Object[]{localizationService.localize("filename")})).get();
        }
    }

    public PrivateFileDTO privateUpload(MultipartFile file, Long entityId, EntityTypeEnum entityType, FileType fileType) {
        return getFile(this.upload(file, FolderType.PRIVATE, entityId, entityType, fileType));
    }

    public FileUpload publicUpload(MultipartFile file, Long entityId, EntityTypeEnum entityType, FileType fileType) {
        return this.upload(file, FolderType.PUBLIC, entityId, entityType, fileType);
    }

    public FileUpload uploadCompanyLogo(MultipartFile file, Long entityId) {
        return this.upload(file, FolderType.PUBLIC, entityId, EntityTypeEnum.COMPANY, FileType.COMPANY_LOGO);
    }

    public FileUpload uploadProductPhoto(MultipartFile file, Long entityId) {
        return this.upload(file, FolderType.PUBLIC, entityId, EntityTypeEnum.PRODUCT, FileType.PRODUCT_PHOTO);
    }

    public FileUpload uploadProductPhoto(MultipartFile file, Long entityId, PhotoType photoType) {
        return this.upload(file, FolderType.PUBLIC, entityId, EntityTypeEnum.PRODUCT, FileType.PRODUCT_PHOTO, photoType);
    }

    public FileUpload uploadCompanyBackgroundPhoto(MultipartFile file, Long entityId) {
        return this.upload(file, FolderType.PUBLIC, entityId, EntityTypeEnum.COMPANY, FileType.COMPANY_BACKGROUND_PHOTO);
    }

    public FileUpload uploadUserProfilePhoto(MultipartFile file, Long entityId) {
        return this.upload(file, FolderType.PUBLIC, entityId, EntityTypeEnum.USER, FileType.USER_PROFILE_PHOTO);
    }

    public FileUpload publicUpload(MultipartFile file) {
        return this.upload(file, FolderType.PUBLIC, null, null, null);
    }

    private FileUpload upload(MultipartFile file, FolderType folderType, Long entityId, EntityTypeEnum entityType, FileType fileType) {
        return this.upload(file, folderType, entityId, entityType, fileType, null);
    }

    private FileUpload upload(MultipartFile file, FolderType folderType, Long entityId, EntityTypeEnum entityType, FileType fileType, PhotoType photoType) {
        this.validateFile(file);
        try {
            CustomMultipartFile customMultipartFile = new CustomMultipartFile(file.getBytes(), file.getOriginalFilename().trim().replaceAll("[!?,',{,},/,”,“]", ""), file.getContentType());
            String key = generateKey();
            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileName(WordUtils.cyrillToLatin(customMultipartFile.getOriginalFilename()));
            fileUpload.setContentType(customMultipartFile.getContentType());
            fileUpload.setFileSize(customMultipartFile.getSize());
            fileUpload.setExtension(getFileExtension(customMultipartFile.getOriginalFilename()));
            fileUpload.setKey(key);
            fileUpload.setEntityId(entityId);
            fileUpload.setEntityType(entityType);
            fileUpload.setFileType(fileType);
            fileUpload.setFolderType(folderType);
            fileUpload.setPhotoType(photoType);
            fileUpload.setUrl(
                    this.uploadToAmazon(
                            folderType,
                            fileUpload.getFileName(),
                            key,
                            new ByteArrayInputStream(customMultipartFile.getBytes()),
                            customMultipartFile.getContentType()
                    )
            );
            fileUploadRepository.save(fileUpload);
            return fileUpload;
        } catch (IOException e) {
            log.error("Upload file to Amazon error:" + e.getMessage());
            throw badRequestExceptionThrow(localizationService.localize("unexpected.error.while.uploading.file" + e.getMessage())).get();
        }
    }

    private String uploadToAmazon(FolderType folderType, String fileName, String key, InputStream inputStream, String contentType) {
        String bucketName = FolderType.PUBLIC.equals(folderType)
                ? applicationProperties.getAmazonConfig().getPublicBucketName()
                : applicationProperties.getAmazonConfig().getPrivateBucketName();

        //Set content type
        ObjectMetadata objectMetadata = new ObjectMetadata();
        try {
            objectMetadata.setContentLength(inputStream.available());
        } catch (IOException ignored) {
        }
        objectMetadata.setExpirationTime(null);
        objectMetadata.setContentDisposition("attachment; filename=" + fileName);
        objectMetadata.setContentType(contentType);
        //Upload file
        amazonConfiguration.amazonS3().putObject(new PutObjectRequest(bucketName, key, inputStream, objectMetadata));

        amazonConfiguration
                .amazonS3()
                .setObjectAcl(
                        bucketName,
                        key,
                        FolderType.PUBLIC.equals(folderType) ? CannedAccessControlList.PublicRead : CannedAccessControlList.Private
                );
        return amazonConfiguration.amazonS3().getUrl(bucketName, key).toString();
    }

    private String generateKey() {
        return SecurityUtils.md5(System.currentTimeMillis() + UUID.randomUUID().toString());
    }

    public void delete(Long id) {
        fileUploadRepository.deleteById(id);
    }

    public void delete(List<Long> ids) {
        fileUploadRepository.deleteAllByIdInBatch(ids);
    }

    private static String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return fileName.substring(lastIndexOf);
    }

    public byte[] download(Long id) {
        FileUpload fileUpload = fileUploadRepository
                .findById(id)
                .orElseThrow(notFoundExceptionThrow(FileUpload.class.getSimpleName(), "id", id));
        return this.download(fileUpload);
    }

    public byte[] download(FileUpload fileUpload) {
        return this.download(fileUpload, true);
    }

    public byte[] download(FileUpload fileUpload, boolean throwException) {
        try {
            S3Object object = amazonConfiguration
                    .amazonS3()
                    .getObject(applicationProperties.getAmazonConfig().getPrivateBucketName(), fileUpload.getKey());
            S3ObjectInputStream objectContent = object.getObjectContent();
            return IOUtils.toByteArray(objectContent);
        } catch (AmazonServiceException | IOException e) {
            log.error(e.getMessage(), e);
            if (throwException) {
                throw badRequestExceptionThrow(localizationService.localize("unexpected.error.while.downloading.file" + e.getMessage()))
                        .get();
            }
            return null;
        }
    }

    public PrivateFileDTO getFile(FileUpload fileUpload) {
        PrivateFileDTO privatePhotoDTO = new PrivateFileDTO();
        privatePhotoDTO.setId(fileUpload.getId());
        privatePhotoDTO.setName(fileUpload.getName());
        privatePhotoDTO.setFile(this.download(fileUpload));
        return privatePhotoDTO;
    }
}
