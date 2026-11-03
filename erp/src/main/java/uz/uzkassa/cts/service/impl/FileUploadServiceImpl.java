package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.dto.file.FileDetailDTO;
import uz.uzkassa.cts.dto.file.FileListDTO;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.repository.FileUploadRepository;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.ScpFileService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class FileUploadServiceImpl extends BaseServiceImpl<FileUpload, Long> implements FileUploadService {

    private final FileUploadRepository fileUploadRepository;
    private final ScpFileService scpFileService;

    public FileUploadServiceImpl(FileUploadRepository fileUploadRepository, ScpFileService scpFileService) {
        super(fileUploadRepository);
        this.fileUploadRepository = fileUploadRepository;
        this.scpFileService = scpFileService;
    }

    @Override
    public FileUpload upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw validationService.requiredExceptionThrow(FILE).get();
        }

        if (StringUtils.isEmpty(file.getOriginalFilename())) {
            throw validationService.requiredExceptionThrow(FILENAME).get();
        }

        try {
            byte[] bytes = file.getBytes();
            String fileName = FilenameUtils.getName(file.getOriginalFilename());
            fileName = fileName.replaceAll(" ", "_").replaceAll(" {2}", "_");

            String extension = ".".concat(FilenameUtils.getExtension(file.getOriginalFilename()));
            String fullName = fileName.concat(extension);

            String hash = StringUtils.join(UUID.randomUUID().toString(), extension);
//            Path path = Paths.get(applicationProperties.getFileUpload().getUploadFolder() + hash);

            String downloadUrl = scpFileService.upload(hash, bytes);

//            Files.write(path, bytes);
//            String fileDownloadUrl = applicationProperties.getFileUpload().getDownloadUrl() + hash;

            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileName(fullName);
            fileUpload.setContentType(file.getContentType());
            fileUpload.setSize(file.getSize());
            fileUpload.setDownloadUrl(downloadUrl);
            fileUploadRepository.save(fileUpload);

            return fileUpload;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_UNEXPECTED_ERROR).get();
        }

    }

    @Override
    public ResponseEntity<Resource> download(Long id) {
        FileUpload fileUpload = fileUploadRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(FILE));
        Resource resource = this.download(fileUpload.getDownloadUrl());
        try {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", URLEncoder.encode(fileUpload.getFileName(), StandardCharsets.UTF_8)))
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.parseMediaType(fileUpload.getContentType()))
                    .body(resource);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw validationService.badRequestExceptionThrow("An unexpected error has occurred while downloading file. Error:" + e.getMessage()).get();
        }
    }

    @Override
    public Resource download(String path) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = scpFileService.download(path);
            ByteArrayResource byteArrayResource = new ByteArrayResource(byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            return byteArrayResource;
        } catch (Exception e) {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.flush();
                    byteArrayOutputStream.close();
                } catch (IOException ioException) {
                    log.error("Error has occurred while download file: {}", ioException.getMessage());
                }
            }
            log.error(e.getMessage());
            throw validationService.badRequestExceptionThrow(EXPORT_FILE_FAILED, e.getMessage()).get();
        }
    }

    @Override
    public FileUpload upload(MultipartFile file, EntityTypeEnum entityType, Long entityId, DocumentType documentType) {
        try {
            byte[] bytes = file.getBytes();
            String fileName = Objects.requireNonNull(FilenameUtils.removeExtension(file.getOriginalFilename()))
                    .replaceAll(" ", "_").replaceAll(" {2}", "_");
            String extension = ".".concat(FilenameUtils.getExtension(file.getOriginalFilename()));
            String fullName = fileName.concat(extension);

            String hash = StringUtils.join(UUID.randomUUID().toString(), extension);
            String downloadUrl = scpFileService.upload(hash, bytes);

            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileName(fullName);
            fileUpload.setOriginalName(file.getOriginalFilename());
            fileUpload.setContentType(file.getContentType());
            fileUpload.setSize(file.getSize());
            fileUpload.setDownloadUrl(downloadUrl);
            fileUpload.setEntityId(entityId);
            fileUpload.setEntityType(entityType);
            fileUpload.setDocumentType(documentType);
            fileUploadRepository.save(fileUpload);
            return fileUpload;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_UNEXPECTED_ERROR).get();
        }
    }

    @Override
    public UploadFileResponse upload(MultipartFile file, Long entityId, String entityType, String documentType) {
        if (file == null || file.isEmpty()) {
            throw validationService.requiredExceptionThrow(FILE).get();
        }

        if (StringUtils.isEmpty(file.getOriginalFilename())) {
            throw validationService.requiredExceptionThrow(FILENAME).get();
        }

        EntityTypeEnum entityTypeEnum = EntityTypeEnum.get(entityType);
        if (entityTypeEnum == null) {
            throw validationService.badRequestExceptionThrow(INVALID_INPUT, entityType).get();
        }

        DocumentType documentTypeEnum = DocumentType.get(documentType);
        if (documentTypeEnum == null) {
            throw validationService.badRequestExceptionThrow(INVALID_INPUT, documentType).get();
        }
        if (DocumentType.LEASE_CONTRACT.equals(documentTypeEnum) || DocumentType.CADASTRE.equals(documentTypeEnum) || DocumentType.APPLICATION.equals(documentTypeEnum)) {
            if (!MediaType.APPLICATION_PDF_VALUE.equalsIgnoreCase(file.getContentType())) {
                throw validationService.badRequestExceptionThrow(DOC_TYPE).get();
            }
        }

        try {
            byte[] bytes = file.getBytes();
            //File original name is kept for documents in which types are ATTACHMENT, DOCUMENT and OTHER
            String fileName = (
                    DocumentType.ATTACHMENT.equals(documentTypeEnum) ||
                            DocumentType.DOCUMENT.equals(documentTypeEnum) ||
                            DocumentType.OTHER.equals(documentTypeEnum))
                    ? FilenameUtils.removeExtension(file.getOriginalFilename()) : documentTypeEnum.getName();

            fileName = fileName.replaceAll(" ", "_").replaceAll(" {2}", "_");

            String extension = ".".concat(FilenameUtils.getExtension(file.getOriginalFilename()));
            String fullName = fileName.concat(extension);

            String hash = StringUtils.join(UUID.randomUUID().toString(), extension);
//            Path path = Paths.get(applicationProperties.getFileUpload().getUploadFolder() + hash);
            String downloadUrl = scpFileService.upload(hash, bytes);

//            Files.write(path, bytes);
//            String fileDownloadUrl = applicationProperties.getFileUpload().getDownloadUrl() + hash;

            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileName(fullName);
            fileUpload.setContentType(file.getContentType());
            fileUpload.setSize(file.getSize());
            fileUpload.setDownloadUrl(downloadUrl);
            fileUpload.setEntityId(entityId);
            fileUpload.setEntityType(entityTypeEnum);
            fileUpload.setDocumentType(documentTypeEnum);
            fileUploadRepository.save(fileUpload);

            return fileUpload.getDTO();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_UNEXPECTED_ERROR).get();
        }
    }


    @Override
    public UploadFileResponse upload(byte[] fileByte, String fileName, String contentType, Long fileSize,
                                     Long entityId, String entityType, String documentType) {

        EntityTypeEnum entityTypeEnum = EntityTypeEnum.get(entityType);
        if (entityTypeEnum == null) {
            throw validationService.badRequestExceptionThrow(INVALID_INPUT, entityType).get();
        }

        DocumentType documentTypeEnum = DocumentType.get(documentType);
        if (documentTypeEnum == null) {
            throw validationService.badRequestExceptionThrow(INVALID_INPUT, documentType).get();
        }

        if (fileByte.length == 0) {
            throw validationService.requiredExceptionThrow(FILE).get();
        }

        String hash = StringUtils.join(UUID.randomUUID().toString(), fileName.substring(fileName.lastIndexOf(".")));

        try {
            String downloadUrl = scpFileService.upload(hash, fileByte);
            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileName(fileName);
            fileUpload.setContentType(contentType);
            fileUpload.setSize(fileSize);
            fileUpload.setDownloadUrl(downloadUrl);
            fileUpload.setEntityId(entityId);
            fileUpload.setEntityType(entityTypeEnum);
            fileUpload.setDocumentType(documentTypeEnum);
            fileUploadRepository.save(fileUpload);
            return fileUpload.getDTO();
        } catch (Exception e) {
            log.error("Error has occurred while upload file: {}", e.getMessage());
            throw validationService.badRequestExceptionThrow(UNEXPECTED_ERROR, e.getMessage()).get();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UploadFileResponse> list(Long entityId, String entityType) {
        if (entityId == null) {
            throw validationService.requiredExceptionThrow(ENTITY).get();
        }
        if (entityType == null) {
            throw validationService.requiredExceptionThrow(ENTITY_TYPE).get();
        }
        if (EntityTypeEnum.get(entityType) == null) {
            throw validationService.badRequestExceptionThrow(INVALID_INPUT, entityType).get();
        }

        return fileUploadRepository.list(entityId, entityType).stream().map(FileUpload::getDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long listTotal(Long entityId, String entityType) {
        if (entityId == null) {
            throw validationService.requiredExceptionThrow(ENTITY).get();
        }
        if (entityType == null) {
            throw validationService.requiredExceptionThrow(ENTITY_TYPE).get();
        }
        if (EntityTypeEnum.get(entityType) == null) {
            throw validationService.badRequestExceptionThrow(INVALID_INPUT, entityType).get();
        }

        return (fileUploadRepository.listTotal(entityId, entityType)).longValue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileListDTO> getList(EntityTypeEnum entityType, Long entityId) {
        return fileUploadRepository.findAllByEntityTypeAndEntityIdAndDeletedIsFalse(entityType, entityId)
                .stream()
                .map(fileUpload -> {
                    FileListDTO fileListDTO = new FileListDTO();
                    fileListDTO.setId(fileUpload.getId());
                    fileListDTO.setName(fileUpload.getName());
                    fileListDTO.setUrl(fileUpload.getDownloadUrl());
                    fileListDTO.setDocumentType(fileUpload.getDocumentType());
                    return fileListDTO;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FileDetailDTO get(Long id) {
        return fileUploadRepository.findById(id).map(fileUpload -> {
            FileDetailDTO fileDetailDTO = new FileDetailDTO();
            fileDetailDTO.setId(fileUpload.getId());
            fileDetailDTO.setName(fileUpload.getName());
            fileDetailDTO.setOriginalName(fileUpload.getOriginalName());
            fileDetailDTO.setUrl(fileUpload.getDownloadUrl());
            fileDetailDTO.setContentType(fileUpload.getContentType());
            fileDetailDTO.setSize(fileUpload.getSize());
            fileDetailDTO.setEntityId(fileUpload.getEntityId());
            fileDetailDTO.setEntityType(fileUpload.getEntityType());
            fileDetailDTO.setDocumentType(fileUpload.getDocumentType());
            return fileDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(FILE));
    }

    @Override
    public void remove(FileUpload fileUpload) {
        scpFileService.delete(fileUpload.getDownloadUrl());
        fileUploadRepository.delete(fileUpload);
    }

    @Override
    public InputStream getInputStream(Long id) {
        FileUpload fileUpload = fileUploadRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(FILE));
        try {
            URL fileUrl = new URL(fileUpload.getDownloadUrl());
            return fileUrl.openStream();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw validationService.badRequestExceptionThrow(EXPORT_FILE_FAILED, e.getMessage()).get();
        }
    }

    @Override
    public void rename(Long id, String name) {
        FileUpload fileUpload = fileUploadRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(FILE));
        fileUpload.setOriginalName(name);
        fileUploadRepository.save(fileUpload);
    }
}
