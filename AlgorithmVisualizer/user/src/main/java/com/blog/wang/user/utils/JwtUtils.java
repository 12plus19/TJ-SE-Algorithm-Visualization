package com.blog.wang.user.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    private static final SecretKey signKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Same key for both signing and parsing
    private static String secretKey="nM1WQp2Dg3eR+iuFv9hQnZboHxWxrymuDpl0bMSOcSQ=";

    private static SecretKey getSigningKey() {
        // 使用 Base64 解码密钥
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // Generation of JWT token
    public static String generateJwt(Map<String, Object> claims) {
        String jwt = Jwts.builder()
                .addClaims(claims)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() + 43200000L)) // 12 hours expiration time
                .compact();
        System.out.println("Generated JWT: " + jwt); // Log the generated token
        return jwt;
    }

    // Parsing of JWT token
    public static Claims parseJWT(String jwt) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
            return claims;
        } catch (Exception e) {
            System.err.println("JWT parsing error: " + e.getMessage());
            throw e;  // Rethrow or handle the error as needed
        }
    }
}
