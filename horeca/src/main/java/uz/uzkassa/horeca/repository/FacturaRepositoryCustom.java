package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.factura.Factura;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.FacturaFilter;

public interface FacturaRepositoryCustom {
    ResultList<Factura> getResultList(FacturaFilter filter);
}
