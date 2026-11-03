package uz.uzkassa.horeca.repository.terminal;


import java.util.Optional;
import uz.uzkassa.horeca.domain.terminal.TerminalReceiptTemplate;
import uz.uzkassa.horeca.repository.BaseRepository;

public interface TerminalReceiptTemplateRepository extends BaseRepository<TerminalReceiptTemplate, Long> {
    Optional<TerminalReceiptTemplate> findDistinctTopByCompanyId(Long companyId);
}
