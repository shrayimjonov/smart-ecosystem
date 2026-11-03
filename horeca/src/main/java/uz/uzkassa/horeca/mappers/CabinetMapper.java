package uz.uzkassa.horeca.mappers;

public interface CabinetMapper<BaseDTO, CabinetDTO> {

    BaseDTO fromCabinet(CabinetDTO cabinetDTO);
}
