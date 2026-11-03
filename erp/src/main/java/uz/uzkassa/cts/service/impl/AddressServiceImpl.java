package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Address;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.address.AddressDTO;
import uz.uzkassa.cts.dto.address.AddressListDTO;
import uz.uzkassa.cts.enums.AddressType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.repository.AddressRepository;
import uz.uzkassa.cts.repository.DistrictRepository;
import uz.uzkassa.cts.repository.NeighborhoodRepository;
import uz.uzkassa.cts.repository.RegionRepository;
import uz.uzkassa.cts.service.AddressService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressServiceImpl extends BaseServiceImpl<Address, Long> implements AddressService {

    AddressRepository addressRepository;
    ValidationContext validationContext;
    RegionRepository regionRepository;
    DistrictRepository districtRepository;
    NeighborhoodRepository neighborhoodRepository;

    public AddressServiceImpl(AddressRepository addressRepository, ValidationContext validationContext, RegionRepository regionRepository, DistrictRepository districtRepository, NeighborhoodRepository neighborhoodRepository) {
        super(addressRepository);
        this.addressRepository = addressRepository;
        this.validationContext = validationContext;
        this.regionRepository = regionRepository;
        this.districtRepository = districtRepository;
        this.neighborhoodRepository = neighborhoodRepository;
    }

    @Override
    @Transactional
    public Long create(EntityTypeEnum entityType, Long entityId, AddressDTO addressDTO) {
        addressDTO.setEntityType(entityType);
        addressDTO.setEntityId(entityId);
        validationContext.validate(addressDTO);
        Address address = new Address();
        address.setEntityId(entityId);
        address.setEntityType(entityType);
        address.setType(addressDTO.getType());
        address.setLandmark(addressDTO.getLandmark());
        address.setStreet(addressDTO.getStreet());
        address.setHouse(addressDTO.getHouse());
        address.setApartment(addressDTO.getApartment());
        address.setLongitude(addressDTO.getLongitude());
        address.setLatitude(addressDTO.getLatitude());
        address.setRegionId(addressDTO.getRegionId());
        address.setDistrictId(addressDTO.getDistrictId());
        address.setNeighborhoodId(addressDTO.getNeighborhoodId());
        return addressRepository.save(address).getId();
    }

    @Override
    @Transactional
    public Long update(EntityTypeEnum entityType, Long entityId, AddressDTO addressDTO) {
        addressDTO.setEntityType(entityType);
        addressDTO.setEntityId(entityId);
        validationContext.validate(addressDTO);
        return addressRepository.findFirstByEntityIdAndEntityTypeAndTypeAndDeletedIsFalse(entityId, entityType, addressDTO.getType()).map(address -> {
            address.setLandmark(addressDTO.getLandmark());
            address.setStreet(addressDTO.getStreet());
            address.setHouse(addressDTO.getHouse());
            address.setApartment(addressDTO.getApartment());
            address.setLongitude(addressDTO.getLongitude());
            address.setLatitude(addressDTO.getLatitude());
            if (!Objects.equals(address.getRegionId(), addressDTO.getRegionId())) {
                regionRepository.findById(address.getRegionId()).ifPresentOrElse(region -> {
                    address.setRegionId(region.getId());
                    address.setRegion(region);
                }, () -> validationService.entityNotFoundExceptionThrow(REGION).get());
            }
            if (!Objects.equals(address.getDistrictId(), addressDTO.getDistrictId())) {
                districtRepository.findById(address.getDistrictId()).ifPresentOrElse(district -> {
                    address.setDistrictId(district.getId());
                    address.setDistrict(district);
                }, () -> validationService.entityNotFoundExceptionThrow(DISTRICT).get());
            }
            if (!Objects.equals(address.getNeighborhoodId(), addressDTO.getNeighborhoodId())) {
                neighborhoodRepository.findById(address.getNeighborhoodId()).ifPresentOrElse(neighborhood -> {
                    address.setNeighborhoodId(neighborhood.getId());
                    address.setNeighborhood(neighborhood);
                }, () -> validationService.entityNotFoundExceptionThrow(NEIGHBORHOOD).get());
            }
            return addressRepository.save(address).getId();
        }).orElseThrow(() -> validationService.entityNotFoundExceptionThrow(ADDRESS).get());
    }

    @Override
    @Transactional
    public void updateBatch(EntityTypeEnum entityType, Long entityId, List<AddressDTO> addressDTOList) {
        Map<Long, Address> addressMap = addressRepository.findAllByEntityIdAndEntityTypeAndDeletedIsFalse(entityId, entityType)
                .stream()
                .collect(Collectors.toMap(Address::getId, address -> address));

        addressDTOList.forEach(addressDTO -> {
            if (addressMap.containsKey(addressDTO.getId())) {
                this.update(entityType, entityId, addressDTO);
                addressMap.remove(addressDTO.getId());
            } else {
                this.create(entityType, entityId, addressDTO);
            }
        });
        if (!addressMap.isEmpty()) {
            addressRepository.deleteAll(addressMap.values());
        }
    }

    @Override
    public List<AddressListDTO> getList(EntityTypeEnum entityType, Long entityId) {
        return addressRepository.findAllByEntityIdAndEntityTypeAndDeletedIsFalse(entityId, entityType).stream().map(address -> {
            AddressListDTO addressListDTO = new AddressListDTO();
            addressListDTO.setId(address.getId());
            addressListDTO.setType(address.getType());
            addressListDTO.setRegion(address.getRegion().toCommonDTO());
            if (address.getDistrictId() != null) {
                addressListDTO.setDistrict(address.getDistrict().toCommonDTO());
            }
            if (address.getNeighborhoodId() != null) {
                addressListDTO.setNeighborhood(address.getNeighborhood().toCommonDTO());
            }
            addressListDTO.setLandmark(address.getLandmark());
            addressListDTO.setStreet(address.getStreet());
            addressListDTO.setHouse(address.getHouse());
            addressListDTO.setApartment(address.getApartment());
            addressListDTO.setLongitude(address.getLongitude());
            addressListDTO.setLatitude(address.getLatitude());
            return addressListDTO;
        }).toList();
    }

    @Override
    public Address getLegalAddress(Long entityId, EntityTypeEnum entityType) {
        return this.getAddressByType(entityId, entityType, AddressType.LEGAL);
    }

    @Override
    public Address getPermanentAddress(Long entityId, EntityTypeEnum entityType) {
        return this.getAddressByType(entityId, entityType, AddressType.PERMANENT);
    }

    @Override
    public Address getAddressByType(Long entityId, EntityTypeEnum entityType, AddressType type) {
        return addressRepository.findFirstByEntityIdAndEntityTypeAndTypeAndDeletedIsFalse(entityId, entityType, type).orElse(null);
    }

    @Override
    @Transactional
    public void createBatch(EntityTypeEnum entityType, Long entityId, List<AddressDTO> addressDTOList) {
        if (addressDTOList.isEmpty() || addressDTOList.size() > 2) {
            throw validationService.badRequestExceptionThrow(INVALID_ACTION).get();
        }
        Map<Long, Address> existingAddresses = addressRepository.findAllByEntityIdAndEntityTypeAndDeletedIsFalse(entityId, entityType).stream().collect(Collectors.toMap(SimpleEntity::getId, address -> address));

        for (AddressDTO addressDTO : addressDTOList) {
            addressDTO.setEntityType(entityType);
            addressDTO.setEntityId(entityId);
            validationContext.validate(addressDTO);
            Address address;
            if (addressDTO.getId() == null) {
                address = new Address();
            } else {
                address = addressRepository
                        .findById(addressDTO.getId())
                        .orElseThrow(() -> validationService.badRequestExceptionThrow(INVALID_ACTION).get());
                existingAddresses.remove(address.getId());
            }
            address.setEntityId(entityId);
            address.setEntityType(entityType);
            address.setType(addressDTO.getType());
            address.setLandmark(addressDTO.getLandmark());
            address.setStreet(addressDTO.getStreet());
            address.setHouse(addressDTO.getHouse());
            address.setApartment(addressDTO.getApartment());
            address.setLongitude(addressDTO.getLongitude());
            address.setLatitude(addressDTO.getLatitude());
            address.setRegionId(addressDTO.getRegionId());
            address.setDistrictId(addressDTO.getDistrictId());
            address.setNeighborhoodId(addressDTO.getNeighborhoodId());
            addressRepository.save(address);
        }

        if (!existingAddresses.isEmpty()) {
            addressRepository.deleteAllById(existingAddresses.keySet());
        }
    }

}
