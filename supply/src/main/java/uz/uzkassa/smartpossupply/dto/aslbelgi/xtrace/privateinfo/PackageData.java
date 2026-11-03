package uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.privateinfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PackageData implements Serializable {

    String packageType;

    Boolean emptyPackage;

    String parentCode;

    List<ChildPackageData> children;

    Long actuallyPacked;
}
