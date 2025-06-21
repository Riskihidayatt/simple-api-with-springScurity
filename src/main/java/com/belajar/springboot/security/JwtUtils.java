// src/main/java/com/enigma/live_code_springboot/security/JwtUtils.java
package com.belajar.springboot.security;

// --- Import dari com.auth0.jwt ---
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim; // Import Claim
import com.auth0.jwt.interfaces.DecodedJWT;
// --- Akhir import com.auth0.jwt ---

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class untuk menangani operasi terkait JSON Web Token (JWT).
 * Menggunakan library com.auth0:java-jwt.
 */
@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwt.secret}") // Secret key dari application.properties
    private String jwtSecret;

    @Value("${app.jwt.expirationMs}") // Durasi token dari application.properties
    private long jwtExpirationMs;

    @Value("${app.jwt.issuer}") // Issuer token dari application.properties
    private String jwtIssuer;

    /**
     * Generate token JWT dari objek Authentication Spring Security.
     * @param authentication Objek Authentication yang valid.
     * @return String token JWT atau null jika pembuatan gagal.
     */
    public String generateToken(Authentication authentication) {
        // Pastikan principal adalah UserDetailsImpl yang kita buat
        if (authentication.getPrincipal() instanceof UserDetailsImpl userPrincipal) {
            return generateTokenFromUserDetails(userPrincipal);
        } else {
            log.error("Cannot generate token from principal of type: {}", authentication.getPrincipal().getClass());
            return null; // Atau throw exception
        }
    }

    /**
     * Generate token JWT langsung dari UserDetailsImpl.
     * @param userPrincipal Objek UserDetailsImpl yang berisi data user.
     * @return String token JWT atau null jika pembuatan gagal.
     */
    public String generateTokenFromUserDetails(UserDetailsImpl userPrincipal) {
        try {
            // 1. Tentukan Algoritma (HMAC256 dengan secret key)
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);

            // 2. Siapkan waktu (sekarang dan waktu kedaluwarsa)
            Instant now = Instant.now();
            Instant expiry = now.plusMillis(jwtExpirationMs);

            // 3. Siapkan custom claims (payload tambahan)
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userPrincipal.getId().toString());
            claims.put("email", userPrincipal.getEmail());
            // Opsional: Tambahkan roles jika perlu diakses dari token tanpa ke DB lagi
            // List<String> roles = userPrincipal.getAuthorities().stream()
            //                                  .map(GrantedAuthority::getAuthority)
            //                                  .collect(Collectors.toList());
            // claims.put("roles", roles);

            // 4. Bangun token JWT
            String token = JWT.create()
                    .withIssuer(jwtIssuer)                  // Siapa yang mengeluarkan token
                    .withSubject(userPrincipal.getUsername()) // Username sebagai 'subject' utama
                    .withIssuedAt(Date.from(now))           // Kapan token dibuat
                    .withExpiresAt(Date.from(expiry))       // Kapan token kedaluwarsa
                    .withPayload(claims)                    // Masukkan custom claims (userId, email)
                    // .withClaim("nama_claim_lain", value_lain) // Bisa tambah claim satu per satu juga
                    .sign(algorithm);                       // Tandatangani dengan algoritma

            log.debug("Generated JWT for user '{}', expiring at {}", userPrincipal.getUsername(), expiry);
            return token;

        } catch (JWTCreationException exception){
            log.error("Error creating JWT token for user '{}': {}", userPrincipal.getUsername(), exception.getMessage(), exception); // Log exception juga
            // Di production, mungkin lebih baik throw exception custom
            return null;
        } catch (Exception e) {
            // Tangkap error lain yang mungkin terjadi
            log.error("Unexpected error during JWT generation for user '{}'", userPrincipal.getUsername(), e);
            return null;
        }
    }

    /**
     * Memvalidasi token JWT (memeriksa signature, expiration, dan issuer).
     * @param token Token JWT string.
     * @return true jika token valid, false jika tidak.
     */
    public boolean validateJwtToken(String token) {
        if (token == null || token.isBlank()) {
            log.warn("Validation attempt with null or blank token.");
            return false;
        }
        String tokenStart = token.substring(0, Math.min(token.length(), 10)) + "...";
        try {
            // 1. Siapkan algoritma yang sama dengan saat pembuatan
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            // 2. Bangun verifier dengan algoritma dan issuer yang diharapkan
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(jwtIssuer)
                    .build();
            // 3. Lakukan verifikasi (akan throw exception jika gagal)
            verifier.verify(token);
            log.trace("JWT Token is valid: {}", tokenStart);
            return true;
        } catch (JWTVerificationException exception){
            // Log spesifik jenis error validasi
            log.warn("JWT Verification failed: Type=[{}], Message=[{}] for token: {}",
                    exception.getClass().getSimpleName(),
                    exception.getMessage(),
                    tokenStart);
            return false;
        } catch (Exception e) {
            // Tangkap error tak terduga lainnya
            log.error("Unexpected error during JWT validation for token: {}", tokenStart, e);
            return false;
        }
    }

    /**
     * Mendapatkan username (subject) dari token tanpa verifikasi ulang.
     * Sebaiknya panggil validateJwtToken() terlebih dahulu.
     * @param token Token JWT string.
     * @return Username atau null jika terjadi error decoding.
     */
    public String getUsernameFromJwtToken(String token) {
        try {
            // Decode token (tidak memverifikasi signature/expiration)
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getSubject();
        } catch (Exception e){
            log.error("Error decoding JWT to get username: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Mendapatkan User ID (custom claim 'userId') dari token tanpa verifikasi ulang.
     * Sebaiknya panggil validateJwtToken() terlebih dahulu.
     * @param token Token JWT string.
     * @return UUID User ID atau null jika claim tidak ada atau terjadi error.
     */
    public UUID getUserIdFromJwtToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            Claim userIdClaim = decodedJWT.getClaim("userId"); // Ambil objek Claim
            // Cek jika claim tidak ada atau null
            if (userIdClaim.isNull() || userIdClaim.isMissing()) {
                log.error("userId claim is missing or null in JWT token");
                return null;
            }
            String userIdStr = userIdClaim.asString();
            return UUID.fromString(userIdStr); // Coba parsing ke UUID
        } catch (IllegalArgumentException e) { // Tangkap error parsing UUID
            log.error("Invalid UUID format for userId claim in JWT token: {}", e.getMessage());
            return null;
        } catch (Exception e){ // Tangkap error decode JWT
            log.error("Error decoding/parsing userId from JWT: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Mendapatkan roles (custom claim 'roles') dari token tanpa verifikasi ulang.
     * Sebaiknya panggil validateJwtToken() terlebih dahulu.
     * Mengembalikan list kosong jika claim tidak ada atau bukan list.
     * @param token Token JWT.
     * @return List<String> berisi roles atau list kosong.
     */
    public List<String> getRolesFromJwtToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            Claim rolesClaim = decodedJWT.getClaim("roles");

            if (rolesClaim.isNull() || rolesClaim.isMissing()) {
                log.trace("Claim 'roles' is missing or null in JWT."); // Trace karena ini bisa normal jika roles tidak dimasukkan
                return List.of();
            }

            List<String> roles = rolesClaim.asList(String.class);
            if (roles == null) {
                log.warn("Claim 'roles' exists in JWT but is not a List/Array.");
                return List.of();
            }
            return roles;

        } catch (Exception e){
            log.error("Error decoding/parsing roles claim from JWT: {}", e.getMessage());
            return List.of();
        }
    }
}