package uz.uzkassa.cts.service;

import uz.uzkassa.cts.dto.email.EmailDTO;

public interface EmailService {

    boolean send(EmailDTO emailDTO);

    String create();

    void checkConnection();
}
