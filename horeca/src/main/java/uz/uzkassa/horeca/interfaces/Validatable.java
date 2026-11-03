package uz.uzkassa.horeca.interfaces;

public interface Validatable<DTO> {
    void validate(DTO dto);
}
