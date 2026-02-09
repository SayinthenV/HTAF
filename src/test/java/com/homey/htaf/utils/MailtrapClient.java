package com.homey.htaf.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailtrapClient {
    private static final String BASE_URL = "https://mailtrap.io/api/v1";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);
    private static final Pattern RESET_LINK_PATTERN = Pattern.compile(
            "https?://[^\\s\"'>]*?/sales/password/edit\\?reset_password_token=[^\\s\"'>]+"
    );
    private static final Pattern CHANGE_PASSWORD_ANCHOR_PATTERN = Pattern.compile(
            "<a[^>]*href=['\"]([^'\"]+)['\"][^>]*>\\s*Change my password\\s*</a>",
            Pattern.CASE_INSENSITIVE
    );

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String token;
    private final String inboxId;

    public MailtrapClient(String token, String inboxId) {
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("Mailtrap API token is missing.");
        }
        if (inboxId == null || inboxId.isBlank()) {
            throw new IllegalStateException("Mailtrap inbox id is missing.");
        }
        this.token = token;
        this.inboxId = inboxId;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String waitForResetLink(String recipientEmail, String subjectContains) {
        long deadline = System.currentTimeMillis() + DEFAULT_TIMEOUT.toMillis();
        while (System.currentTimeMillis() < deadline) {
            Optional<Long> messageId = findLatestMessageId(recipientEmail, subjectContains);
            if (messageId.isPresent()) {
                String body = fetchMessageBody(messageId.get());
                Optional<String> link = extractResetLink(body);
                if (link.isPresent()) {
                    return link.get();
                }
            }
            sleep(3000);
        }
        throw new IllegalStateException("Password reset email not received within timeout.");
    }

    private Optional<Long> findLatestMessageId(String recipientEmail, String subjectContains) {
        String url = BASE_URL + "/inboxes/" + inboxId + "/messages";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Api-Token", token)
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new IllegalStateException("Mailtrap list messages failed: " + response.statusCode());
            }
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode messages = extractMessages(root);
            for (JsonNode message : messages) {
                if (!matchesRecipient(message, recipientEmail)) {
                    continue;
                }
                if (!matchesSubject(message, subjectContains)) {
                    continue;
                }
                return Optional.of(message.path("id").asLong());
            }
            if (messages.isArray() && messages.size() > 0) {
                return Optional.of(messages.get(0).path("id").asLong());
            }
            return Optional.empty();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Mailtrap list messages failed.", e);
        }
    }

    private String fetchMessageBody(Long messageId) {
        String url = BASE_URL + "/inboxes/" + inboxId + "/messages/" + messageId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Api-Token", token)
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new IllegalStateException("Mailtrap get message failed: " + response.statusCode());
            }
            JsonNode message = objectMapper.readTree(response.body());
            String htmlBody = message.path("html_body").asText("");
            String textBody = message.path("text_body").asText("");
            if (!htmlBody.isBlank() || !textBody.isBlank()) {
                return !htmlBody.isBlank() ? htmlBody : textBody;
            }
            String htmlPath = message.path("html_path").asText("");
            if (!htmlPath.isBlank()) {
                return fetchMessagePart(htmlPath);
            }
            String textPath = message.path("txt_path").asText("");
            if (!textPath.isBlank()) {
                return fetchMessagePart(textPath);
            }
            return "";
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Mailtrap get message failed.", e);
        }
    }

    private Optional<String> extractResetLink(String body) {
        if (body == null || body.isBlank()) {
            return Optional.empty();
        }
        Matcher anchorMatcher = CHANGE_PASSWORD_ANCHOR_PATTERN.matcher(body);
        if (anchorMatcher.find()) {
            return Optional.of(anchorMatcher.group(1));
        }
        Matcher matcher = RESET_LINK_PATTERN.matcher(body);
        if (matcher.find()) {
            return Optional.of(matcher.group());
        }
        return Optional.empty();
    }

    private String fetchMessagePart(String path) throws IOException, InterruptedException {
        String url = path.startsWith("http") ? path : "https://mailtrap.io" + path;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Api-Token", token)
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 300) {
            throw new IllegalStateException("Mailtrap get message part failed: " + response.statusCode());
        }
        return response.body();
    }

    private boolean matchesRecipient(JsonNode message, String recipientEmail) {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            return true;
        }
        String toEmail = message.path("to_email").asText();
        if (!toEmail.isBlank() && recipientEmail.equalsIgnoreCase(toEmail)) {
            return true;
        }
        JsonNode toArray = message.path("to");
        if (toArray.isArray()) {
            for (JsonNode recipient : toArray) {
                String email = recipient.path("email").asText();
                if (recipientEmail.equalsIgnoreCase(email)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchesSubject(JsonNode message, String subjectContains) {
        if (subjectContains == null || subjectContains.isBlank()) {
            return true;
        }
        String subject = message.path("subject").asText();
        return subject != null && subject.contains(subjectContains);
    }

    private JsonNode extractMessages(JsonNode root) {
        if (root == null) {
            return objectMapper.createArrayNode();
        }
        if (root.isArray()) {
            return root;
        }
        if (root.has("messages")) {
            return root.path("messages");
        }
        if (root.has("data")) {
            return root.path("data");
        }
        return objectMapper.createArrayNode();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
