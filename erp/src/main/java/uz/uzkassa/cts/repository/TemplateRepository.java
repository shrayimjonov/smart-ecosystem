package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Template;
import uz.uzkassa.cts.enums.ChannelType;
import uz.uzkassa.cts.enums.TemplateType;
import uz.uzkassa.cts.repository.custom.TemplateRepositoryCustom;

import java.util.Optional;

@Repository
public interface TemplateRepository extends BaseRepository<Template, Long>, TemplateRepositoryCustom {

    Optional<Template> findFirstByTypeAndChannelTypeAndDeletedIsFalse(TemplateType type, ChannelType channelType);
}
