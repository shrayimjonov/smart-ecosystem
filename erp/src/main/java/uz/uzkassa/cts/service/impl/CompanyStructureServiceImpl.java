package uz.uzkassa.cts.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.hrms.Department;
import uz.uzkassa.cts.domain.hrms.Position;
import uz.uzkassa.cts.dto.companystructure.company.BranchTreeByCompanyStructureDTO;
import uz.uzkassa.cts.dto.companystructure.company.CompanyStructureCompanyDTO;
import uz.uzkassa.cts.dto.companystructure.company.DepartmentTreeByCompanyStructureDTO;
import uz.uzkassa.cts.dto.companystructure.company.EmployeeTreeByCompanyStructureDTO;
import uz.uzkassa.cts.dto.companystructure.department.CompanyStructureDepartmentDTO;
import uz.uzkassa.cts.dto.companystructure.department.DepartmentStructureDTO;
import uz.uzkassa.cts.dto.companystructure.department.DepartmentStructureEmployeeDTO;
import uz.uzkassa.cts.dto.companystructure.position.CompanyStructurePositionDTO;
import uz.uzkassa.cts.dto.companystructure.position.PositionStructureDTO;
import uz.uzkassa.cts.dto.companystructure.position.PositionStructureEmployeeDTO;
import uz.uzkassa.cts.dto.companystructure.supervisor.CompanyStructureSupervisorDTO;
import uz.uzkassa.cts.dto.companystructure.supervisor.SupervisorStructureDTO;
import uz.uzkassa.cts.dto.companystructure.supervisor.SupervisorStructureEmployeeDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.UserStatus;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.CompanyStructureService;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class CompanyStructureServiceImpl extends BaseServiceImpl<Company, Long> implements CompanyStructureService {

    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;

    public CompanyStructureServiceImpl(CompanyRepository companyRepository, BranchRepository branchRepository, DepartmentRepository departmentRepository, EmployeeRepository employeeRepository, PositionRepository positionRepository) {
        super(companyRepository);
        this.companyRepository = companyRepository;
        this.branchRepository = branchRepository;
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
    }

    @Override
    public CompanyStructureCompanyDTO getByCompany(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));
        CompanyStructureCompanyDTO result = new CompanyStructureCompanyDTO();
        result.setId(company.getId());
        result.setTin(company.getTin());
        result.setName(company.getName());

        branchRepository.findAllByCompanyIdAndStatusAndParentIdIsNullAndDeletedIsFalse(company.getId(), CommonStatus.ACTIVE)
                .forEach(branch -> {
                    BranchTreeByCompanyStructureDTO branchDTO = new BranchTreeByCompanyStructureDTO();
                    branchDTO.setId(branch.getId());
                    branchDTO.setName(branch.getName());
                    departmentRepository.findAllByBranchIdAndParentIdIsNullAndStatus(branch.getId(), CommonStatus.ACTIVE)
                            .forEach(department -> {
                                DepartmentTreeByCompanyStructureDTO departmentDTO = new DepartmentTreeByCompanyStructureDTO();
                                departmentDTO.setId(department.getId());
                                departmentDTO.setName(department.getName());
                                employeeRepository.findAllByDepartmentIdAndStatusAndDeletedIsFalse(department.getId(), UserStatus.ACTIVE)
                                        .forEach(employee -> {
                                            EmployeeTreeByCompanyStructureDTO employeeDTO = new EmployeeTreeByCompanyStructureDTO();
                                            employeeDTO.setId(employee.getId());
                                            employeeDTO.setFirstName(employee.getFirstName());
                                            employeeDTO.setLastName(employee.getLastName());
                                            employeeDTO.setPatronymic(employee.getPatronymic());
                                            employeeDTO.setPosition(employee.getPosition().toCommonDTO());
                                            departmentDTO.addEmployee(employeeDTO);
                                        });
                                branchDTO.addDepartment(departmentDTO);
                            });
                    result.addBranch(branchDTO);
                    if (CollectionUtils.isNotEmpty(branch.getChildren())) {
                        this.branchRecursion(branchDTO, branch.getChildren());
                    }
                });

        return result;
    }

    private void branchRecursion(BranchTreeByCompanyStructureDTO parentBranchDTO, List<Branch> branches) {
        branches.forEach(branch -> {
            BranchTreeByCompanyStructureDTO branchDTO = new BranchTreeByCompanyStructureDTO();
            branchDTO.setId(branch.getId());
            branchDTO.setName(branch.getName());
            departmentRepository.findAllByBranchIdAndParentIdIsNullAndStatus(branch.getId(), CommonStatus.ACTIVE)
                    .forEach(department -> {
                        DepartmentTreeByCompanyStructureDTO departmentDTO = new DepartmentTreeByCompanyStructureDTO();
                        departmentDTO.setId(department.getId());
                        departmentDTO.setName(department.getName());
                        employeeRepository.findAllByDepartmentIdAndStatusAndDeletedIsFalse(department.getId(), UserStatus.ACTIVE)
                                .forEach(employee -> {
                                    EmployeeTreeByCompanyStructureDTO employeeDTO = new EmployeeTreeByCompanyStructureDTO();
                                    employeeDTO.setId(employee.getId());
                                    employeeDTO.setFirstName(employee.getFirstName());
                                    employeeDTO.setLastName(employee.getLastName());
                                    employeeDTO.setPatronymic(employee.getPatronymic());
                                    employeeDTO.setPosition(employee.getPosition().toCommonDTO());
                                    departmentDTO.addEmployee(employeeDTO);
                        });
                        branchDTO.addDepartment(departmentDTO);
                    });
            parentBranchDTO.addChild(branchDTO);
            if (CollectionUtils.isNotEmpty(branch.getChildren())) {
                this.branchRecursion(branchDTO, branch.getChildren());
            }
        });
    }

    @Override
    public CompanyStructurePositionDTO getByPosition(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));
        CompanyStructurePositionDTO result = new CompanyStructurePositionDTO();
        result.setId(company.getId());
        result.setName(company.getName());
        result.setTin(company.getTin());

        positionRepository.findAllByCompanyIdAndParentIdIsNullAndStatus(companyId, CommonStatus.ACTIVE)
                .forEach(position -> {
                    PositionStructureDTO positionDTO = new PositionStructureDTO();
                    positionDTO.setId(position.getId());
                    positionDTO.setName(position.getName());
                    employeeRepository.findAllByPositionIdAndDeletedIsFalse(position.getId())
                            .forEach(employee -> {
                                PositionStructureEmployeeDTO employeeDTO = new PositionStructureEmployeeDTO();
                                employeeDTO.setId(employee.getId());
                                employeeDTO.setLastName(employee.getLastName());
                                employeeDTO.setFirstName(employee.getFirstName());
                                employeeDTO.setPatronymic(employee.getPatronymic());
                                if (employee.getPhotoId() != null) {
                                    employeeDTO.setPhoto(employee.getPhoto().toFileDTO());
                                }
                                if (employee.getDepartmentId() != null) {
                                    employeeDTO.setDepartment(employee.getDepartment().toCommonDTO());
                                }
                                positionDTO.addEmployee(employeeDTO);
                            });
                    result.addPosition(positionDTO);
                    if (CollectionUtils.isNotEmpty(position.getChildren())) {
                        this.positionRecursion(positionDTO, position.getChildren());
                    }
                });

        return result;
    }

    private void positionRecursion(PositionStructureDTO parentPositionDTO, List<Position> positions) {
        for (Position position : positions) {
            PositionStructureDTO positionDTO = new PositionStructureDTO();
            positionDTO.setId(position.getId());
            positionDTO.setName(position.getName());
            employeeRepository.findAllByPositionIdAndDeletedIsFalse(position.getId())
                    .forEach(employee -> {
                        PositionStructureEmployeeDTO employeeDTO = new PositionStructureEmployeeDTO();
                        employeeDTO.setId(employee.getId());
                        employeeDTO.setLastName(employee.getLastName());
                        employeeDTO.setFirstName(employee.getFirstName());
                        employeeDTO.setPatronymic(employee.getPatronymic());
                        if (employee.getPhotoId() != null) {
                            employeeDTO.setPhoto(employee.getPhoto().toFileDTO());
                        }
                        if (employee.getDepartmentId() != null) {
                            employeeDTO.setDepartment(employee.getDepartment().toCommonDTO());
                        }
                        positionDTO.addEmployee(employeeDTO);
                    });
            parentPositionDTO.addChild(positionDTO);
            if (CollectionUtils.isNotEmpty(position.getChildren())) {
                this.positionRecursion(positionDTO, position.getChildren());
            }
        }
    }

    @Override
    public CompanyStructureDepartmentDTO getByDepartment(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));
        CompanyStructureDepartmentDTO result = new CompanyStructureDepartmentDTO();
        result.setId(company.getId());
        result.setName(company.getName());
        result.setTin(company.getTin());

        List<Long> branchIds = branchRepository.findAllIdsByCompanyIdAndStatus(companyId, CommonStatus.ACTIVE);
        departmentRepository.findAllByBranchIdInAndParentIdIsNullAndStatus(branchIds, CommonStatus.ACTIVE)
                .forEach(department -> {
                    DepartmentStructureDTO departmentDTO = new DepartmentStructureDTO();
                    departmentDTO.setId(department.getId());
                    departmentDTO.setName(department.getName());
                    employeeRepository.findAllByDepartmentIdAndStatusAndDeletedIsFalse(department.getId(), UserStatus.ACTIVE)
                            .forEach(employee -> {
                                DepartmentStructureEmployeeDTO employeeDTO = new DepartmentStructureEmployeeDTO();
                                employeeDTO.setId(employee.getId());
                                employeeDTO.setLastName(employee.getLastName());
                                employeeDTO.setFirstName(employee.getFirstName());
                                employeeDTO.setPatronymic(employee.getPatronymic());
                                if (employee.getPhotoId() != null) {
                                    employeeDTO.setPhoto(employee.getPhoto().toFileDTO());
                                }
                                if (employee.getPositionId() != null) {
                                    employeeDTO.setPosition(employee.getPosition().toCommonDTO());
                                }
                                departmentDTO.addEmployee(employeeDTO);
                            });
                    result.addDepartment(departmentDTO);
                    if (CollectionUtils.isNotEmpty(department.getChildren())) {
                        this.departmentRecursion(departmentDTO, department.getChildren());
                    }
                });

        return result;
    }

    private void departmentRecursion(DepartmentStructureDTO parentDepartmentDTO, List<Department> departments) {
        for (Department department : departments) {
            DepartmentStructureDTO departmentDTO = new DepartmentStructureDTO();
            departmentDTO.setId(department.getId());
            departmentDTO.setName(department.getName());
            employeeRepository.findAllByDepartmentIdAndStatusAndDeletedIsFalse(department.getId(), UserStatus.ACTIVE)
                    .forEach(employee -> {
                        DepartmentStructureEmployeeDTO employeeDTO = new DepartmentStructureEmployeeDTO();
                        employeeDTO.setId(employee.getId());
                        employeeDTO.setLastName(employee.getLastName());
                        employeeDTO.setFirstName(employee.getFirstName());
                        employeeDTO.setPatronymic(employee.getPatronymic());
                        if (employee.getPhotoId() != null) {
                            employeeDTO.setPhoto(employee.getPhoto().toFileDTO());
                        }
                        if (employee.getPositionId() != null) {
                            employeeDTO.setPosition(employee.getPosition().toCommonDTO());
                        }
                        departmentDTO.addEmployee(employeeDTO);
                    });
            parentDepartmentDTO.addChild(departmentDTO);
            if (CollectionUtils.isNotEmpty(department.getChildren())) {
                this.departmentRecursion(departmentDTO, department.getChildren());
            }
        }
    }

    @Override
    public CompanyStructureSupervisorDTO getBySupervisor(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY));
        CompanyStructureSupervisorDTO result = new CompanyStructureSupervisorDTO();
        result.setId(company.getId());
        result.setName(company.getName());
        result.setTin(company.getTin());

        employeeRepository.findAllByCompanyIdAndDeletedIsFalseAndStatusIsActiveAndSupervisorIdIsNull(companyId)
                .forEach(supervisor -> {
                    SupervisorStructureDTO supervisorDTO = new SupervisorStructureDTO();
                    SupervisorStructureEmployeeDTO employeeDTO = new SupervisorStructureEmployeeDTO();
                    employeeDTO.setId(supervisor.getId());
                    employeeDTO.setLastName(supervisor.getLastName());
                    employeeDTO.setFirstName(supervisor.getFirstName());
                    employeeDTO.setPatronymic(supervisor.getPatronymic());
                    if (supervisor.getPhoto() != null) {
                        employeeDTO.setPhoto(supervisor.getPhoto().toFileDTO());
                    }
                    if (supervisor.getPositionId() != null) {
                        employeeDTO.setPosition(supervisor.getPosition().toCommonDTO());
                    }
                    supervisorDTO.addEmployee(employeeDTO);
                    List<User> employees = employeeRepository.findAllByCompanyIdAndDeletedIsFalseAndStatusIsActiveAndSupervisorId(companyId, supervisor.getId());

                    result.addSupervisor(supervisorDTO);
                    if (CollectionUtils.isNotEmpty(employees)) {
                        this.supervisorRecursion(supervisorDTO, employees, companyId);
                    }
                });
        return result;
    }

    private void supervisorRecursion(SupervisorStructureDTO parentSupervisorDTO, List<User> supervisors, Long companyId) {
        for (User supervisor : supervisors) {
            SupervisorStructureDTO supervisorDTO = new SupervisorStructureDTO();
            SupervisorStructureEmployeeDTO employeeDTO = new SupervisorStructureEmployeeDTO();
            employeeDTO.setId(supervisor.getId());
            employeeDTO.setLastName(supervisor.getLastName());
            employeeDTO.setFirstName(supervisor.getFirstName());
            employeeDTO.setPatronymic(supervisor.getPatronymic());
            if (supervisor.getPhotoId() != null) {
                employeeDTO.setPhoto(supervisor.getPhoto().toFileDTO());
            }
            if (supervisor.getPositionId() != null) {
                employeeDTO.setPosition(supervisor.getPosition().toCommonDTO());
            }
            supervisorDTO.addEmployee(employeeDTO);
            List<User> employees = employeeRepository.findAllByCompanyIdAndDeletedIsFalseAndStatusIsActiveAndSupervisorId(companyId, supervisor.getId());
            parentSupervisorDTO.addChild(supervisorDTO);
            if (CollectionUtils.isNotEmpty(employees)) {
                this.supervisorRecursion(supervisorDTO, employees, companyId);
            }
        }
    }
}
