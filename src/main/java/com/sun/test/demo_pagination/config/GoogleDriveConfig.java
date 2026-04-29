package com.sun.test.demo_pagination.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleDriveConfig {

    private static final String APPLICATION_NAME = "Master2026-Billing-App";

    /**
     * Define the Drive bean for @Autowired in GoogleDriveService
     */
    @Bean
    public Drive googleDriveClient() throws GeneralSecurityException, IOException {
        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(getCredentials()))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Logic to load credentials.json from src/main/resources
     */
    private GoogleCredentials getCredentials() throws IOException {
        // Load the JSON file from resources
        ClassPathResource resource = new ClassPathResource("credentials.json");

        return GoogleCredentials.fromStream(resource.getInputStream())
                .createScoped(Collections.singleton(DriveScopes.DRIVE));
    }
}