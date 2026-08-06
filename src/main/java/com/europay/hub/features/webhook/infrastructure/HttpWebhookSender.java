package com.europay.hub.features.webhook.infrastructure;

import com.europay.hub.features.webhook.application.port.WebhookSender;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

/**
 * Delivers webhooks over HTTP with an HMAC-SHA256 signature so the merchant can verify the call
 * really came from EuroPay Hub. Signature header: {@code X-EuroPay-Signature: sha256=<hex>}.
 */
@Component
public class HttpWebhookSender implements WebhookSender {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final HttpClient client = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();

    @Override
    public DeliveryResult send(String url, String secret, String payload) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(TIMEOUT)
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "EuroPayHub-Webhooks/1.0")
                    .header("X-EuroPay-Signature", "sha256=" + sign(secret, payload))
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            int code = response.statusCode();
            boolean ok = code >= 200 && code < 300;
            return new DeliveryResult(code, ok, ok ? null : "HTTP " + code);
        } catch (Exception e) {
            return new DeliveryResult(null, false, e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private static String sign(String secret, String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to sign webhook payload", e);
        }
    }
}
