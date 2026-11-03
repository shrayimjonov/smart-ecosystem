package uz.uzkassa.smartpos.domain.postgres.base;

import uz.uzkassa.smartpos.dto.SelectItem;

import java.io.Serializable;

public abstract class BaseEntity implements Serializable {

    public abstract Object getId();

    public abstract SelectItem toSelectItem();
}
