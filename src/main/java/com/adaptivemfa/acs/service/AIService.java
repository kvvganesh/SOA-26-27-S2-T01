package com.adaptivemfa.acs.service;

import com.adaptivemfa.acs.dto.AIPredictionRequest;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class AIService {

    private final ObjectMapper objectMapper;

    public AIService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> predictRisk(
            int failedAttempts,
            int trustedDevice,
            int trustedLocation,
            int unusualTime) {

        try {

            AIPredictionRequest request = new AIPredictionRequest(
                    failedAttempts,
                    trustedDevice,
                    trustedLocation,
                    unusualTime
            );

            String json = objectMapper.writeValueAsString(request);

            System.out.println("JSON SENT TO AI:");
            System.out.println(json);

            URL url = new URL("http://127.0.0.1:8000/predict");

            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
            );
            connection.setDoOutput(true);

            byte[] requestBody =
                    json.getBytes(StandardCharsets.UTF_8);

            connection.setRequestProperty(
                    "Content-Length",
                    String.valueOf(requestBody.length)
            );

            try (OutputStream outputStream =
                         connection.getOutputStream()) {

                outputStream.write(requestBody);
                outputStream.flush();
            }

            int statusCode = connection.getResponseCode();

            System.out.println("AI STATUS:");
            System.out.println(statusCode);

            BufferedReader reader;

            if (statusCode >= 400) {
                reader = new BufferedReader(
                        new InputStreamReader(
                                connection.getErrorStream(),
                                StandardCharsets.UTF_8
                        )
                );
            } else {
                reader = new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                );
            }

            StringBuilder responseBody = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }

            reader.close();

            System.out.println("AI RESPONSE:");
            System.out.println(responseBody);

            if (statusCode >= 400) {
                throw new RuntimeException(
                        "AI service returned HTTP " +
                                statusCode +
                                ": " +
                                responseBody
                );
            }

            return objectMapper.readValue(
                    responseBody.toString(),
                    Map.class
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "AI service communication failed: "
                            + e.getMessage(),
                    e
            );
        }
    }
}