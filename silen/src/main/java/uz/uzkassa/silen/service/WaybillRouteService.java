package uz.uzkassa.silen.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.WaybillRoute;
import uz.uzkassa.silen.dto.waybill.WaybillRouteDTO;
import uz.uzkassa.silen.repository.pg.WaybillRouteRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaybillRouteService extends BaseService {
    private final WaybillRouteRepository waybillRouteRepository;

    public WaybillRouteDTO save(WaybillRouteDTO waybillRouteDTO) {
        WaybillRoute entity = Optional.ofNullable(waybillRouteDTO.getId()).flatMap(waybillRouteRepository::findById).orElse(new WaybillRoute());
        entity.setTin(waybillRouteDTO.getTin());
        entity.setRegionId(waybillRouteDTO.getRegionId());
        entity.setRegionName(waybillRouteDTO.getRegionName());
        entity.setDistrictCode(waybillRouteDTO.getDistrictCode());
        entity.setDistrictName(waybillRouteDTO.getDistrictName());
        entity.setMahallaId(waybillRouteDTO.getMahallaId());
        entity.setMahallaName(waybillRouteDTO.getMahallaName());
        entity.setAddress(waybillRouteDTO.getAddress());
        entity.setLongitude(waybillRouteDTO.getLongitude());
        entity.setLatitude(waybillRouteDTO.getLatitude());
        entity.setTransportType(waybillRouteDTO.getTransportType());
        waybillRouteRepository.save(entity);
        return entity.toDTO();
    }

    public List<WaybillRouteDTO> findAll(String tin) {
        return waybillRouteRepository.findAllByTin(tin).stream().map(WaybillRoute::toDTO).toList();
    }

    public void delete(String id) {
        waybillRouteRepository.deleteById(id);
    }

}
