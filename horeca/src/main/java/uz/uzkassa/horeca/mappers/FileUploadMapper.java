package uz.uzkassa.horeca.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.dto.file.FileDTO;

@Component
@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface FileUploadMapper {
    FileDTO toFileDTO(FileUpload entity);
}
