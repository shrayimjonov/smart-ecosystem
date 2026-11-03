package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.menu.Menu;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.qrmenu.QrMenuCompanyDetails;
import uz.uzkassa.horeca.dto.qrmenu.QrMenuProducts;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.repository.CompanyDetailRepository;
import uz.uzkassa.horeca.repository.CompanyRepository;
import uz.uzkassa.horeca.repository.MenuItemRepository;
import uz.uzkassa.horeca.repository.MenuRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Slf4j
public class QrMenuService extends BaseService {
    private final CompanyRepository companyRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;
    private final CompanyDetailRepository companyDetailRepository;

    private Long parseAndGetCompanyId(String uri, int index) {
        if (StringUtils.isBlank(uri)) {
            throw new IllegalArgumentException("uri cannot be empty");
        }
        String string = new String(Base64.decodeBase64(uri.getBytes(StandardCharsets.UTF_8)));
        String[] split = string.split(":");
        if (StringUtils.isBlank(split[index])) {
            throw new IllegalArgumentException("uri must contain company id");
        }
        return Long.parseLong(split[index]);
    }

    public QrMenuCompanyDetails getCompanyDetails(String uri) {
        Long companyId = parseAndGetCompanyId(uri, 0);
        Company company = companyRepository.findById(companyId).orElseThrow(notFoundExceptionThrow(Company.class.getSimpleName(), "id", companyId));
        QrMenuCompanyDetails qrMenuCompanyDetails = new QrMenuCompanyDetails();
        qrMenuCompanyDetails.setName(company.getName());
        qrMenuCompanyDetails.setAddress(addressMapper.toDTO(company.getAddress()));
        qrMenuCompanyDetails.setContactName(company.getContactName());
        qrMenuCompanyDetails.setContactPhone(company.getContactPhone());
        qrMenuCompanyDetails.setBrand(company.getBrand());
        qrMenuCompanyDetails.setWifiPassword(company.getWifiPassword());
        companyDetailRepository.findFirstByCompanyId(companyId).ifPresent(companyDetail -> {
            if (companyDetail.getLogoId() != null) {
                qrMenuCompanyDetails.setLogo(companyDetail.getLogo().toFileDTO());
            }
            if (companyDetail.getBackgroundPhotoId() != null) {
                qrMenuCompanyDetails.setBackground(companyDetail.getBackgroundPhoto().toFileDTO());
            }
        });
        return qrMenuCompanyDetails;
    }

    public List<CommonDTO> getCategories(String uri) {
        Long menuId = parseAndGetCompanyId(uri, 1);
        return menuRepository.findAllByParentIdAndStatus(menuId, CommonStatus.ACTIVE).stream()
                .map(Menu::toCommonDTO).toList();
    }

    public List<QrMenuProducts> getProducts(String uri) {
        Long menuId = parseAndGetCompanyId(uri, 1);
        List<Long> menuIds = menuRepository.getIdAllByParentIdAndStatus(menuId, CommonStatus.ACTIVE);
        return menuItemRepository.findAllByMenuIdInAndStatusAndDeletedFalseOrderByMenuIdAsc(menuIds, CommonStatus.ACTIVE)
                .stream().map(menuItem -> {
                    QrMenuProducts qrMenuProducts = new QrMenuProducts();
                    qrMenuProducts.setMenuId(menuItem.getMenuId());
                    qrMenuProducts.setName(menuItem.getName());
                    qrMenuProducts.setDescription(menuItem.getProduct().getDescription());
                    if (menuItem.getPhotoId() != null) {
                        qrMenuProducts.setPhoto(menuItem.getPhoto().toFileDTO());
                        if (!CollectionUtils.isEmpty(menuItem.getPhoto().getChildren())) {
                            qrMenuProducts.setPhotos(menuItem.getPhoto().getChildren().stream().map(FileUpload::toFileDTO).collect(Collectors.toList()));
                        }
                    }
                    return qrMenuProducts;
                }).toList();
    }
}
