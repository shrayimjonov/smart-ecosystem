package uz.uzkassa.cts.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.dto.file.FileDetailDTO;
import uz.uzkassa.cts.dto.file.FileListDTO;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;

import java.io.InputStream;
import java.util.List;

public interface FileUploadService extends BaseService<FileUpload, Long> {

    FileUpload upload(MultipartFile file, EntityTypeEnum entityType, Long entityId, DocumentType documentType);

    UploadFileResponse upload(MultipartFile file, Long entityId, String entityType, String documentType);

    FileUpload upload(MultipartFile file);

    Resource download(String path);

    ResponseEntity<Resource> download(Long id);

    UploadFileResponse upload(byte[] fileByte, String fileName, String contentType, Long fileSize,
                              Long entityId, String entityType, String documentType);

    List<UploadFileResponse> list(Long entityId, String entityType);

    Long listTotal(Long entityId, String entityType);

    List<FileListDTO> getList(EntityTypeEnum entityType, Long entityId);

    FileDetailDTO get(Long id);

    void remove(FileUpload fileUpload);

    InputStream getInputStream(Long id);

    void rename(Long id, String name);
}
