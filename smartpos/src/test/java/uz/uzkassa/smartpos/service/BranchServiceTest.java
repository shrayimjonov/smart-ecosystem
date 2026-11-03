package uz.uzkassa.smartpos.service;

import org.apache.commons.collections4.CollectionUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.BranchGroup;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyPermission;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.BranchGroupDTO;
import uz.uzkassa.smartpos.dto.CompanyRegisterBranchesDTO;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.dto.branch.BranchDTO;
import uz.uzkassa.smartpos.dto.branch.CabinetBranchCreateDto;
import uz.uzkassa.smartpos.dto.customerreceipts.BranchReceiptsDTO;
import uz.uzkassa.smartpos.dto.filter.receipt.CustomerReceiptsFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.enums.DeviceType;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.BranchNotFoundException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.filter.BranchFilter;
import uz.uzkassa.smartpos.mappers.BranchMapper;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptReportByBranch;
import uz.uzkassa.smartpos.repository.mongo.AuditLogRepository;
import uz.uzkassa.smartpos.repository.postgres.company.BranchGroupRepository;
import uz.uzkassa.smartpos.repository.postgres.company.BranchRepository;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyRepository;
import uz.uzkassa.smartpos.repository.postgres.terminal.DeviceRepository;
import uz.uzkassa.smartpos.repository.postgres.user.UserRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {
    @Mock
    CompanyRepository companyRepository;
    @Mock
    BranchRepository branchRepository;
    @Mock
    DeviceRepository deviceRepository;
    @Spy
    @InjectMocks
    BranchService branchService;
    @Mock
    LocalizationService localizationService;
    @Mock
    ClickhouseReceiptReportByBranch clickhouseReceiptReportByBranch;
    @Mock
    BranchGroupRepository branchGroupRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    BranchMapper branchMapper;
    @Mock
    RabbitMqProducer rabbitMqProducer;
    @Mock
    CompanyService companyService;

    @Test
    @Transactional
    void updateBranchWhenFiscal() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);

        CabinetBranchCreateDto cabinetBranchCreateDto = new CabinetBranchCreateDto(
                1L, null, null, null, null, null, null, null, null, null, null
        );
        Branch branch = new Branch();
        branch.setId(1L);
        branch.setFiscal(true);

        when(localizationService.getMessage(anyString())).thenAnswer(i -> i.getArguments()[0].toString());
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        BadRequestException exception = assertThrows(BadRequestException.class, () -> branchService.update(cabinetBranchCreateDto));
        assertEquals("fiscal.branch.edit", exception.getMessage());

        verify(branchRepository, times(1)).findById(1L);
        verify(localizationService, times(1)).getMessage("fiscal.branch.edit");
        verify(localizationService, times(1)).getMessage("branch");
        verifyNoMoreInteractions(branchMapper, branchRepository, companyRepository, rabbitMqProducer);
    }

    @Test
    @Transactional
    void updateBranchWhenAdminCompany() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);

        Field companyField = SmartposService.class.getDeclaredField("companyRepository");
        companyField.setAccessible(true);
        companyField.set(branchService, companyRepository);

        CabinetBranchCreateDto cabinetBranchCreateDto = new CabinetBranchCreateDto(
                1L, null, null, null, null, null, null, null, null, null, null
        );
        Branch branch = new Branch();
        branch.setId(1L);
        branch.setCompanyId(1L);

        Company company = new Company();
        company.setId(1L);
        company.setAdmin(true);

        when(localizationService.getMessage(anyString())).thenAnswer(i -> i.getArguments()[0].toString());
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        BadRequestException exception = assertThrows(BadRequestException.class, () -> branchService.update(cabinetBranchCreateDto));
        assertEquals("admin.company.branch.creation.not.allowed", exception.getMessage());

        verify(branchRepository, times(1)).findById(1L);
        verify(companyRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(branchMapper, branchRepository, companyRepository, rabbitMqProducer);

    }

    @Test
    void whenRegisterBranches() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);

        Field companyField = SmartposService.class.getDeclaredField("companyRepository");
        companyField.setAccessible(true);
        companyField.set(branchService, companyRepository);
        try (MockedStatic<BeanUtil> beanUtilMockedStatic = mockStatic(BeanUtil.class)) {
            beanUtilMockedStatic.when(() -> BeanUtil.getBean(AuditLogRepository.class)).thenReturn(mock(AuditLogRepository.class));
            CompanyPermission permission = new CompanyPermission();
            permission.setServiceType(ServiceType.RECEIPT_FROM_INVOICE);
            permission.setEndDate(LocalDateTime.now().plusDays(1));

            Long COMPANY_ID = 1L;
            Company company = new Company();
            company.setId(COMPANY_ID);
            company.setCompanyPermissions(new HashSet<>(Collections.singletonList(permission)));

            CompanyRegisterBranchesDTO registerBranchesDTO = new CompanyRegisterBranchesDTO(COMPANY_ID, Arrays.asList(1L, 2L));
            when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.of(company));

            Branch branch = new Branch();
            branch.setId(1L);
            branch.setCompanyId(COMPANY_ID);

            Branch branch1 = new Branch();
            branch1.setId(2L);
            branch1.setCompanyId(COMPANY_ID);
            List<Branch> branches = Arrays.asList(branch, branch1);

            when(branchRepository.findAllByIdInAndCompanyId(registerBranchesDTO.getBranchIds(), COMPANY_ID)).thenReturn(branches);
            when(branchRepository.nextVal()).thenReturn(12345);
            doNothing().when(companyService).sendToApay(any(CompanyRegisterBranchesDTO.class));
            branchService.registerBranches(registerBranchesDTO);

            verify(companyRepository, times(1)).findById(COMPANY_ID);
            verify(branchRepository, times(1)).findAllByIdInAndCompanyId(registerBranchesDTO.getBranchIds(), COMPANY_ID);
            verify(companyService, times(1)).sendToApay(any(CompanyRegisterBranchesDTO.class));
            verify(branchRepository, times(1)).saveAll(argThat(argument -> {
                AtomicInteger count = new AtomicInteger();
                argument.forEach(branch2 -> {
                    assertTrue(branch2.isFiscal());
                    assertEquals("№ - 0012345", branch2.getSerialNumber());
                    count.getAndIncrement();
                });
                return count.get() == 2;
            }));
        }
    }

    @Test
    void whenRegisterBranchedThrow() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);

        Field companyField = SmartposService.class.getDeclaredField("companyRepository");
        companyField.setAccessible(true);
        companyField.set(branchService, companyRepository);

        Long COMPANY_ID = 1L;
        Company company = new Company();
        company.setId(COMPANY_ID);
        company.setCompanyPermissions(new HashSet<>());

        CompanyRegisterBranchesDTO companyRegisterBranchesDTO = new CompanyRegisterBranchesDTO(COMPANY_ID, Collections.singletonList(1L));

        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.of(company));
        when(localizationService.getMessage(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        BadRequestException exception = assertThrows(BadRequestException.class, () -> branchService.registerBranches(companyRegisterBranchesDTO));
        assertEquals("feature.not.allowed", exception.getMessage());

        verify(branchRepository, never()).findAllByIdInAndCompanyId(any(), any());
        verify(companyService, never()).sendToApay(any());
        verify(branchRepository, never()).saveAll(any());

    }

    @Test
    @Transactional
    void whenUpdateBranchGroup() throws NoSuchFieldException, IllegalAccessException {

        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Long branchGroupId = 1L;
        String updatedName = "Update Branch Group";
        Long BRANCH_ID1 = 10L;
        Long BRANCH_ID2 = 20L;
        Long BRANCH_ID3 = 30L;

        Branch branch1 = new Branch();
        branch1.setId(BRANCH_ID1);
        branch1.setGroups(null);

        Branch branch2 = new Branch();
        branch2.setId(BRANCH_ID2);
        branch2.setGroups(null);

        Branch branch3 = new Branch();
        branch3.setId(BRANCH_ID3);
        branch3.setGroups(null);

        BranchGroup branchGroup = new BranchGroup();
        branchGroup.setId(branchGroupId);
        branchGroup.setName("BranchGroup");

        BranchGroupDTO branchGroupDTO = new BranchGroupDTO();
        branchGroupDTO.setId(branchGroupId);
        branchGroupDTO.setName(updatedName);
        branchGroupDTO.setBranches(List.of(new SelectItem(BRANCH_ID1, "Branch1"), new SelectItem(BRANCH_ID2, "Branch2")));

        when(branchGroupRepository.getReferenceById(branchGroupId)).thenReturn(branchGroup);
        when(branchRepository.findAllById(anyList())).thenReturn(List.of(branch1, branch2, branch3));
        BranchGroupDTO result = branchService.updateBranchGroup(branchGroupDTO);

        assertNotNull(result);
        assertEquals(branchGroupId, result.getId());
        assertEquals(updatedName, result.getName());
        assertEquals(3, result.getBranches().size());

        verify(branchGroupRepository).save(argThat(argument -> argument.getId().equals(branchGroupId)
                && argument.getName().equals(updatedName)));

//        verify(branchRepository).saveAll(argThat(argument -> {
//            List<Branch> branches = IterableUtils.toList(argument);
//            var removedBranchFromGroup = branches.stream().filter(branch -> branch.getGroups() == null).findFirst();
//            assertTrue(removedBranchFromGroup.isPresent());
//            assertEquals(BRANCH_ID3, removedBranchFromGroup.get().getId());
//
//            assertEquals(2, branches.stream().filter(branch -> branch.getGroups() != null).count());
//
//            return true;
//        }));

    }

    @Test
    @Transactional
    void testDeleteBranch_Success() throws NoSuchFieldException, IllegalAccessException, EntityNotFound {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);

        Field companyField = SmartposService.class.getDeclaredField("companyRepository");
        companyField.setAccessible(true);
        companyField.set(branchService, companyRepository);

        User user = new User();
        user.setPassword("encryptedPassword");

        BranchDTO branchDTO = new BranchDTO();
        branchDTO.setId(1L);
        branchDTO.setPassword("password");

        Branch branch = new Branch();
        branch.setId(1L);
        branch.setCompanyId(1L);
        branch.setFiscal(false);

        Company company = new Company();
        company.setId(1L);
        company.setAdmin(false);

        when(branchService.getCurrentUserId()).thenReturn(2L);
        when(branchService.getCurrentCompanyId()).thenReturn(1L);
        when(userRepository.getReferenceById(anyLong())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(branchRepository.findById(anyLong())).thenReturn(Optional.of(branch));
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));
        when(localizationService.getMessage(anyString())).thenReturn("message");
        branchService.deleteBranch(branchDTO);
        verify(branchRepository, times(1)).save(argThat(Branch::isDeleted));
    }

    @Test
    @Transactional
    void testDeleteBranch_InvalidPassword() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);

        Field companyField = SmartposService.class.getDeclaredField("companyRepository");
        companyField.setAccessible(true);
        companyField.set(branchService, companyRepository);

        try (MockedStatic<BeanUtil> beanUtilMockedStatic = mockStatic(BeanUtil.class)) {
            beanUtilMockedStatic.when(() -> BeanUtil.getBean(AuditLogRepository.class)).thenReturn(mock(AuditLogRepository.class));
            User user = new User();
            user.setId(1L);
            user.setPassword("password");

            BranchDTO branchDTO = new BranchDTO();
            branchDTO.setId(1L);
            branchDTO.setPassword("wrong");

            when(branchService.getCurrentUserId()).thenReturn(2L);
            when(userRepository.getReferenceById(anyLong())).thenReturn(user);
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
            assertThrows(BadRequestException.class, () -> branchService.deleteBranch(branchDTO));

        }
    }

    @Test
    @Transactional
    void when_delete_branch_notfound() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);

        Field companyField = SmartposService.class.getDeclaredField("companyRepository");
        companyField.setAccessible(true);
        companyField.set(branchService, companyRepository);

        try (MockedStatic<BeanUtil> beanUtilMockedStatic = mockStatic(BeanUtil.class)) {
            beanUtilMockedStatic.when(() -> BeanUtil.getBean(AuditLogRepository.class)).thenReturn(mock(AuditLogRepository.class));
            User user = new User();
            user.setId(1L);
            user.setPassword("password");

            BranchDTO branchDTO = new BranchDTO();
            branchDTO.setId(1L);
            branchDTO.setPassword("wrong");

            when(userRepository.getReferenceById(anyLong())).thenReturn(user);
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(branchRepository.findById(anyLong())).thenReturn(Optional.empty());
            when(branchService.getCurrentUserId()).thenReturn(2L);
            assertThrows(EntityNotFound.class, () -> branchService.deleteBranch(branchDTO));
        }
    }

    @Test
    @Transactional
    void deleteBranchFiscal() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);

        Field companyField = SmartposService.class.getDeclaredField("companyRepository");
        companyField.setAccessible(true);
        companyField.set(branchService, companyRepository);
        try (MockedStatic<BeanUtil> beanUtilMockedStatic = mockStatic(BeanUtil.class)) {
            beanUtilMockedStatic.when(() -> BeanUtil.getBean(AuditLogRepository.class)).thenReturn(mock(AuditLogRepository.class));
            User user = new User();
            user.setId(1L);
            user.setPassword("password");

            BranchDTO branchDTO = new BranchDTO();
            branchDTO.setId(1L);
            branchDTO.setPassword("wrong");

            Branch branch = new Branch();
            branch.setId(1L);
            branch.setCompanyId(1L);
            branch.setFiscal(true);

            when(branchService.getCurrentUserId()).thenReturn(2L);
            when(branchService.getCurrentCompanyId()).thenReturn(1L);
            when(userRepository.getReferenceById(anyLong())).thenReturn(user);
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(branchRepository.findById(anyLong())).thenReturn(Optional.of(branch));
            assertThrows(BadRequestException.class, () -> branchService.deleteBranch(branchDTO));
        }
    }

    @Test
    @Transactional
    void deleteBranchAdminCompany() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);

        Field companyField = SmartposService.class.getDeclaredField("companyRepository");
        companyField.setAccessible(true);
        companyField.set(branchService, companyRepository);
        try (MockedStatic<BeanUtil> beanUtilMockedStatic = mockStatic(BeanUtil.class)) {
            beanUtilMockedStatic.when(() -> BeanUtil.getBean(AuditLogRepository.class)).thenReturn(mock(AuditLogRepository.class));
            User user = new User();
            user.setId(1L);
            user.setPassword("password");

            BranchDTO branchDTO = new BranchDTO();
            branchDTO.setId(1L);
            branchDTO.setPassword("wrong");

            Branch branch = new Branch();
            branch.setId(1L);
            branch.setCompanyId(1L);
            branch.setFiscal(false);

            Company company = new Company();
            company.setId(1L);
            company.setAdmin(true);

            when(branchService.getCurrentUserId()).thenReturn(2L);
            when(branchService.getCurrentCompanyId()).thenReturn(1L);
            when(userRepository.getReferenceById(anyLong())).thenReturn(user);
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(branchRepository.findById(anyLong())).thenReturn(Optional.of(branch));
            when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));
            assertThrows(BadRequestException.class, () -> branchService.deleteBranch(branchDTO));
        }
    }

    @Test
    @Transactional
    void deleteBranchCompanyCompare() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleFile = BaseService.class.getDeclaredField("localizationService");
        bundleFile.setAccessible(true);
        bundleFile.set(branchService, localizationService);

        Long BRANCH_ID = Instancio.create(Long.class);
        Long COMPANY_ID = Instancio.create(Long.class);
        Branch branch = new Branch();
        branch.setId(BRANCH_ID);
        branch.setCompanyId(COMPANY_ID);
        branch.setFiscal(false);
        branch.setDeleted(false);
//        branch.setGroupId(2L);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
        when(branchService.getCurrentCompanyId()).thenReturn(Instancio.create(Long.class));
        assertThrows(BadRequestException.class, () -> branchService.delete(BRANCH_ID));
        verify(branchService, times(1)).delete(BRANCH_ID);

    }

    @Test
    @Transactional
    void deleteFiscalBranch() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);

        Long BRANCH_ID = Instancio.create(Long.class);
        Long COMPANY_ID = Instancio.create(Long.class);

        Branch branch = new Branch();
        branch.setId(BRANCH_ID);
        branch.setCompanyId(COMPANY_ID);
        branch.setFiscal(true);
        branch.setDeleted(false);
//        branch.setGroupId(2L);

        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
        when(branchService.getCurrentCompanyId()).thenReturn(COMPANY_ID);
        assertThrows(BadRequestException.class, () -> branchService.delete(BRANCH_ID));
        verify(branchService, times(1)).delete(BRANCH_ID);
    }

    @Test
    @Transactional
    void deleteAdminCompanyBranch() throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = SmartposService.class.getDeclaredField("branchRepository");
        declaredField.setAccessible(true);
        declaredField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);

        Field companyField = SmartposService.class.getDeclaredField("companyRepository");
        companyField.setAccessible(true);
        companyField.set(branchService, companyRepository);

        Long BRANCH_ID = Instancio.create(Long.class);
        Long COMPANY_iD = Instancio.create(Long.class);

        Branch branch = new Branch();
        branch.setId(BRANCH_ID);
        branch.setCompanyId(COMPANY_iD);
        branch.setFiscal(false);
        branch.setDeleted(false);
//        branch.setGroupId(2L);

        Company company = new Company();
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
        when(branchService.getCurrentCompanyId()).thenReturn(COMPANY_iD);
        when(companyRepository.findById(COMPANY_iD)).thenReturn(Optional.of(company));
        when(branchService.isAdminCompany(company)).thenReturn(true);
        assertThrows(BadRequestException.class, () -> branchService.delete(BRANCH_ID));
        verify(branchService, times(1)).delete(BRANCH_ID);
    }

    @Test
    @Transactional
    void deleteBranchSuccess() throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = SmartposService.class.getDeclaredField("branchRepository");
        declaredField.setAccessible(true);
        declaredField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);

        Field fieldCompany = SmartposService.class.getDeclaredField("companyRepository");
        fieldCompany.setAccessible(true);
        fieldCompany.set(branchService, companyRepository);

        Branch branch = new Branch();
        Long BRANCH_ID = Instancio.create(Long.class);
        Long COMPANY_ID = Instancio.create(Long.class);

        branch.setId(BRANCH_ID);
        branch.setCompanyId(COMPANY_ID);
        branch.setFiscal(false);
        branch.setDeleted(false);

        Company company = new Company();
        company.setId(COMPANY_ID);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
        when(branchService.getCurrentCompanyId()).thenReturn(COMPANY_ID);
        when(companyRepository.findById(company.getId())).thenReturn(Optional.of(company));
        when(branchService.isAdminCompany(company)).thenReturn(false);

        branchService.delete(BRANCH_ID);
        verify(branchRepository, times(1)).save(argThat(argument ->
                argument.isDeleted() && argument.getGroups() == null));
        verifyNoMoreInteractions(branchRepository);
    }

    @Test
    void forceDelete() throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = SmartposService.class.getDeclaredField("branchRepository");
        declaredField.setAccessible(true);
        declaredField.set(branchService, branchRepository);
        Field bundleMessage = BaseService.class.getDeclaredField("localizationService");
        bundleMessage.setAccessible(true);
        bundleMessage.set(branchService, localizationService);
        try (MockedStatic<BeanUtil> beanUtilMockedStatic = mockStatic(BeanUtil.class)) {
            beanUtilMockedStatic.when(() -> BeanUtil.getBean(AuditLogRepository.class)).thenReturn(mock(AuditLogRepository.class));
            Branch branch = new Branch();
            branch.setId(2L);
            branch.setDeleted(false);
            branch.setFiscal(true);
            branch.setSerialNumber("test");
            when(branchRepository.findById(2L)).thenReturn(Optional.of(branch));
            branchService.forceDelete(2L);
            verify(branchRepository).save(argThat(argument -> {
                var deleted = branch.isDeleted();
                var notFiscal = !branch.isFiscal();
                var serialNumberNull = branch.getSerialNumber() == null;
                return deleted &&
                        notFiscal &&
                        serialNumberNull;
            }));
        }
    }

    @Test
    @Transactional
    void deleteWebkassa() throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = SmartposService.class.getDeclaredField("branchRepository");
        declaredField.setAccessible(true);
        declaredField.set(branchService, branchRepository);
        Field bundleMessage = BaseService.class.getDeclaredField("localizationService");
        bundleMessage.setAccessible(true);
        bundleMessage.set(branchService, localizationService);
        try (MockedStatic<BeanUtil> beanUtilMockedStatic = mockStatic(BeanUtil.class)) {
            beanUtilMockedStatic.when(() -> BeanUtil.getBean(AuditLogRepository.class)).thenReturn(mock(AuditLogRepository.class));
            Branch branch = new Branch();
            branch.setId(2L);
            branch.setSerialNumber("test");
            branch.setCashboxCreatedDate(LocalDateTime.now());
            when(branchRepository.findById(2L)).thenReturn(Optional.of(branch));
            branchService.deleteWebkassa(2L);
            verify(branchRepository).save(argThat(argument ->
                    argument.getSerialNumber() == null &&
                            argument.getCashboxCreatedDate() == null));
        }
    }

    @Test
    void when_currentBranchSerialNotEmpty() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);
        try (MockedStatic<BeanUtil> beanUtilMockedStatic = mockStatic(BeanUtil.class)) {
            beanUtilMockedStatic.when(() -> BeanUtil.getBean(AuditLogRepository.class)).thenReturn(mock(AuditLogRepository.class));
            try (MockedStatic<SecurityUtils> securityUtilsMockedStatic = mockStatic(SecurityUtils.class)) {
                securityUtilsMockedStatic.when(SecurityUtils::getCurrentBranchId).thenReturn(1L);
                String SERIAL = "serial123";
                Long COMPANY_ID = 1L;
                Branch branch = new Branch();
                branch.setCompanyId(COMPANY_ID);
                branch.setId(1L);
                branch.setName("Branch1");

                BranchDTO branchDTO = new BranchDTO();
                branchDTO.setId(1L);
                branchDTO.setName("Branch1");

                Device terminal = new Device();
                terminal.setSerialNumber(SERIAL);
                terminal.setBranch(branch);

                when(branchService.getCurrentTerminalSnByHeader()).thenReturn(SERIAL);
                when(deviceRepository.findFirstBySerialNumberAndType(SERIAL, DeviceType.FISCAL)).thenReturn(Optional.of(terminal));
                when(branchMapper.toDto(branch)).thenReturn(branchDTO);
                when(branchService.getCurrentCompanyId()).thenReturn(COMPANY_ID);
                when(branchService.isOwner()).thenReturn(false);

                assertNotNull(branchService.getCurrentBranch());
                verify(deviceRepository, times(1)).findFirstBySerialNumberAndType(SERIAL, DeviceType.FISCAL);
            }
        }
    }

    @Test
    void when_currentSerialNotEmptyThrowException() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);
        String serial = "serial123";

        when(branchService.getCurrentTerminalSnByHeader()).thenReturn(serial);
        when(deviceRepository.findFirstBySerialNumberAndType(serial, DeviceType.FISCAL)).thenReturn(Optional.empty());
        assertThrows(BranchNotFoundException.class, () -> branchService.getCurrentBranch());
    }

    @Test
    void when_currentOwnerAndBranchThrowExample() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);
        try (MockedStatic<BeanUtil> beanUtilMockedStatic = mockStatic(BeanUtil.class)) {
            beanUtilMockedStatic.when(() -> BeanUtil.getBean(AuditLogRepository.class)).thenReturn(mock(AuditLogRepository.class));
            String serial = "serial123";
            Long COMPANY_ID = 1L;
            Branch branch = new Branch();
            branch.setCompanyId(COMPANY_ID);
            branch.setId(1L);
            branch.setName("Branch1");

            BranchDTO branchDTO = new BranchDTO();
            branchDTO.setId(1L);
            branchDTO.setName("Branch1");

            Device terminal = new Device();
            terminal.setSerialNumber(serial);
            terminal.setBranch(branch);

            when(branchService.getCurrentTerminalSnByHeader()).thenReturn(serial);
            when(deviceRepository.findFirstBySerialNumberAndType(serial, DeviceType.FISCAL)).thenReturn(Optional.of(terminal));
            when(branchMapper.toDto(branch)).thenReturn(branchDTO);
            when(branchService.getCurrentCompanyId()).thenReturn(COMPANY_ID);
            when(branchService.isOwner()).thenReturn(false);
            assertThrows(BadRequestException.class, () -> branchService.getCurrentBranch());
        }
    }

    @Test
    void when_currentReturnBranchFromId() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);
        try (MockedStatic<BeanUtil> beanUtilMockedStatic = mockStatic(BeanUtil.class)) {
            beanUtilMockedStatic.when(() -> BeanUtil.getBean(AuditLogRepository.class)).thenReturn(mock(AuditLogRepository.class));
            Long BRANCH_ID = 2L;
            Long COMPANY_ID = 1L;
            Branch branch = new Branch();
            branch.setCompanyId(COMPANY_ID);
            branch.setId(BRANCH_ID);
            branch.setName("Branch2");

            BranchDTO branchDTO = new BranchDTO();
            branchDTO.setId(BRANCH_ID);
            branchDTO.setName("Branch2");

            when(branchService.getCurrentTerminalSnByHeader()).thenReturn("");
            when(branchService.getCurrentBranchId()).thenReturn(BRANCH_ID);
            when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
            when(branchMapper.toDto(branch)).thenReturn(branchDTO);
            when(branchService.getCurrentCompanyId()).thenReturn(COMPANY_ID);
            assertNotNull(branchService.getCurrentBranch());
            verify(branchRepository, times(1)).findById(BRANCH_ID);
        }
    }

    @Test
    void when_currentBranchNotFoundByIdThrowException() throws NoSuchFieldException, IllegalAccessException {
        Field branchField = SmartposService.class.getDeclaredField("branchRepository");
        branchField.setAccessible(true);
        branchField.set(branchService, branchRepository);

        Field bundleField = BaseService.class.getDeclaredField("localizationService");
        bundleField.setAccessible(true);
        bundleField.set(branchService, localizationService);
        Long BRANCH_ID = 1L;
        when(branchService.getCurrentTerminalSnByHeader()).thenReturn("");
        when(branchService.getCurrentBranchId()).thenReturn(BRANCH_ID);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.empty());
        assertThrows(BranchNotFoundException.class, () -> branchService.getCurrentBranch());
    }

    @Test
    void findAll_When_Owner() throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = SmartposService.class.getDeclaredField("branchRepository");
        declaredField.setAccessible(true);
        declaredField.set(branchService, branchRepository);

        BranchFilter branchFilter = new BranchFilter();
        final Long COMPANY_ID = Instancio.gen().longs().get();
        when(branchService.isOwner()).thenReturn(Boolean.TRUE);
        when(branchService.getCurrentCompanyId()).thenReturn(COMPANY_ID);
        branchService.findAll(branchFilter);
        verify(branchRepository, times(1)).findAllByFilter(argThat(argument ->
                Objects.equals(argument.getCompanyId(), COMPANY_ID) &&
                        CollectionUtils.isEmpty(argument.getBranchIds()) &&
                        CollectionUtils.isEmpty(argument.getBranchGroupIds())
        ));
    }

    @Test
    void findAll_when_BranchAdmin() throws NoSuchFieldException, IllegalAccessException {
        Field field = SmartposService.class.getDeclaredField("branchRepository");
        field.setAccessible(true);
        field.set(branchService, branchRepository);

        BranchFilter branchFilter = new BranchFilter();
        branchFilter.setCompanyId(1L);
        when(branchService.getCurrentCompanyId()).thenReturn(1L);
        when(branchService.isOwner()).thenReturn(false);
        when(branchService.isBranchAdmin()).thenReturn(true);
        Long expectedBranchGroupId = Instancio.gen().longs().get();
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentBranchGroupId).thenReturn(Optional.of(expectedBranchGroupId));
            branchService.findAll(branchFilter);

            verify(branchRepository, times(1))
                    .findAllByFilter(argThat(argument ->
                            argument.getBranchGroupIds().contains(expectedBranchGroupId) &&
                                    Objects.equals(argument.getCompanyId(), branchFilter.getCompanyId())));
        }
    }

    @Test
    void findAll_when_Cashier() throws IllegalAccessException, NoSuchFieldException {
        Field declaredField = SmartposService.class.getDeclaredField("branchRepository");
        declaredField.setAccessible(true);
        declaredField.set(branchService, branchRepository);

        BranchFilter branchFilter = new BranchFilter();
        branchFilter.setCompanyId(1L);
        when(branchService.getCurrentCompanyId()).thenReturn(1L);
        when(branchService.isOwner()).thenReturn(false);
        when(branchService.isBranchAdmin()).thenReturn(false);
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            Long expectedBranchId = Instancio.gen().longs().get();
            securityUtils.when(SecurityUtils::getCurrentBranchId).thenReturn(expectedBranchId);
            branchService.findAll(branchFilter);
            verify(branchRepository, times(1)).findAllByFilter(argThat(argument ->
                    Objects.equals(argument.getCompanyId(), branchFilter.getCompanyId()) &&
                            argument.getBranchIds().size() == 1 &&
                            argument.getBranchIds().contains(expectedBranchId)));
        }
    }

    @Test
    void search_When_Admin() throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = SmartposService.class.getDeclaredField("branchRepository");
        declaredField.setAccessible(true);
        declaredField.set(branchService, branchRepository);
        BranchFilter filter = new BranchFilter();
        filter.setForAdmin(true);
        filter.setCompanyId(Instancio.gen().longs().get());
        branchService.search(filter);
        verify(branchRepository, times(1)).lookup(argThat(argument -> Objects.equals(argument.getCompanyId(), filter.getCompanyId())));
    }

    @Test
    void search_When_Owner() throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = SmartposService.class.getDeclaredField("branchRepository");
        declaredField.setAccessible(true);
        declaredField.set(branchService, branchRepository);
        BranchFilter filter = new BranchFilter();
        filter.setForAdmin(false);
        when(branchService.getCurrentCompanyId()).thenReturn(2L);
        when(branchService.isOwner()).thenReturn(true);
        when(branchService.isBranchAdmin()).thenReturn(false);
        branchService.search(filter);
        verify(branchRepository, times(1)).lookup(argThat(argument -> argument.getCompanyId().equals(2L)));
    }

    @Test
    void search_When_BranchAdmin() throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = SmartposService.class.getDeclaredField("branchRepository");
        declaredField.setAccessible(true);
        declaredField.set(branchService, branchRepository);
        BranchFilter filter = new BranchFilter();
        filter.setForAdmin(false);
        try (var securityUtil = mockStatic(SecurityUtils.class)) {
            when(branchService.getCurrentCompanyId()).thenReturn(2L);
            when(branchService.isBranchAdmin()).thenReturn(true);
            securityUtil.when(SecurityUtils::getCurrentBranchGroupId).thenReturn(Optional.of(1L));
            when(branchService.isOwner()).thenReturn(false);
            branchService.search(filter);
            verify(branchRepository, times(1)).lookup(argThat(argument ->
                    argument.getCompanyId().equals(2L) &&
                            argument.getBranchGroupIds().size() == 1 &&
                            argument.getBranchGroupIds().contains(1L)));
        }
    }

    @Test
    void search_When_Cashier() throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = SmartposService.class.getDeclaredField("branchRepository");
        declaredField.setAccessible(true);
        declaredField.set(branchService, branchRepository);
        BranchFilter filter = new BranchFilter();
        filter.setForAdmin(false);
        final var COMPANY_ID = Instancio.gen().longs().get();
        final var BRANCH_ID = Instancio.gen().longs().get();
        when(branchService.getCurrentCompanyId()).thenReturn(COMPANY_ID);
        when(branchService.isBranchAdmin()).thenReturn(false);
        when(branchService.isOwner()).thenReturn(false);
        when(branchService.getCurrentBranchId()).thenReturn(BRANCH_ID);
        branchService.search(filter);
        verify(branchRepository, times(1)).lookup(argThat(argument ->
                argument.getCompanyId().equals(COMPANY_ID) &&
                        argument.getBranchIds().size() == 1 &&
                        argument.getBranchIds().contains(BRANCH_ID)));
    }

    @Test
    void getReceiptsAmountByBranch() throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = SmartposService.class.getDeclaredField("branchRepository");
        declaredField.setAccessible(true);
        declaredField.set(branchService, branchRepository);
        try (MockedStatic<BeanUtil> beanUtilMockedStatic = mockStatic(BeanUtil.class)) {
            beanUtilMockedStatic.when(() -> BeanUtil.getBean(AuditLogRepository.class)).thenReturn(mock(AuditLogRepository.class));
            CustomerReceiptsFilter customerReceiptsFilter = new CustomerReceiptsFilter();
            List<BranchReceiptsDTO> dtos = IntStream.range(0, 3).mapToObj(i -> Instancio.create(BranchReceiptsDTO.class)).toList();
            List<Branch> branches = IntStream.range(0, 3).mapToObj(i -> Instancio.create(Branch.class)).toList();
            for (int i = 0; i < dtos.size(); i++) {
                var branch = branches.get(i);
                branch.setId(dtos.get(i).getId());
            }
            when(clickhouseReceiptReportByBranch.getReceiptsAmountByBranch(customerReceiptsFilter)).thenReturn(new PageImpl<>(dtos));
            when(branchRepository.findAllById(any())).thenReturn(branches);
            Page<BranchReceiptsDTO> result = branchService.getReceiptsAmountByBranch(customerReceiptsFilter);
            assertEquals(dtos.size(), result.getContent().size());
            for (int i = 0; i < 3; i++) {
                var branchReceipt = result.getContent().get(i);
                assertEquals(branchReceipt.getActivityType(), branches.get(i).getActivityType().getNameRu());
                assertEquals(branchReceipt.getCompanyName(), branches.get(i).getCompany().getName());
                assertEquals(branchReceipt.getRegionName(), branches.get(i).getRegion().getNameRu());
                assertEquals(branchReceipt.getCityName(), branches.get(i).getCity().getNameRu());
                assertEquals(branchReceipt.getBranchName(), branches.get(i).getName());
            }
        }
    }

//    @Test
//    void deleteGroup() throws NoSuchFieldException, IllegalAccessException {
//        Field declaredField = SmartposService.class.getDeclaredField("branchRepository");
//        declaredField.setAccessible(true);
//        declaredField.set(branchService, branchRepository);
//        Field localizationServiceField = BaseService.class.getDeclaredField("localizationService");
//        localizationServiceField.setAccessible(true);
//        localizationServiceField.set(branchService, localizationService);
//        Long groupId = 1L;
//        BranchGroup branchGroup = new BranchGroup();
//        branchGroup.setId(groupId);
//        Branch branch = new Branch();
//        branch.setId(1L);
//        branch.setGroups(new HashSet<>());
//        Branch branch1 = new Branch();
//        branch1.setId(2L);
//        branch1.setGroups(new HashSet<>());
//        Set<Branch> branches = new HashSet<>();
//        branches.add(branch);
//        branches.add(branch1);
//        branchGroup.setBranches(branches);
//        when(branchGroupRepository.findById(groupId)).thenReturn(Optional.of(branchGroup));
//        branchService.deleteGroup(groupId);
//        verify(branchRepository, times(1)).saveAll(argThat(argument -> {
//            for (Branch b : argument) {
//                if (b.getGroups() != null) return false;
//            }
//            return true;
//        }));
//        verify(branchGroupRepository, times(1)).delete(any(BranchGroup.class));
//    }

    @Test
    @Transactional
    void createBranchGroup() throws NoSuchFieldException, IllegalAccessException {
        Field field = SmartposService.class.getDeclaredField("branchRepository");
        field.setAccessible(true);
        field.set(branchService, branchRepository);

        BranchGroupDTO branchGroupDTO = new BranchGroupDTO();
        branchGroupDTO.setName("Test Group");

        Branch branch = new Branch();
        branch.setId(1L);
        Branch branch1 = new Branch();
        branch1.setId(2L);

        SelectItem selectItem = new SelectItem(branch.getId(), "branch");
        SelectItem selectItem1 = new SelectItem(branch1.getId(), "branch1");
        List<SelectItem> branches = new ArrayList<>(List.of(selectItem, selectItem1));
        branchGroupDTO.setBranches(branches);
        Long GROUP_ID = Instancio.gen().longs().get();
        when(branchGroupRepository.save(any())).thenAnswer((Answer<BranchGroup>) invocation -> {
            BranchGroup argument = invocation.getArgument(0, BranchGroup.class);
            argument.setId(GROUP_ID);
            return argument;
        });
        when(branchRepository.findAllById(anyList())).thenReturn(List.of(branch, branch1));
        BranchGroupDTO result = branchService.createBranchGroup(branchGroupDTO);
//        verify(branchRepository, times(1)).saveAll(argThat(argument -> {
//            for (Branch temp : argument) {
//                if (!Objects.equals(temp.getGroupId(), GROUP_ID)) return false;
//            }
//            return true;
//        }));

        assertNotNull(result);
        assertEquals("Test Group", result.getName());
        verify(branchGroupRepository, times(2)).save(any(BranchGroup.class));

    }
}