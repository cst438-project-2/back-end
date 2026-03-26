package org.example.photoalbum.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws Exception {
        try {
            if (!FirebaseApp.getApps().isEmpty()) return;

            // Read Firebase credentials from environment variable (used on Render)
            String credentialsJson = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");

            GoogleCredentials credentials;
            if (credentialsJson != null && !credentialsJson.isBlank()) {
                // On Render: parse credentials from the env var JSON string
                credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))
                );
            } else {
                // Locally: fall back to gcloud application default credentials
                credentials = GoogleCredentials.getApplicationDefault();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId("photoapi-57629")
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("Firebase Admin initialized");
        } catch (Exception e) {
            // Need to setup Google Application Default Credentials: gcloud auth application-default login
            // Fallback for now. If no credentials, don't initialize firebase
            System.out.println("Firebase not initialized (no credentials found)");
        }
    }
}