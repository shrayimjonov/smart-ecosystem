package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.Document;
import uz.uzkassa.cts.dto.document.DocumentListDTO;
import uz.uzkassa.cts.filter.DocumentFilter;

public interface DocumentService extends BaseService<Document, Long> {

    Page<DocumentListDTO> getList(DocumentFilter filter);

    Long upload(MultipartFile file, Long folderId);

    void rename(Long id, String name);
}
