package uz.uzkassa.smartpossupply.dto.user.supervisor;

public record EmployeeAssignmentDTO(
    Long id,
    String fullName,
    Boolean assigned
) {}