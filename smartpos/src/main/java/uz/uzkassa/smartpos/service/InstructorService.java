package uz.uzkassa.smartpos.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.domain.postgres.Instructor;
import uz.uzkassa.smartpos.domain.postgres.Instructor_;
import uz.uzkassa.smartpos.dto.InstructorFileCreateDTO;
import uz.uzkassa.smartpos.dto.InstructorFileDTO;
import uz.uzkassa.smartpos.dto.InstructorFileUpdateDTO;
import uz.uzkassa.smartpos.enums.EntityType;
import uz.uzkassa.smartpos.enums.InstructorType;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.filter.InstructorFilter;
import uz.uzkassa.smartpos.mappers.InstructorMapper;
import uz.uzkassa.smartpos.repository.postgres.FileUploadRepository;
import uz.uzkassa.smartpos.repository.postgres.InstructorRepository;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class InstructorService extends SmartposService {
    private final FileUploadRepository fileUploadRepository;
    private final InstructorMapper instructorMapper;
    private final InstructorRepository instructorRepository;
    private final FileUploadService fileUploadService;


    public InstructorFileDTO create(InstructorFileCreateDTO dto) {
        FileUpload fileUpload = fileUploadRepository.findById(dto.getFileId()).orElseThrow(notFound(localizationService.getMessage("file")));
        fileUpload.setEntityType(EntityType.INSTRUCTOR);
        fileUploadRepository.save(fileUpload);

        FileUpload imageUpload = fileUploadRepository.findById(dto.getImageId()).orElseThrow(notFound(localizationService.getMessage("file")));
        fileUpload.setEntityType(EntityType.INSTRUCTOR);
        fileUploadRepository.save(imageUpload);

        Instructor instructor = instructorMapper.toEntity(dto);
        instructorRepository.save(instructor);
        return instructorMapper.toDto(instructor);
    }

    public List<InstructorFileDTO> findAllFileByInstructorType(InstructorType type) {
        List<Instructor> files = instructorRepository.findAllByType(type);
        return files.stream().map(instructorMapper::toDto).toList();
    }

    public Page<InstructorFileDTO> findAllFile(InstructorFilter filter) {
        return instructorRepository.findAll((Specification<Instructor>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(filter.getParentCodes())) {
                predicates.add(root.get(Instructor_.parentCode).in(filter.getParentCodes()));
            }
            if (CollectionUtils.isNotEmpty((filter.getTypes()))) {
                predicates.add(root.get(Instructor_.type).in(filter.getTypes()));
            }
            if (StringUtils.isNotBlank(filter.getOrderBy())) {
                if (SortTypeEnum.asc == filter.getSortOrder()) {
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(filter.getOrderBy())));
                } else {
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(filter.getOrderBy())));
                }
            } else {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Instructor_.id)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, filter.getPageable()).map(instructorMapper::toDto);
    }

    public InstructorFileDTO update(InstructorFileUpdateDTO dto) {
        Instructor instructor = instructorRepository.findById(dto.getId()).orElseThrow(notFound("Instructor file "));
        instructor = instructorMapper.updateEntity(dto, instructor);
        instructorRepository.save(instructor);
        return instructorMapper.toDto(instructor);
    }

    @Transactional
    public void delete(Long id) {
        Instructor instructor = instructorRepository.findById(id).orElseThrow(notFound("Instructor file "));
        instructorRepository.delete(instructor);
        if (instructor.getFile() != null) {
            fileUploadService.removeObject(instructor.getFile());
        }
        if (instructor.getImage() != null) {
            fileUploadService.removeObject(instructor.getImage());
        }
    }
}
