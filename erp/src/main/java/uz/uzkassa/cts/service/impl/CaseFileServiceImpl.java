package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.cases.CaseFile;
import uz.uzkassa.cts.domain.cases.CaseFile_;
import uz.uzkassa.cts.domain.cases.Cases;
import uz.uzkassa.cts.dto.casefile.request.CaseFileRequestDto;
import uz.uzkassa.cts.dto.casefile.response.CaseFileResponseDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.CaseStatus;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CaseFilter;
import uz.uzkassa.cts.mappings.CaseFileMapper;
import uz.uzkassa.cts.repository.CaseFileRepository;
import uz.uzkassa.cts.service.CaseFileService;
import uz.uzkassa.cts.service.CaseService;
import uz.uzkassa.cts.service.FileUploadService;

import java.util.ArrayList;

@Service
public class CaseFileServiceImpl extends BaseServiceImpl<CaseFile, Long> implements CaseFileService {

    private final CaseFileRepository repository;
    private final FileUploadService fileUploadService;
    private final CaseService caseService;
    private final CaseFileMapper caseFileMapper;

    public CaseFileServiceImpl(CaseFileRepository repository,
                               FileUploadService fileUploadService,
                               CaseService caseService,
                               CaseFileMapper caseFileMapper) {
        super(repository);
        this.repository = repository;
        this.fileUploadService = fileUploadService;
        this.caseService = caseService;
        this.caseFileMapper = caseFileMapper;
    }

    @Override
    public CaseFileResponseDto get(Long id) {
        return caseFileMapper.toDto(findById(id));
    }

    @Override
    public Page<CaseFileResponseDto> list(CaseFilter filter) {
        return repository
            .findAll(getCaseFileSpecification(filter), filter.getOrderedPageable())
            .map(caseFileMapper::toDto);
    }

    @Override
    public Page<CaseFileResponseDto> getByCaseId(Long id, BaseFilter filter) {
        return repository
            .findAll(getCaseFileSpecification(filter).and(getByCaseId(id)), filter.getOrderedPageable())
            .map(caseFileMapper::toDto);
    }


    @Override
    public CaseFileResponseDto upload(CaseFileRequestDto dto, MultipartFile file) {
        if (file.isEmpty()) {
            throw validationService.requiredExceptionThrow(FILE).get();
        }
        Cases cases = caseService.findById(dto.getCaseId());
        if (CaseStatus.DRAFT == cases.getStatus() || CaseStatus.DEFECTIVE == cases.getStatus()) {
            repository.findByCaseIdAndDocumentType(dto.getCaseId(), dto.getDocumentType())
                .ifPresent(repository::delete);

            UploadFileResponse response = fileUploadService.upload(file, dto.getCaseId(), EntityTypeEnum.CASE_FILE.name(), DocumentType.CASE_FILE.name());

            CaseFile newCase = new CaseFile();
            newCase.setFileName(response.getFileName());
            newCase.setFileUrl(response.getFileDownloadUri());
            newCase.setDescription(dto.getDescription());
            newCase.setCaseId(dto.getCaseId());
            newCase.setDocumentType(dto.getDocumentType());
            newCase = save(newCase);

            return caseFileMapper.toDto(newCase);
        }
        throw validationService.statusChangeExceptionThrow("DRAFT or DEFECT").get();
    }


    public static Specification<CaseFile> getByCaseId(Long caseId) {
        return (root, query, cb) -> cb.equal(root.get(CaseFile_.CASE_ID), caseId);
    }

    private static Specification<CaseFile> getCaseFileSpecification(BaseFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> fileUrl = root.get(CaseFile_.FILE_URL);
                list.add(criteriaBuilder.like(criteriaBuilder.lower(fileUrl), filter.getSearchForQuery()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }
}
