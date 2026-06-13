package rs.fon.skolajezika.config;

import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TabTokenService {

    private final Map<String, Authentication> prijave = new ConcurrentHashMap<>();

    public String kreiraj(Authentication authentication) {
        String token = UUID.randomUUID().toString();
        prijave.put(token, authentication);
        return token;
    }

    public Authentication pronadji(String token) {
        return token == null ? null : prijave.get(token);
    }

    public void ukloni(String token) {
        if (token != null) {
            prijave.remove(token);
        }
    }
}
