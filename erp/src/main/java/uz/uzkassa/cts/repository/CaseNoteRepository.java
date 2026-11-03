package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.cases.CaseNote;

@Repository
public interface CaseNoteRepository extends BaseRepository<CaseNote, Long> {
}
