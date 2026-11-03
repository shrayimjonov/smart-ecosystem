package uz.uzkassa.horeca.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfiguration {

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    @Bean
    FirebaseApp firebaseApp(GoogleCredentials credentials) {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        return FirebaseApp.initializeApp(options,"FIREFORPUSH");
    }

    @Bean
    GoogleCredentials googleCredentials() {
        try (InputStream is = new ClassPathResource("horeca-2d7fa-firebase-adminsdk-uejmj-3056256cca.json").getInputStream()) {
            return GoogleCredentials.fromStream(is);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}