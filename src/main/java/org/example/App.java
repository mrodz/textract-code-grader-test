package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.TextractClientBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String... args) {
        logger.info("Application starts");

        Region region = Region.US_WEST_1;

        logger.info("Application ends");

        TextractClientBuilder config = TextractClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create("textract-handwriting-demo"))
                .region(region);

        File folder = new File("./src/main/resources/code-images");

        try (CodeParser textractClient = new CodeParser(config)) {

            for (File file : Objects.requireNonNull(folder.listFiles())) {
                logger.info("Processing {}", file);
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    textractClient.detectDocText(fileInputStream);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
