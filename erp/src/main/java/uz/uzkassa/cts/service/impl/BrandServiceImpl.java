package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.inventory.Brand;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.BrandRepository;
import uz.uzkassa.cts.service.BrandService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class BrandServiceImpl extends BaseServiceImpl<Brand, Long> implements BrandService {

    private final BrandRepository brandRepository;
    private final ValidationContext validationContext;

    public BrandServiceImpl(BrandRepository brandRepository, ValidationContext validationContext) {
        super(brandRepository);
        this.brandRepository = brandRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(CommonDTO brandDTO) {
        validationContext.validate(brandDTO);
        Brand brand = new Brand();
        brand.setName(brandDTO.getName());
        return brandRepository.save(brand).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, CommonDTO brandDTO) {
        brandDTO.setId(id);
        validationContext.validate(brandDTO);
        return brandRepository.findById(id).map(brand -> {
            brand.setName(brandDTO.getName());
            return brandRepository.save(brand).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(BRAND));
    }

    @Override
    public CommonDTO get(Long id) {
        return brandRepository.findById(id).map(SimpleEntity::toCommonDTO).orElseThrow(validationService.entityNotFoundExceptionThrow(BRAND));
    }

    @Override
    public Page<CommonDTO> getList(BaseFilter filter) {
        ResultList<Brand> resultList = brandRepository.getResultList(filter);
        List<CommonDTO> result = resultList
                .getList()
                .stream()
                .map(SimpleEntity::toCommonDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(BaseFilter filter) {
        return brandRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }
}
