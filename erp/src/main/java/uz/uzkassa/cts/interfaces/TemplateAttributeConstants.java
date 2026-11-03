package uz.uzkassa.cts.interfaces;

import uz.uzkassa.cts.dto.base.CommonCodeDTO;

import java.util.List;

public interface TemplateAttributeConstants {

    String FIRST_NAME = "${FIRST_NAME}";
    String LAST_NAME = "${LAST_NAME}";
    String PATRONYMIC = "${PATRONYMIC}";
    String CALL_CENTER_PHONE = "${CALL_CENTER_PHONE}";
    String ACTIVATION_CODE = "${ACTIVATION_CODE}";
    String EXPIRE_TIME  = "${EXPIRE_TIME}";
    String USER_NAME  = "${USER_NAME}";
    String PHONE  = "${PHONE}";
    String NUMBER  = "${NUMBER}";
    String ENTITY_TYPE = "${ENTITY_TYPE}";
    String STATUS = "${STATUS}";
    String APPROVER = "${APPROVER}";
    String CREATOR = "${CREATOR}";

    interface ACTIVATION {
        List<CommonCodeDTO> ATTRIBUTES = List.of(
                new CommonCodeDTO(1L, "Activation code", ACTIVATION_CODE),
                new CommonCodeDTO(2L, "Expire Time", EXPIRE_TIME)
        );
    }

    interface CHANGE_PASSWORD {
        List<CommonCodeDTO> ATTRIBUTES = List.of(
                new CommonCodeDTO(1L, "User Name", USER_NAME),
                new CommonCodeDTO(2L, "Activation code", ACTIVATION_CODE),
                new CommonCodeDTO(3L, "Expire Time", EXPIRE_TIME)
        );
    }

    interface CHANGE_USERNAME {
        List<CommonCodeDTO> ATTRIBUTES = List.of(
                new CommonCodeDTO(1L, "User Name", USER_NAME),
                new CommonCodeDTO(2L, "Activation code", ACTIVATION_CODE),
                new CommonCodeDTO(3L, "Expire Time", EXPIRE_TIME)
        );
    }

    interface BIRTHDAY {
        List<CommonCodeDTO> ATTRIBUTES = List.of(
                new CommonCodeDTO(1L, "User Name", FIRST_NAME),
                new CommonCodeDTO(2L, "Activation code", ACTIVATION_CODE)
        );
    }

    interface CUSTOMER_ACTIVATION {
        List<CommonCodeDTO> ATTRIBUTES = List.of(
                new CommonCodeDTO(1L, "Activation code", ACTIVATION_CODE)
        );
    }

    interface LEAD {
        List<CommonCodeDTO> ATTRIBUTES = List.of(
                new CommonCodeDTO(1L, "Phone ", PHONE)
        );
    }

    interface VACANCY_APPLICATION {
        List<CommonCodeDTO> ATTRIBUTES = List.of(
                new CommonCodeDTO(1L, "Number", NUMBER),
                new CommonCodeDTO(2L, "Approver Fist name", FIRST_NAME),
                new CommonCodeDTO(3L, "Approver Last name", LAST_NAME)
        );
    }

    interface APPROVAL_PROCESS_TEMPLATE_FOR_CREATOR {
        List<CommonCodeDTO> ATTRIBUTES = List.of(
                new CommonCodeDTO(1L, "Entity Name", ENTITY_TYPE),
                new CommonCodeDTO(2L, "Status", STATUS),
                new CommonCodeDTO(3L, "Approver", APPROVER)
        );
    }

    interface APPROVAL_PROCESS_TEMPLATE_FOR_APPROVER {
        List<CommonCodeDTO> ATTRIBUTES = List.of(
                new CommonCodeDTO(1L, "Entity type", ENTITY_TYPE),
                new CommonCodeDTO(2L, "Creator", CREATOR)
        );
    }
}
