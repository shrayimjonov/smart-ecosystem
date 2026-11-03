package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Note;
import uz.uzkassa.cts.repository.custom.NoteRepositoryCustom;

@Repository
public interface NoteRepository extends BaseRepository<Note, Long>, NoteRepositoryCustom {
}
