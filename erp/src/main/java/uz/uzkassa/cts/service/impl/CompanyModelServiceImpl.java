package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.domain.CompanyModel;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.device.CompanyModelDTO;
import uz.uzkassa.cts.repository.CompanyModelRepository;
import uz.uzkassa.cts.repository.CompanyRepository;
import uz.uzkassa.cts.service.CompanyModelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CompanyModelServiceImpl extends BaseServiceImpl<CompanyModel, Long> implements CompanyModelService {

    private final CompanyModelRepository companyModelRepository;
    private final CompanyRepository companyRepository;

    public CompanyModelServiceImpl(CompanyModelRepository companyModelRepository, CompanyRepository companyRepository) {
        super(companyModelRepository);
        this.companyModelRepository = companyModelRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public void add(CompanyModelDTO companyModelDTO) {
        companyRepository.findById(companyModelDTO.getCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));

        List<CompanyModel> result = new ArrayList<>();
        companyModelDTO.getModels().forEach(model -> {
            CompanyModel companyModel = new CompanyModel();
            companyModel.setCompanyId(Optional.ofNullable(companyModelDTO.getCompanyId()).orElse(getCompanyId()));
            companyModel.setModelId(model.getId());
            result.add(companyModel);
        });

        companyModelRepository.saveAll(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommonDTO> getByCompany(Long companyId) {
        companyRepository.findById(companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));
        List<CommonDTO> result = new ArrayList<>();
        companyModelRepository
            .getAllByCompanyId(companyId)
            .forEach(companyModelRegistry -> result.add(companyModelRegistry.getModel().toCommonCodeDTO()));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommonDTO> getItems() {
        if (!isSuperAdmin() && getCompanyId() != null)
            return getByCompany(getCompanyId());
        else {
            List<CommonDTO> listDTO = new ArrayList<>();
            companyModelRepository.findAll().forEach(companyModel ->
                listDTO.add(companyModel.getModel().toCommonCodeDTO()));
            return listDTO;
        }
    }
}
