package org.global.Void;

import java.util.Collections;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (req == null || req.getUsername() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "username and password required"));
        }

        if ("alice".equals(req.getUsername()) && "secret".equals(req.getPassword())) {
            String token = UUID.randomUUID().toString();
            LoginResponse resp = new LoginResponse(token);
            return ResponseEntity.ok(resp);
        }

        return ResponseEntity.status(401).body(Collections.singletonMap("error", "Invalid credentials"));
    }

}
