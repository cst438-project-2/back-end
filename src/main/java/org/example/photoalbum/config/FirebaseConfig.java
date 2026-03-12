package org.example.photoalbum.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws Exception {
        try {
            if (!FirebaseApp.getApps().isEmpty()) return;

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build();

            FirebaseApp.initializeApp(options);

            System.out.println("✅ Firebase Admin initialized");
        } catch (Exception e) {
            // Need to setup Google Application Default Credentials: gcloud auth application-default login
            // Fallback for now. If no credentials, don't initialize firebase
            System.out.println("Firebase not initialized (no credentials found)");
        }
    }
}