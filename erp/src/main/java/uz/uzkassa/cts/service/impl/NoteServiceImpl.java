package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.domain.Note;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.note.NoteDTO;
import uz.uzkassa.cts.dto.note.admin.NoteDetailAdminDTO;
import uz.uzkassa.cts.dto.note.admin.NoteListAdminDTO;
import uz.uzkassa.cts.dto.note.cabinet.NoteDetailCabinetDTO;
import uz.uzkassa.cts.dto.note.cabinet.NoteListCabinetDTO;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.NoteRepository;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.NoteService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class NoteServiceImpl extends BaseServiceImpl<Note, Long> implements NoteService {

    private final NoteRepository noteRepository;
    private final ValidationContext validationContext;
    private final FileUploadService fileUploadService;

    public NoteServiceImpl(NoteRepository noteRepository, ValidationContext validationContext, FileUploadService fileUploadService) {
        super(noteRepository);
        this.noteRepository = noteRepository;
        this.validationContext = validationContext;
        this.fileUploadService = fileUploadService;
    }

    @Override
    @Transactional
    public Long create(NoteDTO noteDTO) {
        validationContext.validate(noteDTO);
        Note note = new Note();
        note.setDescription(noteDTO.getDescription());
        note.setEntityId(noteDTO.getEntityId());
        note.setEntityType(noteDTO.getEntityType());
        return noteRepository.save(note).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, NoteDTO noteDTO) {
        return noteRepository.findById(id).map(note -> {
            note.setDescription(noteDTO.getDescription());
            return noteRepository.save(note).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(NOTE));
    }

    @Override
    public NoteDetailAdminDTO getForAdmin(Long id) {
        return noteRepository.findById(id).map(note -> {
            NoteDetailAdminDTO noteDetailDTO = new NoteDetailAdminDTO();
            noteDetailDTO.setId(note.getId());
            noteDetailDTO.setDescription(note.getDescription());
            noteDetailDTO.setEntityId(note.getEntityId());
            noteDetailDTO.setEntityType(note.getEntityType());
            noteDetailDTO.setCreatedDate(note.getCreatedDate());
            if (note.getCreator() != null)
                noteDetailDTO.setCreator(note.getCreator().toCommonDTO());
            if (note.getFileId() != null) {
                noteDetailDTO.setFile(note.getFile().toFileDTO());
            }
            return noteDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(NOTE));
    }

    @Override
    public NoteDetailCabinetDTO getForCabinet(Long id) {
        return noteRepository.findById(id).map(note -> {
            NoteDetailCabinetDTO noteDetailDTO = new NoteDetailCabinetDTO();
            noteDetailDTO.setId(note.getId());
            noteDetailDTO.setDescription(note.getDescription());
            noteDetailDTO.setEntityId(note.getEntityId());
            noteDetailDTO.setEntityType(note.getEntityType());
            noteDetailDTO.setCreatedDate(note.getCreatedDate());
            if (note.getCreator() != null)
                noteDetailDTO.setCreator(note.getCreator().toCommonDTO());
            if (note.getFileId() != null) {
                noteDetailDTO.setFile(note.getFile().toFileDTO());
            }
            return noteDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(NOTE));
    }

    @Override
    public Page<NoteListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<Note> resultList = noteRepository.getResultList(filter);
        List<NoteListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(note -> {
                    NoteListAdminDTO noteListDTO = new NoteListAdminDTO();
                    noteListDTO.setId(note.getId());
                    noteListDTO.setDescription(note.getDescription());
                    noteListDTO.setCreatedDate(note.getCreatedDate());
                    if (note.getCreator() != null)
                        noteListDTO.setCreator(note.getCreator().toCommonDTO());
                    if (note.getFileId() != null) {
                        noteListDTO.setFile(note.getFile().toFileDTO());
                    }
                    return noteListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<NoteListCabinetDTO> getListForCabinet(BaseFilter filter) {
        ResultList<Note> resultList = noteRepository.getResultList(filter);
        List<NoteListCabinetDTO> result = resultList
                .getList()
                .stream()
                .map(note -> {
                    NoteListCabinetDTO noteListDTO = new NoteListCabinetDTO();
                    noteListDTO.setId(note.getId());
                    noteListDTO.setDescription(note.getDescription());
                    noteListDTO.setCreatedDate(note.getCreatedDate());
                    if (note.getCreator() != null)
                        noteListDTO.setCreator(note.getCreator().toCommonDTO());
                    if (note.getFileId() != null) {
                        noteListDTO.setFile(note.getFile().toFileDTO());
                    }
                    return noteListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public Long upload(EntityTypeEnum entityType, Long entityId, MultipartFile file) {
        FileUpload response = fileUploadService.upload(file, entityType, entityId, DocumentType.NOTE);
        Note note = new Note();
        note.setEntityId(entityId);
        note.setEntityType(entityType);
        note.setFileId(response.getId());
        return noteRepository.save(note).getId();
    }
}
