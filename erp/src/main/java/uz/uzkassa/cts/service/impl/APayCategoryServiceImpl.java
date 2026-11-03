package uz.uzkassa.cts.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.apay.APayCategory;
import uz.uzkassa.cts.dto.apay.category.APayCategoryDTO;
import uz.uzkassa.cts.dto.apay.category.APayCategoryListDTO;
import uz.uzkassa.cts.dto.payload.FilterPayload;
import uz.uzkassa.cts.filter.ApayCategoryFilter;
import uz.uzkassa.cts.integrations.ApayClient;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqAPayProducer;
import uz.uzkassa.cts.repository.ApayCategoryRepository;
import uz.uzkassa.cts.service.APayCategoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class APayCategoryServiceImpl extends BaseServiceImpl<APayCategory, Long> implements APayCategoryService {

    private final ApayCategoryRepository apayCategoryRepository;
    private final ApayClient apayClient;
    private final RabbitMqAPayProducer rabbitMqAPayProducer;

    public APayCategoryServiceImpl(ApayCategoryRepository apayCategoryRepository, ApayClient apayClient, RabbitMqAPayProducer rabbitMqAPayProducer) {
        super(apayCategoryRepository);
        this.apayCategoryRepository = apayCategoryRepository;
        this.apayClient = apayClient;
        this.rabbitMqAPayProducer = rabbitMqAPayProducer;
    }

    @Override
    @Transactional
    public void sync() {
        long count = apayClient.getCategories(0, 1).getTotalElements();
        int limit = 200;
        int page = (int) Math.ceil((double) count / limit);
        for (int i = 0; i <= page; i++) {
            FilterPayload filter = new FilterPayload();
            filter.setPage(i);
            filter.setSize(limit);
            rabbitMqAPayProducer.syncCategory(filter);
        }
    }

    @Override
    @Transactional
    public void syncQueue(FilterPayload filter) {
        List<APayCategoryListDTO> categories = apayClient.getCategories(filter.getPage(), filter.getSize()).getContent();
        if (CollectionUtils.isNotEmpty(categories)) {
            List<APayCategory> categoryList = new ArrayList<>();
            categories.forEach(aPayCategoryDTO -> {
                APayCategory aPayCategory = apayCategoryRepository.findFirstByApayId(aPayCategoryDTO.getId()).orElse(new APayCategory());
                aPayCategory.setApayId(aPayCategoryDTO.getId());
                aPayCategory.setParentId(aPayCategoryDTO.getParentId());
                aPayCategory.setNameRu(aPayCategoryDTO.getNameRu());
                aPayCategory.setNameUzLatin(aPayCategoryDTO.getNameUz());
                aPayCategory.setNameUzCyrillic(aPayCategoryDTO.getNameCyrillic());
                categoryList.add(aPayCategory);
            });

            ListUtils.partition(categoryList, 50).forEach(aPayCategories -> {
                apayCategoryRepository.saveAll(aPayCategories);
                apayCategoryRepository.flush();
            });
        }
    }

    @Override
    public List<APayCategoryDTO> lookUp(ApayCategoryFilter filter) {
        return apayCategoryRepository.getResultList(filter).getList().stream().map(aPayCategory -> {
            APayCategoryDTO aPayCategoryDTO = new APayCategoryDTO();
            aPayCategoryDTO.setId(aPayCategory.getId());
            aPayCategoryDTO.setName(aPayCategory.getName());
            aPayCategoryDTO.setApayId(aPayCategory.getApayId());
            aPayCategoryDTO.setParentId(aPayCategory.getParentId());
            return aPayCategoryDTO;
        }).collect(Collectors.toList());
    }
}
