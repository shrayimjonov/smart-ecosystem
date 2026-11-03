package uz.uzkassa.smartpos.web.rest;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.constants.CacheConstants;
import uz.uzkassa.smartpos.domain.postgres.common.City;
import uz.uzkassa.smartpos.domain.postgres.common.Neighborhood;
import uz.uzkassa.smartpos.domain.postgres.common.Region;
import uz.uzkassa.smartpos.dto.CityDTO;
import uz.uzkassa.smartpos.dto.NeighborhoodDTO;
import uz.uzkassa.smartpos.dto.RegionDTO;
import uz.uzkassa.smartpos.dto.nic.OfdResponseDataDTO;
import uz.uzkassa.smartpos.integration.ofd.OfdClient;
import uz.uzkassa.smartpos.mappers.NeighborhoodMapper;
import uz.uzkassa.smartpos.repository.postgres.common.NeighborhoodRepository;
import uz.uzkassa.smartpos.repository.postgres.common.RegionRepository;
import uz.uzkassa.smartpos.service.OfdService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link RegionDTO}.
 */
@RestController
@RequestMapping("/api")
public class RegionCityResource {
    private final RegionRepository regionRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final NeighborhoodMapper neighborhoodMapper;
    private final OfdService ofdService;
    private final OfdClient ofdClient;

    public RegionCityResource(RegionRepository regionRepository,
                              NeighborhoodRepository neighborhoodRepository, NeighborhoodMapper neighborhoodMapper, OfdService ofdService, OfdClient ofdClient) {
        this.regionRepository = regionRepository;
        this.neighborhoodRepository = neighborhoodRepository;
        this.neighborhoodMapper = neighborhoodMapper;
        this.ofdService = ofdService;
        this.ofdClient = ofdClient;
    }

    /**
     * {@code GET  /regions} : get all Regions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of regions in body.
     */
    @Cacheable(cacheNames = CacheConstants.REGION_CACHE)
    @GetMapping("/regions")
    public List<RegionDTO> getAllRegions() {
        return regionRepository.findAll().stream().map(Region::getDTO).collect(Collectors.toList());
    }

    /**
     * {@code GET  /cities/:id} : get cities within region with given id
     *
     * @param regionId the id of the regions cities to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cities,
     * or with status {@code 404 (Not Found)}.
     */
    @Cacheable(cacheNames = CacheConstants.CITY_CACHE)
    @GetMapping("/cities/{regionId}")
    public List<CityDTO> getCityList(@PathVariable Long regionId) {
        List<City> cities = regionRepository.findByRegionId(regionId);
        return cities.stream().map(City::getDTO).collect(Collectors.toList());
    }

    @GetMapping("/neighborhood/{cityId}")
    public List<NeighborhoodDTO> getNeighborhoodList(@PathVariable Long cityId) {
        List<Neighborhood> neighborhoods = neighborhoodRepository.findAllByCity_Id(cityId);
        return neighborhoods.stream().map(neighborhoodMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/neighborhood/ofd")
    public OfdResponseDataDTO<List<NeighborhoodDTO>> getNeighborhoodList(@RequestParam Long cityCode, @RequestParam Long regionCode) {
        return ofdClient.neighborhoodList(ofdService.getToken(), regionCode, cityCode).getBody();
    }

    @GetMapping("/neighborhood/sync")
    public void getNeighborhoodList() {
        Thread.startVirtualThread(ofdService::syncNeighborhoods);
    }
}
