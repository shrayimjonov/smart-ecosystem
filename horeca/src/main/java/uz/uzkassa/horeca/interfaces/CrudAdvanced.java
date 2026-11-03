package uz.uzkassa.horeca.interfaces;

import org.springframework.data.domain.Page;

import java.util.List;


public interface CrudAdvanced<ID, DTO, DetailDTO, ListDTO, Filter, Status, LookUpDTO>
    extends Crud<ID, DTO, DetailDTO, ListDTO, Filter>, StatusUpdater<ID, Status>, LookUp<LookUpDTO, Filter>, Validatable<DTO> {
    void validate(DTO dto);

    ID create(DTO dto);

    ID update(ID id, DTO dto);

    DetailDTO get(ID id);

    Page<ListDTO> getList(Filter f);

    void delete(ID id);

    List<LookUpDTO> lookUp(Filter filter);

    void updateStatus(ID id, Status status);
}
