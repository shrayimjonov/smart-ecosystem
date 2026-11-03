package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Vehicle;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.vehicle.VehicleDTO;
import uz.uzkassa.cts.dto.vehicle.VehicleDetailDTO;
import uz.uzkassa.cts.dto.vehicle.VehicleListDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.VehicleRepository;
import uz.uzkassa.cts.service.VehicleService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleServiceImpl extends BaseServiceImpl<Vehicle, Long> implements VehicleService {

    VehicleRepository vehicleRepository;
    ValidationContext validationContext;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, ValidationContext validationContext) {
        super(vehicleRepository);
        this.vehicleRepository = vehicleRepository;
        this.validationContext = validationContext;
    }

    @Override
    public Long create(VehicleDTO vehicleDTO) {
        validationContext.validate(vehicleDTO);
        Vehicle vehicle = new Vehicle();
        vehicle.setEmployeeId(vehicleDTO.getEmployeeId());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setColor(vehicleDTO.getColor());
        vehicle.setNumberPlate(vehicleDTO.getNumberPlate());
        return vehicleRepository.save(vehicle).getId();
    }

    @Override
    public Long update(Long id, VehicleDTO vehicleDTO) {
        validationContext.validate(vehicleDTO);
        return vehicleRepository.findById(id).map(vehicle -> {
            vehicle.setEmployeeId(vehicleDTO.getEmployeeId());
            vehicle.setModel(vehicleDTO.getModel());
            vehicle.setColor(vehicleDTO.getColor());
            vehicle.setNumberPlate(vehicleDTO.getNumberPlate());
            return vehicleRepository.save(vehicle).getId();
        }).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(Vehicle.class.getSimpleName()).get());
    }

    @Override
    public VehicleDetailDTO get(Long id) {
        return vehicleRepository.findById(id).map(vehicle -> {
            VehicleDetailDTO vehicleDetailDTO = new VehicleDetailDTO();
            vehicleDetailDTO.setId(vehicle.getId());
            vehicleDetailDTO.setEmployee(vehicle.getEmployee().toCommonDTO());
            vehicleDetailDTO.setModel(vehicle.getModel());
            vehicleDetailDTO.setColor(vehicle.getColor());
            vehicleDetailDTO.setNumberPlate(vehicle.getNumberPlate());
            vehicleDetailDTO.setStatus(vehicle.getStatus());
            return vehicleDetailDTO;
        }).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(Vehicle.class.getSimpleName()).get());
    }

    @Override
    public Page<VehicleListDTO> getList(BaseFilter filter) {
        ResultList<Vehicle> resultList = vehicleRepository.getResultList(filter);
        List<VehicleListDTO> result = resultList
                .getList()
                .stream()
                .map(vehicle -> {
                    VehicleListDTO vehicleListDTO = new VehicleListDTO();
                    vehicleListDTO.setId(vehicle.getId());
                    vehicleListDTO.setEmployee(vehicle.getEmployee().toCommonDTO());
                    vehicleListDTO.setModel(vehicle.getModel());
                    vehicleListDTO.setColor(vehicle.getColor());
                    vehicleListDTO.setNumberPlate(vehicle.getNumberPlate());
                    vehicleListDTO.setStatus(vehicle.getStatus());
                    return vehicleListDTO;
                })
                .toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public void updateStatus(Long id, CommonStatus commonStatus) {
        vehicleRepository.findById(id).ifPresentOrElse(vehicle -> {
            vehicle.setStatus(commonStatus);
            vehicleRepository.save(vehicle);
        }, () -> validationService.entityNotFoundExceptionThrow(Vehicle.class.getSimpleName()).get());
    }
}
