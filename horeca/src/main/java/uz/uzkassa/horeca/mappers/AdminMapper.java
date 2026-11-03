package uz.uzkassa.horeca.mappers;

public interface AdminMapper<BaseDTO, AdminDTO> {

    BaseDTO fromAdmin(AdminDTO adminDTO);
}
