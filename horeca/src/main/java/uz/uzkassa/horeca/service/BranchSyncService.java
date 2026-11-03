package uz.uzkassa.horeca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.hall.Hall;
import uz.uzkassa.horeca.domain.menu.Menu;
import uz.uzkassa.horeca.dto.address.AddressDTO;
import uz.uzkassa.horeca.dto.branch.BranchDTO;
import uz.uzkassa.horeca.dto.category.CatalogDTO;
import uz.uzkassa.horeca.dto.category.CategoryDTO;
import uz.uzkassa.horeca.dto.department.DepartmentDTO;
import uz.uzkassa.horeca.dto.hall.HallDTO;
import uz.uzkassa.horeca.dto.menu.MenuDTO;
import uz.uzkassa.horeca.dto.product.ProductDTO;
import uz.uzkassa.horeca.dto.sync.BranchSyncDTO;
import uz.uzkassa.horeca.enums.*;
import uz.uzkassa.horeca.repository.DistrictRepository;
import uz.uzkassa.horeca.repository.NeighborhoodRepository;
import uz.uzkassa.horeca.repository.RegionRepository;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BranchSyncService extends BaseService {

    RegionRepository regionRepository;
    DistrictRepository districtRepository;
    NeighborhoodRepository neighborhoodRepository;
    BranchService branchService;
    DepartmentService departmentService;
    HallService hallService;
    CategoryService categoryService;
    private final ProductService productService;
    private final MenuService menuService;

    @Transactional
    public Long sync(BranchSyncDTO branchSyncDTO) {
        try {
            log.info("SYNC PAYLOAD: {}", objectMapper.writeValueAsString(branchSyncDTO));
        } catch (JsonProcessingException e) {
            log.error("Object writing error: {}", e.getMessage());
        }
        Company company = companyRepository.findByTin(branchSyncDTO.getCompany().getTin()).orElseThrow(notFoundExceptionThrow(Company.class.getSimpleName()));
        BranchDTO branchDTO = new BranchDTO();
        branchDTO.setName(branchSyncDTO.getName());
        branchDTO.setCompanyId(company.getId());
        branchDTO.setCtsId(branchSyncDTO.getId());
        AddressDTO addressDTO = new AddressDTO();
        if (branchSyncDTO.getAddress().getRegionCode() != null) {
            regionRepository.findFirstByCode(branchSyncDTO.getAddress().getRegionCode().intValue()).ifPresent(region -> addressDTO.setRegionId(region.getId()));
            if (branchSyncDTO.getAddress().getDistrictCode() != null) {
                districtRepository.findFirstByRegion_CodeAndCode(branchSyncDTO.getAddress().getRegionCode().intValue(), branchSyncDTO.getAddress().getDistrictCode().intValue()).ifPresent(district -> {
                    addressDTO.setDistrictId(district.getId());
                });
                if (branchSyncDTO.getAddress().getNeighborhoodCode() != null) {
                    neighborhoodRepository.findFirstByDistrict_CodeAndCode(branchSyncDTO.getAddress().getDistrictCode().intValue(), branchSyncDTO.getAddress().getNeighborhoodCode()).ifPresent(neighborhood -> {
                        addressDTO.setNeighborhoodId(neighborhood.getId());
                    });
                }
            }
        }

        addressDTO.setStreet(branchSyncDTO.getAddress().getStreet());
        addressDTO.setHouse(branchSyncDTO.getAddress().getHouse());
        addressDTO.setApartment(branchSyncDTO.getAddress().getApartment());
        addressDTO.setLongitude(branchSyncDTO.getAddress().getLongitude());
        addressDTO.setLatitude(branchSyncDTO.getAddress().getLatitude());
        branchDTO.setAddress(addressDTO);

        Branch branch = branchRepository.findFirstByCtsId(branchSyncDTO.getId()).orElse(null);
        Long branchId;
        if (branch == null) {
            branchId = branchService.create(branchDTO);
            DepartmentDTO departmentDTO = new DepartmentDTO();
            departmentDTO.setBranchId(branchId);
            departmentDTO.setName("Основной цех");
            departmentDTO.setType(DepartmentType.KITCHEN);
            departmentService.create(departmentDTO);
            HallDTO hallDTO = new HallDTO();
            hallDTO.setBranchId(branchId);
            hallDTO.setName("Основной зал");
            hallService.create(hallDTO);
            CatalogDTO catalogDTO = new CatalogDTO();
            catalogDTO.setCode("10202002003000001");
            catalogDTO.setName("Прочие услуги сервисного обслуживания в заведении общественного питания");
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setBranchId(branchId);
            categoryDTO.setName("Прочие услуги сервисного обслуживания в заведении общественного питания");
            categoryDTO.setType(CategoryType.CATERING_SERVICE);
            categoryDTO.setCatalog(catalogDTO);
            categoryDTO.setPackageCode("1649645");
            categoryDTO.setPackageName("комплект/набор=1 услуга (раз)");
            categoryDTO.setOrigin(ProductOrigin.SERVICE);
            Long categoryId = categoryService.create(categoryDTO);
            ProductDTO productDTO = new ProductDTO();
            productDTO.setCategoryId(categoryId);
            productDTO.setBranchId(branchId);
            productDTO.setName(catalogDTO.getName());
            productDTO.setBarcode("123455678952");
            productDTO.setCatalogCode(catalogDTO.getCode());
            productDTO.setCatalogName(catalogDTO.getName());
            productDTO.setHasMark(false);
            productDTO.setPackageCode(categoryDTO.getPackageCode());
            productDTO.setPackageName(categoryDTO.getPackageName());
            productDTO.setUnitId(7L);
            productDTO.setVat(Vat.VAT_0);
            productDTO.setOrigin(ProductOrigin.SERVICE);
            productDTO.setHasTechCard(false);
            productService.create(productDTO);
            MenuDTO menuDTO = new MenuDTO();
            menuDTO.setBranchId(branchId);
            menuDTO.setName("Основное меню");
            menuDTO.setStatus(CommonStatus.ACTIVE);
            menuService.create(menuDTO);
        } else {
            branchId = branchService.update(branch.getId(), branchDTO);
        }
        return branchId;
    }
}
