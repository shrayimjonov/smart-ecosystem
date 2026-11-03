package uz.uzkassa.cts.dto.cases.caseproperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.domain.cases.CaseFile;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.dto.applicationfile.ApplicationTemplateDTO;
import uz.uzkassa.cts.dto.customer.CustomerBranchUpdateDTO;
import uz.uzkassa.cts.dto.customerbranch.rent.CustomerBranchRentDTO;
import uz.uzkassa.cts.dto.ofd.CustomerBranchSendDTO;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.ApplicationTemplateType;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqCustomerBranchSendProducer;
import uz.uzkassa.cts.repository.CaseFileRepository;
import uz.uzkassa.cts.repository.FiscalizationRepository;
import uz.uzkassa.cts.service.CustomerBranchOldService;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.FiscalizationService;
import uz.uzkassa.cts.service.OfdApplicationService;
import uz.uzkassa.cts.utils.FileUtils;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Component
public class CustomerBranchAddressProperty implements CaseAction {
    @Autowired
    @JsonIgnore
    private CustomerBranchOldService customerBranchOldService;
    @Autowired
    @JsonIgnore
    private OfdApplicationService ofdApplicationService;
    @Autowired
    @JsonIgnore
    private FiscalizationRepository fiscalizationRepository;
    @Autowired
    @JsonIgnore
    private FiscalizationService fiscalizationService;
    @Autowired
    @JsonIgnore
    private FileUploadService fileUploadService;
    @Autowired
    @JsonIgnore
    private CaseFileRepository caseFileRepository;
    @Autowired
    @JsonIgnore
    private RabbitMqCustomerBranchSendProducer rabbitMqCustomerBranchSendProducer;


    @NotNull
    private Long customerBranchId;
    @NotNull
    private Long customerId;
    @NotNull
    private Long deviceId;
    @NotBlank
    private String deviceSerialNumber;
    private BaseAddressDTO address;
    private String cadastreNumber;
    private CustomerBranchRentDTO rent;
    private Long activityTypeId;
    private String phone;
    private String name;
    private Long caseId;


    @Override
    public void accept() {
        //1 step: /api/customer/branches PUT
        updateCustomerBranch();

        // PODTVERJDENIYEE TT
        // api/ofd/customer/branch
        rabbitMqCustomerBranchSendProducer.sendServiceOrderInvoice(acceptTTToDto());
    }

    private void updateCustomerBranch() {
        customerBranchOldService.update(mapToDto());

        // 2 file UPLOAD
        // upload new file from caseFile to customer branch file
        List<UploadFileResponse> fileResponses = fileUploadService.list(this.customerBranchId, "CUSTOMER_BRANCH");
        for (UploadFileResponse fileRespons : fileResponses) {
            fileUploadService.deleteById(fileRespons.getId());
        }

        for (CaseFile file : caseFileRepository.findByCaseId(caseId)) {
            if (file.getDocumentType() != DocumentType.APPLICATION)
                try {
                    MultipartFile multipart = FileUtils.getMultipartFileFromUrl(file.getFileUrl());
                    fileUploadService.upload(multipart, customerBranchId, "CUSTOMER_BRANCH", file.getDocumentType().getCode());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    }


    private CustomerBranchSendDTO acceptTTToDto() {
        CustomerBranch branch = customerBranchOldService.findById(this.customerBranchId);
        CustomerBranchSendDTO dto = new CustomerBranchSendDTO();
        dto.setCustomerId(this.customerId);
        dto.setCustomerBranchId(this.customerBranchId);
        dto.setOfdId(branch.getOfdId());
        return dto;
    }

    private CustomerBranchUpdateDTO mapToDto() {
        CustomerBranchUpdateDTO dto = new CustomerBranchUpdateDTO();
        dto.setId(this.customerBranchId);
        dto.setName(this.name);
        dto.setCadastreNumber(this.cadastreNumber);
        if (this.rent != null) {
            CustomerBranchRentDTO rentDTO = this.rent;
            rentDTO.setCustomerBranchId(this.customerBranchId);
            dto.setRent(rentDTO);
        }
        dto.setPhone(this.phone);
        dto.setActivityTypeId(this.activityTypeId);
        dto.setAddress(mapAddressUpdateDto());
        return dto;
    }

    private BaseAddressDTO mapAddressUpdateDto() {
        BaseAddressDTO dto = new BaseAddressDTO();
        dto.setApartment(this.address.getApartment());
        dto.setHouse(this.address.getHouse());
        dto.setStreet(this.address.getStreet());
        dto.setRegionId(this.address.getRegionId());
        dto.setDistrictId(this.address.getDistrictId());
        dto.setLatitude(this.address.getLatitude());
        dto.setLongitude(this.address.getLongitude());
        dto.setNeighborhoodId(this.address.getNeighborhoodId());
        return dto;
    }

    @Override
    public List<String> generateTemplate() {
        return Collections.singletonList(fiscalizationService.downloadApplicationTemplate(mapToTemplateDto()));
    }

    private ApplicationTemplateDTO mapToTemplateDto() {
        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndCustomerBranchIdAndDeviceId(this.customerId, this.customerBranchId, this.deviceId);
        if (fiscalization == null) {
            throw new BadRequestException("Fiscalization not found");
        }
        ApplicationTemplateDTO applicationTemplateDTO = new ApplicationTemplateDTO();
        applicationTemplateDTO.setAddress(mapAddressDto());
        applicationTemplateDTO.setFiscalizationId(fiscalization.getId());
        applicationTemplateDTO.setTemplateType(ApplicationTemplateType.CHANGE_CUSTOMER_BRANCH_ADDRESS);
        return applicationTemplateDTO;
    }

    private BaseAddressDTO mapAddressDto() {
        BaseAddressDTO dto = new BaseAddressDTO();
        dto.setApartment(this.address.getApartment());
        dto.setHouse(this.address.getHouse());
        dto.setStreet(this.address.getStreet());
        dto.setRegionId(this.address.getRegionId());
        dto.setDistrictId(this.address.getDistrictId());
        return dto;
    }

    @Override
    @JsonIgnore
    public boolean is2Step() {
        return false;
    }
}
