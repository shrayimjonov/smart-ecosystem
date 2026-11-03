package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.Document;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.document.DocumentListDTO;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.DocumentFilter;
import uz.uzkassa.cts.repository.DocumentRepository;
import uz.uzkassa.cts.service.DocumentService;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.utils.FileUtils;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class DocumentServiceImpl extends BaseServiceImpl<Document, Long> implements DocumentService {

    private final DocumentRepository documentRepository;
    private final FileUploadService fileUploadService;

    public DocumentServiceImpl(DocumentRepository documentRepository, FileUploadService fileUploadService) {
        super(documentRepository);
        this.documentRepository = documentRepository;
        this.fileUploadService = fileUploadService;
    }

    @Override
    public Page<DocumentListDTO> getList(DocumentFilter filter) {
        ResultList<Document> resultList = documentRepository.getResultList(filter);
        List<DocumentListDTO> result = resultList.getList().stream().map(document -> {
            DocumentListDTO documentListDTO = new DocumentListDTO();
            documentListDTO.setId(document.getId());
            if (document.getFileId() != null) {
                documentListDTO.setName(document.getFile().getOriginalName());
                documentListDTO.setPath(document.getFile().getDownloadUrl());
                documentListDTO.setFileSize(FileUtils.getFileNameSizeAsString(document.getFile().getSize()));
            } else {
                documentListDTO.setName(document.getName());
                documentListDTO.setPath(document.getPath());
                documentListDTO.setFileSize(FileUtils.getFileNameSizeAsString(document.getSize()));
            }
            return documentListDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public Long upload(MultipartFile file, Long folderId) {
        Document document = new Document();
        document.setFolderId(folderId);
        documentRepository.save(document);
        FileUpload fileUpload = fileUploadService.upload(file, EntityTypeEnum.DOCUMENT, document.getId(), DocumentType.DOCUMENT);
        document.setFileId(fileUpload.getId());
        document.setFile(fileUpload);
        documentRepository.save(document);
        return document.getId();
    }

    @Override
    @Transactional
    public void rename(Long id, String name) {
        Document document = documentRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(DOCUMENT));
        if (document.getFileId() != null) {
            fileUploadService.rename(document.getFileId(), name);
        } else {
            document.setName(name);
            documentRepository.save(document);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Document document = documentRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(DOCUMENT));
        if (document.getFileId() != null) {
            fileUploadService.deleteById(document.getFileId());
        }
        documentRepository.delete(document);
    }
}
