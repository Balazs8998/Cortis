package com.cortis.core.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final JwtParser jwtParser;
    private final long expirationSeconds;

    public JwtService(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.expiration-seconds}") long expirationSeconds
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parser()
                .verifyWith(signingKey)
                .build();

        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(UserDetails userDetails) {

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(
                        now.plusSeconds(expirationSeconds)
                ))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {
        Claims claims = extractAllClaims(token);

        return userDetails.isEnabled()
                && userDetails.getUsername()
                .equals(claims.getSubject());
    }

    private Claims extractAllClaims(String token) {
        return jwtParser
                .parseSignedClaims(token)
                .getPayload();
    }
}