package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.PrintTemplate;
import uz.uzkassa.silen.dto.PrintTemplateDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.PrintTemplateFilter;
import uz.uzkassa.silen.repository.pg.PrintTemplateRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrintTemplateService extends BaseService {
    PrintTemplateRepository printTemplateRepository;

    public String save(PrintTemplateDTO dto) {
        Optional<PrintTemplate> checkByName = printTemplateRepository.findFirstByOrganizationIdAndProductIdAndNameIgnoreCase(getCurrentOrganizationId(), dto.getProductId(), dto.getName());
        if (checkByName.isPresent()) {
            if (dto.getId() == null) {
                throw badRequest("Шаблон с таким именем уже создано").get();
            } else if (!dto.getId().equals(checkByName.get().getId()))
                throw badRequest("Шаблон с таким именем уже создано").get();

        }
        PrintTemplate printTemplate = Optional.ofNullable(dto.getId()).flatMap(printTemplateRepository::findById).orElse(new PrintTemplate());
        printTemplate.setName(dto.getName());
        printTemplate.setProductId(dto.getProductId());
        printTemplate.setTemplate(dto.getTemplate());
        printTemplate.setOrganizationId(getCurrentOrganizationId());
        printTemplate.setCodeType(dto.getCodeType());
        printTemplateRepository.save(printTemplate);
        return printTemplate.getId();
    }

    public PrintTemplateDTO getOne(String id) {
        PrintTemplate printTemplate = printTemplateRepository.findById(id).orElseThrow(notFound("Шаблон не найден"));
        return printTemplate.toDTO(isAgency());
    }

    public Page<PrintTemplateDTO> findAllByFilter(PrintTemplateFilter filter) {
        return printTemplateRepository.findAllByFilter(filter).map(printTemplate -> printTemplate.toDTO(isAgency()));
    }

    public void delete(String id) {
        printTemplateRepository.deleteById(id);
    }

    public List<SelectItem> getItems(PrintTemplateFilter filter) {
        return printTemplateRepository.findAllByFilter(filter).map(PrintTemplate::toSelectItem).getContent();
    }
}
