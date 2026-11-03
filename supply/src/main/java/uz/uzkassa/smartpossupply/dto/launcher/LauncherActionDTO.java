package uz.uzkassa.smartpossupply.dto.launcher;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 11.12.2024
 */

@Getter
@Setter
public class LauncherActionDTO implements Serializable {

    List<Long> terminalIds;
}
