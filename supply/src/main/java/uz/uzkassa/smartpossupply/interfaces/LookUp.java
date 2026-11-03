package uz.uzkassa.smartpossupply.interfaces;

import java.util.List;


public interface LookUp<LookUpDTO, Filter> {
    List<LookUpDTO> lookUp(Filter filter);
}
