package com.cortis.auth.service;


import com.cortis.auth.dto.LoginRequest;
import com.cortis.auth.dto.LoginResponse;
import com.cortis.core.entity_legacy.user.UserEntity;
import com.cortis.auth.repositorie.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository , PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public LoginResponse login(LoginRequest request) {

        System.out.println("1. Login service elindult");

        UserEntity user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    System.out.println("2. Felhasználó nem található");

                    return new RuntimeException(
                            "Hibás felhasználónév vagy jelszó"
                    );
                });

        System.out.println("2. Felhasználó megtalálva");

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        System.out.println(
                "3. Jelszó eredménye: " + passwordMatches
        );

        if (!passwordMatches) {
            throw new RuntimeException(
                    "Hibás felhasználónév vagy jelszó"
            );
        }

        System.out.println("4. LoginResponse létrehozása");

        LoginResponse response = new LoginResponse(
                user.getUsername(),
                "test-token"
        );

        System.out.println("5. AuthService visszatér");

        return response;
    }


}
