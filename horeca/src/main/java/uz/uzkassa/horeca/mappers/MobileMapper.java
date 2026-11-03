package uz.uzkassa.horeca.mappers;

public interface MobileMapper<BaseDTO, MobileDTO> {

    BaseDTO fromMobile(MobileDTO mobileDTO);
}
