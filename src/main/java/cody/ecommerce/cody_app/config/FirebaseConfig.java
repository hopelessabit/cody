package cody.ecommerce.cody_app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Configuration
public class FirebaseConfig {
    @Value("${firebase.storage.bucket-name}")
    private String bucketName;

    @Value("${firebase.credentials.base64:}")
    private String firebaseCredentialsBase64;

    private GoogleCredentials credentials;

    @PostConstruct
    public void init() throws IOException {
        this.credentials = getGoogleCredentials();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setStorageBucket(bucketName)
                    .build();
            FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public Storage storage() throws IOException {
        if (credentials == null) {
            credentials = getGoogleCredentials();
        }

        GoogleCredentials scopedCredentials = credentials
                .createScoped(List.of("https://www.googleapis.com/auth/devstorage.full_control"));

        return StorageOptions.newBuilder()
                .setCredentials(scopedCredentials)
                .build()
                .getService();
    }

    private GoogleCredentials getGoogleCredentials() throws IOException {
        // Use base64 credentials if available (for production)
        if (!firebaseCredentialsBase64.isEmpty()) {
            byte[] credentialsBytes = Base64.getDecoder().decode(firebaseCredentialsBase64);
            return GoogleCredentials.fromStream(new ByteArrayInputStream(credentialsBytes));
        }

        // Fallback to classpath file (for local development)
        try (var in = new ClassPathResource("firebase.json").getInputStream()) {
            return GoogleCredentials.fromStream(in);
        }
    }
}