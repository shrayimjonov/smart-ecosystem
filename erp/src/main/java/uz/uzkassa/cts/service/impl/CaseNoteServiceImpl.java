package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.cases.CaseNote;
import uz.uzkassa.cts.domain.cases.CaseNote_;
import uz.uzkassa.cts.dto.casenote.request.CaseNoteRequestDto;
import uz.uzkassa.cts.dto.casenote.response.CaseNoteResponseDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.CaseMessageType;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.CaseNoteMapper;
import uz.uzkassa.cts.repository.CaseNoteRepository;
import uz.uzkassa.cts.service.CaseNoteService;
import uz.uzkassa.cts.service.FileUploadService;

import java.util.ArrayList;

@Service
public class CaseNoteServiceImpl extends BaseServiceImpl<CaseNote, Long> implements CaseNoteService {

    private final CaseNoteRepository repository;
    private final FileUploadService fileUploadService;
    private final CaseNoteMapper caseNoteMapper;

    public CaseNoteServiceImpl(CaseNoteRepository repository,
                               FileUploadService fileUploadService,
                               CaseNoteMapper caseNoteMapper) {
        super(repository);
        this.repository = repository;
        this.fileUploadService = fileUploadService;
        this.caseNoteMapper = caseNoteMapper;
    }

    @Override
    public CaseNoteResponseDto get(Long id) {
        return caseNoteMapper.toDto(findById(id));
    }

    @Override
    public Page<CaseNoteResponseDto> getByCaseId(Long id, BaseFilter filter) {
        return repository
            .findAll(getCaseFileSpecification(filter).and(getByCaseId(id)), filter.getOrderedPageable())
            .map(caseNoteMapper::toDto);
    }

    @Override
    public CaseNoteResponseDto create(CaseNoteRequestDto dto) {
        CaseNote caseNote = new CaseNote();
        caseNote.setCaseId(dto.getCaseId());
        caseNote.setMessage(dto.getMessage());
        caseNote.setMessageType(CaseMessageType.TEXT);
        caseNote = save(caseNote);

        return caseNoteMapper.toDto(caseNote);
    }

    @Override
    public CaseNoteResponseDto uploadByCaseId(Long id, MultipartFile file) {
        CaseNote note = new CaseNote();
        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.CASE_FILE.name(), DocumentType.CASE_FILE.name());

        note.setCaseId(id);
        note.setMessage(response.getFileDownloadUri());
        note.setMessageType(CaseMessageType.FILE);
        save(note);

        return caseNoteMapper.toDto(note);
    }

    public static Specification<CaseNote> getByCaseId(Long caseId) {
        return (root, query, cb) -> cb.equal(root.get(CaseNote_.CASE_ID), caseId);
    }

    private static Specification<CaseNote> getCaseFileSpecification(BaseFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> messagePath = root.get(CaseNote_.MESSAGE);
                list.add(criteriaBuilder.like(criteriaBuilder.lower(messagePath), filter.getSearchForQuery()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }
}
