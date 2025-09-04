package kimp.security.cookie.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.DatatypeConverter;
import kimp.security.cookie.CookieVerifier;
import kimp.security.user.dto.CookiePayload;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
public class CookieVerifierImpl implements CookieVerifier {
    private final ObjectMapper objectMapper;

    public CookieVerifierImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public CookiePayload verify(String cookieValue, String secret) {
        if(cookieValue == null) {
            return null;
        }

        String[] parts = cookieValue.split("\\.");
        if(parts.length != 2) {
            return null;
        }

        String payloadB64 = parts[0];
        String signatureB64Url = parts[1];

        try {
            byte[] payloadBytes = base64UrlDecode(payloadB64);
            byte[] givenSig = base64UrlDecode(signatureB64Url);

            // HMAC-SHA256 알고리즘 사용
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] expectedSig = mac.doFinal(payloadBytes);


            if (!constantTimeEquals(givenSig, expectedSig)) return null;

            return objectMapper.readValue(payloadBytes, CookiePayload.class);
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] base64UrlDecode(String input) {
        String base64 = input.replace("-", "+").replace("_", "/");
        int pad = (4- (base64.length() % 4)) % 4;
        base64 = base64 + "=".repeat(pad);
        return Base64.getDecoder().decode(base64);
    }

    private boolean constantTimeEquals(byte[] a, byte[] b) {
        if(a == null || b == null || a.length != b.length) {
            return false;
        }
        int r = 0;
        for(int i = 0; i < a.length; i++){
            r |= a[i] ^ b[i];
        }
        return r == 0;
    }

    @Override
    public String createSignedCookie(String id, String secret) {
        try {
            CookiePayload payload = new CookiePayload(id, System.currentTimeMillis() / 1000);
            String payloadJson = objectMapper.writeValueAsString(payload);
            byte[] payloadBytes = payloadJson.getBytes(StandardCharsets.UTF_8);
            
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signature = mac.doFinal(payloadBytes);
            
            String payloadB64 = base64UrlEncode(payloadBytes);
            String signatureB64 = base64UrlEncode(signature);
            
            return payloadB64 + "." + signatureB64;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create signed cookie", e);
        }
    }
    
    private String base64UrlEncode(byte[] input) {
        return Base64.getEncoder().encodeToString(input)
                .replace("+", "-")
                .replace("/", "_")
                .replaceAll("=", "");
    }
}
