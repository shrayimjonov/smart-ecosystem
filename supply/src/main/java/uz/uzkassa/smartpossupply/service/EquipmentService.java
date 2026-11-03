package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.equipment.Equipment;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.equipment.EquipmentDTO;
import uz.uzkassa.smartpossupply.dto.equipment.EquipmentDetailDTO;
import uz.uzkassa.smartpossupply.dto.equipment.EquipmentListMobileDTO;
import uz.uzkassa.smartpossupply.dto.payload.UpdateCompanyEquipmentCountPayload;
import uz.uzkassa.smartpossupply.filters.EquipmentFilter;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqCompanyProducer;
import uz.uzkassa.smartpossupply.repository.EquipmentRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EquipmentService extends BaseService {

    EquipmentRepository equipmentRepository;
    FileUploadService fileUploadService;
    RabbitMqCompanyProducer rabbitMqCompanyProducer;

    @Transactional
    public Long create(EquipmentDTO equipmentDTO) {
        this.validate(equipmentDTO);
        Equipment equipment = new Equipment();
        equipment.setName(equipmentDTO.getName());
        equipment.setBrand(equipmentDTO.getBrand());
        equipment.setInventoryNumber(equipmentDTO.getInventoryNumber());
        equipment.setType(equipmentDTO.getType());
        equipment.setCustomerBranchId(equipmentDTO.getCustomerBranchId());
        equipmentRepository.save(equipment);
        rabbitMqCompanyProducer.updateEquipmentCount(new UpdateCompanyEquipmentCountPayload(getCurrentCompanyId(), 1));
        return equipment.getId();
    }

    @Transactional
    public void update(Long id, EquipmentDTO equipmentDTO) {
        this.validate(equipmentDTO);
        equipmentRepository.findById(id).ifPresentOrElse(equipment -> {
            equipment.setName(equipmentDTO.getName());
            equipment.setBrand(equipmentDTO.getBrand());
            equipment.setInventoryNumber(equipmentDTO.getInventoryNumber());
            equipment.setType(equipmentDTO.getType());
            equipment.setCustomerBranchId(equipmentDTO.getCustomerBranchId());
        }, () -> {
            throw validationService.entityNotFoundExceptionThrow(Equipment.class.getSimpleName(), "id", id).get();
        });
    }

    public EquipmentDetailDTO get(Long id) {
        return equipmentRepository.findById(id).map(equipment -> {
            EquipmentDetailDTO equipmentDetailDTO = new EquipmentDetailDTO();
            equipmentDetailDTO.setId(equipment.getId());
            equipmentDetailDTO.setName(equipment.getName());
            equipmentDetailDTO.setInventoryNumber(equipment.getInventoryNumber());
            equipmentDetailDTO.setBrand(equipment.getBrand());
            equipmentDetailDTO.setType(equipment.getType());
            equipmentDetailDTO.setCustomerBranch(equipment.getCustomerBranch().toCommonDTO());
            if (equipment.getPhoto() != null) {
                equipmentDetailDTO.setPhoto(equipment.getPhoto().toFileDTO());
            }
            return equipmentDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Equipment.class.getSimpleName(), "id", id));
    }

    public Page<EquipmentListMobileDTO> getListForMobile(EquipmentFilter filter) {
        ResultList<Equipment> resultList = equipmentRepository.getResultList(filter);
        List<EquipmentListMobileDTO> result = resultList.getList().stream().map(equipment -> {
            EquipmentListMobileDTO equipmentListMobileDTO = new EquipmentListMobileDTO();
            equipmentListMobileDTO.setId(equipment.getId());
            equipmentListMobileDTO.setName(equipment.getName());
            equipmentListMobileDTO.setBrand(equipment.getBrand());
            equipmentListMobileDTO.setType(equipment.getType());
            equipmentListMobileDTO.setCustomerBranch(equipment.getCustomerBranch().toCommonDTO());
            return equipmentListMobileDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void uploadPhoto(Long id, MultipartFile photo) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Equipment.class.getSimpleName(), "id", id));
        FileUpload fileUpload = fileUploadService.fileUpload(photo);
        equipment.setPhotoId(fileUpload.getId());
        equipment.setPhoto(fileUpload);
        equipmentRepository.save(equipment);
    }

    @Transactional
    public void removePhoto(Long id) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Equipment.class.getSimpleName(), "id", id));
        FileUpload photo = equipment.getPhoto();
        if (photo != null) {
            equipment.setPhoto(null);
            equipment.setPhotoId(null);
            equipmentRepository.save(equipment);
            fileUploadService.delete(photo);
        }
    }

    private void validate(EquipmentDTO equipmentDTO) {
        if (equipmentDTO.getType() == null) {
            throw validationService.badRequestExceptionThrow("Type must not be null").get();
        }
        if (equipmentDTO.getCustomerBranchId() == null) {
            throw validationService.badRequestExceptionThrow("Customer branch ID must not be null").get();
        }
    }
}
