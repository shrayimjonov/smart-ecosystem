package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.domain.postgres.common.City;
import uz.uzkassa.smartpos.domain.postgres.common.Neighborhood;
import uz.uzkassa.smartpos.dto.NeighborhoodDTO;
import uz.uzkassa.smartpos.dto.nic.OfdResponseDataDTO;
import uz.uzkassa.smartpos.integration.ofd.OfdClient;
import uz.uzkassa.smartpos.mappers.NeighborhoodMapper;
import uz.uzkassa.smartpos.repository.postgres.common.CityRepository;
import uz.uzkassa.smartpos.repository.postgres.common.NeighborhoodRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfdService {
    private final NeighborhoodRepository neighborhoodRepository;
    private final CityRepository cityRepository;
    private final ApplicationProperties applicationProperties;
    private final OfdClient ofdClient;
    private final NeighborhoodMapper neighborhoodMapper;

    @Scheduled(cron = "0 0 4 * * 5")
    @Transactional
    public void syncNeighborhoods() {
        List<City> cities = cityRepository.findAll();
        cities.forEach(city -> {
            log.info("Syncing city: {}", city.getNameRu());
            try {
                ResponseEntity<OfdResponseDataDTO<List<NeighborhoodDTO>>> responseEntity = ofdClient.neighborhoodList(getToken(), city.getRegionCode(), city.getCode());
                OfdResponseDataDTO<List<NeighborhoodDTO>> body = responseEntity.getBody();
                if (body != null && CollectionUtils.isNotEmpty(body.getData())) {
                    List<NeighborhoodDTO> neighborhoodList = body.getData();
                    List<Long> codes = neighborhoodList.stream().map(NeighborhoodDTO::getCode).collect(Collectors.toList());
                    List<Neighborhood> deletedNeighborhoods = neighborhoodRepository.findByCodeNotInAndCityCodeAndRegionCode(codes, city.getCode(), city.getRegionCode());
                    Map<Long, Neighborhood> neighborhoodMap = neighborhoodRepository.findByCodeIn(codes)
                            .stream().collect(Collectors.toMap(Neighborhood::getCode, Function.identity()));
                    List<Neighborhood> neighborhoods = new ArrayList<>();
                    neighborhoodList.forEach(dto -> {
                        Neighborhood neighborhood = neighborhoodMap.getOrDefault(dto.getCode(), new Neighborhood());
                        neighborhoodMapper.partialUpdate(dto, neighborhood);
                        neighborhood.setCityCode(city.getCode());
                        neighborhood.setRegionCode(city.getRegionCode());
                        neighborhoods.add(neighborhood);
                    });
                    neighborhoodRepository.deleteAll(deletedNeighborhoods);
                    neighborhoodRepository.saveAll(neighborhoods);
                }
            } catch (Exception e) {
                log.error("Error during sync neighborhood list: {}", e.getMessage());
            }
        });
    }

    public String getToken() {
        return ofdClient.getToken(applicationProperties.getOfd().getUsername(), applicationProperties.getOfd().getPassword());
    }
}
