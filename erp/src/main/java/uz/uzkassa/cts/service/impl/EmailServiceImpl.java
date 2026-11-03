package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.dto.email.EmailDTO;
import uz.uzkassa.cts.service.EmailService;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@Slf4j
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    ApplicationProperties applicationProperties;

    @Override
    public boolean send(EmailDTO emailDTO) {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", applicationProperties.getEmailProperty().getHost());
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "25");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(applicationProperties.getEmailProperty().getUsername(), applicationProperties.getEmailProperty().getPassword());
            }
        };

        Session session = Session.getDefaultInstance(properties, auth);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailDTO.getFrom()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailDTO.getTo()));
            message.setSubject(emailDTO.getSubject());
            message.setText(emailDTO.getMessage());
            Transport.send(message);
            return true;
        } catch (MessagingException ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public String create() {
        try {
            // Step 1: Configure the XML-RPC client
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://127.0.0.1/postfixadmin/public/xmlrpc.php"));

            // You might need to provide admin credentials if your API requires authentication.
            config.setBasicUserName("user@example.com"); // PostfixAdmin API admin username
            config.setBasicPassword("Softline2020!");            // PostfixAdmin API admin password

            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            // Step 2: Prepare parameters for the mailbox creation request
            Map<String, Object> mailboxParams = new HashMap<>();
            mailboxParams.put("domain", "smartpos.uz");               // The domain part of the email
            mailboxParams.put("local_part", "john");                  // The local part of the email (before the @)
            mailboxParams.put("password", "secret123");               // Password for the email account
            mailboxParams.put("password2", "secret123");              // Password confirmation
            mailboxParams.put("name", "John Doe");                    // Full name of the mailbox owner (optional)
            mailboxParams.put("quota", 2048);                         // Mailbox quota (in MB, optional)
            mailboxParams.put("active", 1);                           // Whether the account is active (1 for active)

            // Step 3: Call the PostfixAdmin API to create the mailbox
            Object result = client.execute("mailbox_add", new Object[]{mailboxParams});

            // Step 4: Output the result (success/failure)
            System.out.println("Mailbox creation result: " + result);
            return result.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    @Override
    public void checkConnection() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", applicationProperties.getEmailProperty().getHost());
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "25");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(applicationProperties.getEmailProperty().getUsername(), applicationProperties.getEmailProperty().getPassword());
            }
        };

        Session session = Session.getDefaultInstance(properties, auth);
        EmailDTO emailDTO = EmailDTO
                .builder()
                .from(applicationProperties.getEmailProperty().getFrom())
                .to(applicationProperties.getEmailProperty().getCheckEmail())
                .subject("Checking connection")
                .message("Checking Email service")
                .build();

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailDTO.getFrom()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailDTO.getTo()));
            message.setSubject(emailDTO.getSubject());
            message.setText(emailDTO.getMessage());
            Transport.send(message);
        } catch (MessagingException ex) {
            log.error(ex.getMessage(), ex);
            throw new IntegrationException("An unexpected error has occurred while authenticating to Email service. Error: " + ex.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }
}
