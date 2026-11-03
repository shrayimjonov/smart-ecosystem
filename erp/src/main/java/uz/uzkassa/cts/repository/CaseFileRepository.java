package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.cases.CaseFile;
import uz.uzkassa.cts.enums.DocumentType;

import java.util.Optional;
import java.util.List;

@Repository
public interface CaseFileRepository extends BaseRepository<CaseFile, Long>{
    List<CaseFile> findByCaseId(Long caseId);
    Optional<CaseFile> findByCaseIdAndDocumentType(Long caseId, DocumentType documentType);
}
