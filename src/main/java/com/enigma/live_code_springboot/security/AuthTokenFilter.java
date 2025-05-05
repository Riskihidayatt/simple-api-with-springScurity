// src/main/java/com/enigma/live_code_springboot/security/AuthTokenFilter.java
package com.enigma.live_code_springboot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Pastikan import Slf4j
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Import ini
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j // Aktifkan logging
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService; // Inject Interface

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. Coba parse token dari header
            String jwt = parseJwt(request);
            log.debug("AuthTokenFilter: Processing request for URI: {}", request.getRequestURI());
            log.debug("AuthTokenFilter: Raw Authorization Header: {}", request.getHeader("Authorization")); // Log header mentah
            log.debug("AuthTokenFilter: Parsed JWT: {}", (jwt != null ? "Present (starts with: " + jwt.substring(0, Math.min(10, jwt.length())) +"...)" : "Not Present/Invalid Format"));

            // 2. Jika token ada dan valid
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                log.debug("AuthTokenFilter: JWT is structurally valid and not expired.");
                // 3. Dapatkan username dari token
                String username = jwtUtils.getUsernameFromJwtToken(jwt);
                log.debug("AuthTokenFilter: Username extracted from JWT: '{}'", username);

                // 4. Pastikan username ada dan belum ada autentikasi di context saat ini
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 5. Load UserDetails
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Ini memanggil UserServiceImpl
                    log.debug("AuthTokenFilter: UserDetails loaded successfully for '{}'", username);

                    // 6. Buat objek Authentication
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,        // Principal
                            null,               // Credentials (tidak relevan untuk JWT)
                            userDetails.getAuthorities()); // Roles/Authorities

                    // 7. Set detail tambahan (IP, session ID, etc.)
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 8. Set Authentication di SecurityContext -> USER SEKARANG DIANGGAP LOGIN
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("AuthTokenFilter: Successfully authenticated user '{}' and set SecurityContext", username);
                } else {
                    if (username == null) log.warn("AuthTokenFilter: Username could not be extracted from valid JWT.");
                    if (SecurityContextHolder.getContext().getAuthentication() != null) log.debug("AuthTokenFilter: SecurityContext already contains Authentication for '{}'", SecurityContextHolder.getContext().getAuthentication().getName());
                }
            } else {
                if (jwt != null) {
                    // Jika jwt ada tapi tidak valid (sudah di-log oleh jwtUtils.validateJwtToken)
                    log.debug("AuthTokenFilter: JWT validation failed (check previous logs for details).");
                } else {
                    log.debug("AuthTokenFilter: No valid Bearer token found in request header.");
                }
                // Pastikan context bersih jika tidak ada token valid
                SecurityContextHolder.clearContext();
            }
        } catch (UsernameNotFoundException e) {
            // Tangkap jika user dari token tidak ditemukan di DB
            log.error("AuthTokenFilter: User not found for username extracted from JWT: {}", e.getMessage());
            SecurityContextHolder.clearContext(); // Hapus konteks
        } catch (Exception e) {
            // Tangkap error tak terduga lainnya selama proses filter
            log.error("AuthTokenFilter: Cannot set user authentication", e);
            SecurityContextHolder.clearContext(); // Hapus konteks
        }

        // Lanjutkan request ke filter/servlet berikutnya
        filterChain.doFilter(request, response);
    }

    /**
     * Helper untuk mengekstrak token dari header "Authorization: Bearer <token>".
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Ambil bagian setelah "Bearer "
        }
        return null; // Kembalikan null jika header tidak ada atau format salah
    }
}