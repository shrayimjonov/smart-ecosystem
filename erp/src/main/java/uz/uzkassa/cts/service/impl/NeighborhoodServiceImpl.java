package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.address.District;
import uz.uzkassa.cts.domain.address.Neighborhood;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.payload.OfdNeighborhoodSyncPayload;
import uz.uzkassa.cts.dto.tin.integration.OfdNeighborhoodListResponseDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.integrations.OfdClient;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqOfdProducer;
import uz.uzkassa.cts.repository.DistrictRepository;
import uz.uzkassa.cts.repository.NeighborhoodRepository;
import uz.uzkassa.cts.service.NeighborhoodService;
import uz.uzkassa.cts.utils.WordUtils;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class NeighborhoodServiceImpl extends BaseServiceImpl<Neighborhood, Long> implements NeighborhoodService {

    private final NeighborhoodRepository neighborhoodRepository;
    private final DistrictRepository districtRepository;
    private final OfdClient ofdClient;
    private final RabbitMqOfdProducer rabbitMqOfdProducer;

    public NeighborhoodServiceImpl(NeighborhoodRepository neighborhoodRepository, DistrictRepository districtRepository, OfdClient ofdClient, RabbitMqOfdProducer rabbitMqOfdProducer) {
        super(neighborhoodRepository);
        this.neighborhoodRepository = neighborhoodRepository;
        this.districtRepository = districtRepository;
        this.ofdClient = ofdClient;
        this.rabbitMqOfdProducer = rabbitMqOfdProducer;
    }

    @Transactional
    @Override
    public void sync() {
        districtRepository.findAllByDeletedIsFalseOrderById().forEach(district -> {
            if (!district.getNameUzLatin().contains("Hududlararo")) {
                rabbitMqOfdProducer.syncNeighborhood(new OfdNeighborhoodSyncPayload(district.getRegionId(), district.getRegion().getCode(), district.getId(), district.getCode()));
            }
        });
    }

    @Transactional
    @Override
    public void syncByDistrict(Long districtId) {
        districtRepository.findById(districtId).map(district -> {
            rabbitMqOfdProducer.syncNeighborhood(new OfdNeighborhoodSyncPayload(district.getRegionId(), district.getRegion().getCode(), district.getId(), district.getCode()));
            return district;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(DISTRICT));
    }

    private void sync(OfdNeighborhoodListResponseDTO ofdNeighborhoodListResponseDTO, Long districtId) {
        if (ofdNeighborhoodListResponseDTO == null || CollectionUtils.isEmpty(ofdNeighborhoodListResponseDTO.getData())) {
            return;
        }
        List<Neighborhood> neighborhoods = ofdNeighborhoodListResponseDTO.getData()
                .stream()
                .map(ofdNeighborhoodDTO -> {
                    Neighborhood neighborhood = neighborhoodRepository.findByCode(ofdNeighborhoodDTO.getCode()).orElseGet(Neighborhood::new);
                    neighborhood.setDistrictId(districtId);
                    neighborhood.setCode(ofdNeighborhoodDTO.getCode());
                    neighborhood.setNameRu(ofdNeighborhoodDTO.getName());
                    neighborhood.setNameUzLatin(ofdNeighborhoodDTO.getName());
                    neighborhood.setNameUzCyrillic(WordUtils.latinToCyrill(ofdNeighborhoodDTO.getName()));
                    return neighborhood;
                }).collect(Collectors.toList());
        neighborhoodRepository.saveAllAndFlush(neighborhoods);
    }

    @Transactional
    @Override
    public void syncFromOfd(OfdNeighborhoodSyncPayload ofdNeighborhoodSyncPayload) {
        sync(ofdClient.getNeighborhoods(ofdNeighborhoodSyncPayload.getRegionCode(), ofdNeighborhoodSyncPayload.getDistrictCode()), ofdNeighborhoodSyncPayload.getDistrictId());
    }

    @Override
    public List<CommonCodeDTO> lookUp(BaseFilter filter) {
        return neighborhoodRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonCodeDTO).collect(Collectors.toList());
    }
}
