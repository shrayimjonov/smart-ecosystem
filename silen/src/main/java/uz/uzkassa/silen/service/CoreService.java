package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.config.ApplicationProperties;
import uz.uzkassa.silen.domain.*;
import uz.uzkassa.silen.dto.FacturaHostDTO;
import uz.uzkassa.silen.dto.SettingsDTO;
import uz.uzkassa.silen.dto.UserDTO;
import uz.uzkassa.silen.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.silen.dto.invoice.*;
import uz.uzkassa.silen.dto.vm.SignVM;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.EimzoClient;
import uz.uzkassa.silen.integration.FacturaClient;
import uz.uzkassa.silen.integration.TasnifClient;
import uz.uzkassa.silen.repository.pg.*;
import uz.uzkassa.silen.security.SecurityUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class CoreService extends BaseService {
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final BankRepository bankRepository;
    private final MeasureRepository measureRepository;
    private final FacturaClient facturaClient;
    private final EimzoClient eimzoClient;
    private final ApplicationProperties applicationProperties;
    private final FacturaHostRepository facturaHostRepository;
    private final TasnifClient tasnifClient;

    @Transactional
    public void syncRegions() {
        RegionDTO[] regions = facturaClient.getRegions();
        List<Region> regionList = new ArrayList<>();
        for (RegionDTO dto : regions) {
            if (dto.getRegionId() != 50) {
                Region region = regionRepository.findById(dto.getRegionId()).orElse(new Region());
                if (region.getId() == null) {
                    region.setId(dto.getRegionId());
                }
                region.setCode(dto.getCode());
                region.setName(dto.getName());
                region.setNameUz(dto.getNameUzLatn());
                region.setNameUzCryl(dto.getNameUzCyrl());
                region.setNameRu(dto.getNameRu());
                regionList.add(region);

            }
        }
        regionRepository.saveAll(regionList);
        regionRepository.flush();
    }

    @Transactional
    public void syncDistricts(Long regionId) {
        DistrictDTO[] districts = facturaClient.getDistricts(regionId);
        List<District> districtList = new ArrayList<>();
        for (DistrictDTO dto : districts) {
            String id = new StringBuilder(dto.getRegionId().toString())
                    .append(dto.getDistrictCode() <= 9 ? "0" + dto.getDistrictCode() : String.valueOf(dto.getDistrictCode()))
                    .toString();
            District district = districtRepository.findById(id).orElse(new District());
            district.setId(id);
            district.setCode(dto.getDistrictCode());
            district.setRegionId(regionId);
            district.setSoato(dto.getSoato());
            district.setName(dto.getName());
            district.setNameUz(dto.getNameUzLatn());
            district.setNameUzCryl(dto.getNameUzCyrl());
            district.setNameRu(dto.getNameRu());
            districtList.add(district);
            if (districtList.size() == 50) {
                districtRepository.saveAll(districtList);
                districtRepository.flush();
                districtList.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(districtList)) {
            districtRepository.saveAll(districtList);
            districtRepository.flush();
        }
    }

    @Transactional
    public void syncDistricts() {
        for (Region region : regionRepository.findAll()) {
            syncDistricts(region.getId());
        }

    }

    public VillageDTO[] getVillages(String soato) {
        return facturaClient.getVillages(soato);
    }

    @Transactional
    public void syncBanks(String lang) {
        BankDTO[] bankDTOS = facturaClient.getBanks(lang);
        List<Bank> banks = new ArrayList<>();
        for (BankDTO dto : bankDTOS) {
            Optional<Bank> bankOptional = bankRepository.findById(dto.getBankId());
            if (bankOptional.isPresent()) {
                continue;
            }
            Bank bank = new Bank();
            bank.setId(dto.getBankId());
            if ("uz".equalsIgnoreCase(lang)) {
                bank.setNameUz(dto.getName());
            } else {
                bank.setNameRu(dto.getName());
            }
            banks.add(bank);
            if (banks.size() == 50) {
                bankRepository.saveAll(banks);
                bankRepository.flush();
                banks.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(banks)) {
            bankRepository.saveAll(banks);
            bankRepository.flush();
        }
    }

    @Transactional
    public void syncMeasures(String lang) {
        MeasureDTO[] measureDTOS = facturaClient.getMeasures(lang);
        List<Measure> measures = new ArrayList<>();
        for (MeasureDTO dto : measureDTOS) {
            Optional<Measure> measureOptional = measureRepository.findById(dto.getMeasureId());
            if (measureOptional.isPresent()) {
                continue;
            }
            Measure measure = new Measure();
            measure.setId(dto.getMeasureId());
            if ("uz".equalsIgnoreCase(lang)) {
                measure.setNameUz(dto.getName());
            } else {
                measure.setNameRu(dto.getName());
            }
            measures.add(measure);
            if (measures.size() == 50) {
                measureRepository.saveAll(measures);
                measureRepository.flush();
                measures.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(measures)) {
            measureRepository.saveAll(measures);
            measureRepository.flush();
        }
    }

    public List<RegionDTO> getRegions() {
        return regionRepository.findAll().stream().map(Region::toDto).collect(Collectors.toList());
    }

    public List<DistrictDTO> getDistricts(Long regionId) {
        return districtRepository.findAllByRegionId(regionId).stream().map(District::toDto).collect(Collectors.toList());
    }

    public List<BankDTO> getBanks() {
        return bankRepository.findAllByOrderByNameRuAsc().stream().map(Bank::toDto).collect(Collectors.toList());
    }

    public List<MeasureDTO> getMeasures() {
        return measureRepository.findAllByOrderByNameRuAsc().stream().map(Measure::toDto).collect(Collectors.toList());
    }

    public ResponseDTO getGuid() {
        return facturaClient.getGuid();
    }

    public List<PackageDTO> getPackages(String mxikCode) {
        MxikPackageResponseDTO responseDTO = tasnifClient.getPackages(mxikCode);
        if (responseDTO != null) {
            return responseDTO.getData();
        }
        return Collections.emptyList();
    }

    public List<MxikDTO> getCompanyCatalog(String tin, String search) {
        search = search != null ? search.toLowerCase() : search;
        String tinOrPinfl = Optional.ofNullable(tin).orElse(SecurityUtils.getCurrentOrganizationTin());
        if (tinOrPinfl == null) {
            throw new BadRequestException("Tin or Pinfl is not defined");
        }

        MxikResponseDTO mxikResponseDTO = tasnifClient.getCompanyCatalog(tinOrPinfl, search);
        if (mxikResponseDTO == null) return Collections.emptyList();
        List<MxikDTO> list = mxikResponseDTO.getData();
        List<MxikDTO> result = new ArrayList<>();
        final boolean hasSearch = StringUtils.isNotEmpty(search);
        int i = 0;
        for (MxikDTO dto : list) {
            if (hasSearch) {
                if (dto.getMxikCode().contains(search)
                        || (StringUtils.isNotEmpty(dto.getSubPositionName()) && dto.getSubPositionName().toLowerCase().contains(search))
                        || (StringUtils.isNotEmpty(dto.getAttributeName()) && dto.getAttributeName().toLowerCase().contains(search))
                        || (StringUtils.isNotEmpty(dto.getBrandName()) && dto.getBrandName().toLowerCase().contains(search))) {
                    result.add(dto);
                    i++;
                }
            } else {
                result.add(dto);
                i++;
            }
            if (i == 99) {
                break;
            }
        }
        return result;
    }

    public ProviderResultDTO getProviders() {
        final String customerTin = getCurrentOrganizationTin();
        if (StringUtils.isEmpty(customerTin)) {
            throw new BadRequestException("ИНН требуется");
        }
        Set<String> providerTins = new HashSet<>();
        ProviderBindingDTO response = facturaClient.getProviders(customerTin);
        if (response != null && CollectionUtils.isNotEmpty(response.getProviders())) {
            providerTins = response.getProviders().stream()
                    .filter(providerDTO -> providerDTO.isEnabled())
                    .map(ProviderDTO::getProviderTin)
                    .collect(Collectors.toSet());
        }
        if (providerTins.contains(applicationProperties.getProviderTin())) {
            final Organization organization = getCurrentOrganization();
            organization.setXfileBinded(true);
            organizationRepository.save(organization);
            return null;
        } else {
            ProviderResultDTO result = new ProviderResultDTO();
            result.setClientTin(customerTin);
            providerTins.add(applicationProperties.getProviderTin());
            result.setProviderTins(providerTins);
            return result;
        }
    }

    public void saveProviders(final SignVM signVM) {
        final Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(signVM.getSign());
        if (pkcs7Dto.isSuccess()) {
            signVM.setSign(pkcs7Dto.getPkcs7b64());
            facturaClient.saveProviders(signVM);

            final Organization organization = getCurrentOrganization();
            organization.setXfileBinded(true);
            organizationRepository.save(organization);
        } else {
            log.error("JSON: {}", pkcs7Dto);
            throw new BadRequestException("Not verified, reason: " + pkcs7Dto.getMessage());
        }
    }

    @Transactional
    public Long setActiveHostForFactura(Long id) {
        facturaHostRepository.findFirstByActiveIsTrue().ifPresent(facturaHost -> {
            facturaHost.setActive(false);
            facturaHostRepository.save(facturaHost);
        });
        facturaHostRepository.findById(id).ifPresent(facturaHost -> {
            facturaHost.setActive(true);
            facturaHostRepository.save(facturaHost);
        });
        return id;
    }

    public List<FacturaHostDTO> getAllHostForFactura() {
        return facturaHostRepository.findAll().stream()
                .map(facturaHost -> new FacturaHostDTO(facturaHost.getId(), facturaHost.getHost(), facturaHost.isActive()))
                .collect(Collectors.toList());
    }

    public void syncPackages() {
        UserDTO dto = new UserDTO();
        dto.setLogin(getCurrentUserLogin());
        dto.setId(getCurrentUserId());
        rabbitMqProducer.syncPackages(dto);
    }

    @Transactional
    public void updateAppVersion(final SettingsDTO settingsDTO) {
        final Settings settings = settingsRepository.findFirstByOrderById().orElseGet(Settings::new);
        settings.setAppVersion(settingsDTO.getAppVersion());
        settingsRepository.save(settings);
    }

    public String getAppVersion() {
        return "1.6.0";
    }
}
