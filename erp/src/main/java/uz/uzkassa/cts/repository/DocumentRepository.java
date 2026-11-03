package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Document;
import uz.uzkassa.cts.repository.custom.DocumentRepositoryCustom;

@Repository
public interface DocumentRepository extends BaseRepository<Document, Long>, DocumentRepositoryCustom {
}
