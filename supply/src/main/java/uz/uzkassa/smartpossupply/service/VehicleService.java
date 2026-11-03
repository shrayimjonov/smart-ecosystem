package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.warehouse.Vehicle;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.soliq.TaxCompanyDTO;
import uz.uzkassa.smartpossupply.dto.vehicle.VehicleDTO;
import uz.uzkassa.smartpossupply.dto.vehicle.VehicleDetailDTO;
import uz.uzkassa.smartpossupply.dto.vehicle.VehicleListDTO;
import uz.uzkassa.smartpossupply.dto.vehicle.VehicleLookupDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillDriverTransportDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.integration.FacturaRestClient;
import uz.uzkassa.smartpossupply.integration.SoliqClient;
import uz.uzkassa.smartpossupply.repository.VehicleRepository;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.01.2025
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VehicleService extends BaseService {

    VehicleRepository vehicleRepository;
    FacturaRestClient facturaRestClient;
    SoliqClient soliqClient;
    ValidationContext validationContext;

    @Transactional
    public Long create(VehicleDTO vehicleDTO) {
        if (!isFromAdmin()) {
            vehicleDTO.setCompanyId(getCurrentCompanyId());
        }
        validationContext.validate(vehicleDTO);
        return vehicleRepository.save(toEntity(vehicleDTO)).getId();
    }

    @Transactional
    public void batchCreate(List<VehicleDTO> vehicleDTOList) {
        List<Vehicle> vehicles = vehicleDTOList.stream().map(vehicleDTO -> {
            if (!isFromAdmin()) {
                vehicleDTO.setCompanyId(getCurrentCompanyId());
            }
            validationContext.validate(vehicleDTO);
            return this.toEntity(vehicleDTO);
        }).toList();
        vehicleRepository.saveAll(vehicles);
    }

    private Vehicle toEntity(VehicleDTO vehicleDTO) {
        Vehicle vehicle = new Vehicle();
        vehicle.setName(vehicleDTO.getName());
        vehicle.setCompanyId(Optional.ofNullable(vehicleDTO.getCompanyId()).orElseGet(BaseService::getCurrentCompanyId));
        vehicle.setRegNo(vehicleDTO.getRegNo());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setTrailerModel(vehicleDTO.getTrailerModel());
        vehicle.setTrailerRegNo(vehicleDTO.getTrailerRegNo());
        vehicle.setTrailerType(vehicleDTO.getTrailerType());
        vehicle.setOwnerPinfl(vehicleDTO.getOwnerPinfl());
        vehicle.setOwnerFullName(vehicleDTO.getOwnerFullName());
        vehicle.setTransportCompanyName(vehicleDTO.getTransportCompanyName());
        vehicle.setTransportCompanyTin(vehicleDTO.getTransportCompanyTin());
        return vehicle;
    }

    @Transactional
    public Long update(Long id, VehicleDTO vehicleDTO) {
        return vehicleRepository.findById(id).map(vehicle -> {
            vehicleDTO.setId(id);
            this.validationContext.validate(vehicleDTO);
            vehicle.setName(vehicleDTO.getName());
            vehicle.setRegNo(vehicleDTO.getRegNo());
            vehicle.setModel(vehicleDTO.getModel());
            vehicle.setTrailerModel(vehicleDTO.getTrailerModel());
            vehicle.setTrailerRegNo(vehicleDTO.getTrailerRegNo());
            vehicle.setTrailerType(vehicleDTO.getTrailerType());
            vehicle.setOwnerPinfl(vehicleDTO.getOwnerPinfl());
            vehicle.setOwnerFullName(vehicleDTO.getOwnerFullName());
            vehicle.setTransportCompanyName(vehicleDTO.getTransportCompanyName());
            vehicle.setTransportCompanyTin(vehicleDTO.getTransportCompanyTin());
            vehicleRepository.save(vehicle);
            return vehicle.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Vehicle.class.getSimpleName(), "id", id));
    }

    public List<VehicleLookupDTO> lookUp(BaseFilter filter) {
        return vehicleRepository.lookUp(filter).stream().map(Vehicle::toLookupDTO).toList();
    }

    public Page<VehicleListDTO> getList(BaseFilter filter) {
        ResultList<Vehicle> resultList = vehicleRepository.getResultList(filter);
        List<VehicleListDTO> result = resultList
            .getList()
            .stream()
            .map(vehicle -> {
                VehicleListDTO vehicleListDTO = new VehicleListDTO();
                vehicleListDTO.setId(vehicle.getId());
                vehicleListDTO.setName(vehicle.getName());
                vehicleListDTO.setRegNo(vehicle.getRegNo());
                vehicleListDTO.setModel(vehicle.getModel());
                vehicleListDTO.setOwnerFullName(vehicle.getOwnerFullName());
                vehicleListDTO.setOwnerPinfl(vehicle.getOwnerPinfl());
                vehicleListDTO.setStatus(vehicle.getStatus());
                return vehicleListDTO;
            }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void delete(Long id) {
        vehicleRepository.deleteById(id);
    }

    public VehicleDetailDTO get(Long id) {
        return vehicleRepository.getDetail(id).map(vehicle -> {
            VehicleDetailDTO vehicleDetailDTO = new VehicleDetailDTO();
            vehicleDetailDTO.setId(vehicle.getId());
            vehicleDetailDTO.setName(vehicle.getName());
            vehicleDetailDTO.setStatus(vehicle.getStatus());
            vehicleDetailDTO.setCompany(vehicle.getCompany().toCommonBaseDTO());
            vehicleDetailDTO.setRegNo(vehicle.getRegNo());
            vehicleDetailDTO.setModel(vehicle.getModel());
            vehicleDetailDTO.setTrailerRegNo(vehicle.getTrailerRegNo());
            vehicleDetailDTO.setTrailerModel(vehicle.getTrailerModel());
            vehicleDetailDTO.setTrailerType(vehicle.getTrailerType());
            vehicleDetailDTO.setTransportCompanyTin(vehicle.getTransportCompanyTin());
            vehicleDetailDTO.setTransportCompanyName(vehicle.getTransportCompanyName());
            vehicleDetailDTO.setOwnerPinfl(vehicle.getOwnerPinfl());
            vehicleDetailDTO.setOwnerFullName(vehicle.getOwnerFullName());
            return vehicleDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Vehicle.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        vehicleRepository.findById(id).ifPresentOrElse(vehicle -> {
            vehicle.setStatus(status);
            vehicleRepository.save(vehicle);
        }, () -> validationService.entityNotFoundExceptionThrow(Vehicle.class.getSimpleName(), "id", id));
    }

    public WaybillDriverTransportDTO[] getTransports(String tin) {
        return facturaRestClient.getDriverTransports(tin);
    }

    public TaxCompanyDTO getDriverInfo(String tin) {
        return soliqClient.getUserByTin(tin);
    }
}
