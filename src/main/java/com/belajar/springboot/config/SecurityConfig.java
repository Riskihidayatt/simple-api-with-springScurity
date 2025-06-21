// src/main/java/com/enigma/live_code_springboot/config/SecurityConfig.java
package com.belajar.springboot.config;

import com.belajar.springboot.security.AuthEntryPointJwt;
import com.belajar.springboot.security.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Konfigurasi utama Spring Security.
 * Mengatur filter chain, autentikasi, dan otorisasi.
 */
@Configuration
@EnableWebSecurity // Mengaktifkan dukungan Spring Security untuk web
@EnableMethodSecurity(prePostEnabled = true) // Mengaktifkan anotasi @PreAuthorize, @PostAuthorize
@RequiredArgsConstructor // Inject semua final fields via constructor
public class SecurityConfig {

    // Inject dependencies yang dibutuhkan
    private final UserDetailsService userDetailsService; // Dari UserServiceImpl
    private final AuthTokenFilter authTokenFilter;     // Filter JWT kustom
    private final AuthEntryPointJwt unauthorizedHandler; // Handler untuk error 401
    private final PasswordEncoder passwordEncoder;     // Dari BeanConfiguration

    /**
     * Bean untuk DaoAuthenticationProvider.
     * Menggunakan UserDetailsService untuk load user dan PasswordEncoder untuk cek password.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Set service untuk load user detail
        authProvider.setPasswordEncoder(passwordEncoder); // Set encoder untuk password
        return authProvider;
    }

    /**
     * Bean untuk AuthenticationManager.
     * Dibutuhkan oleh AuthServiceImpl untuk proses login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Bean utama yang mendefinisikan SecurityFilterChain.
     * Mengatur bagaimana request HTTP diamankan.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Nonaktifkan CSRF (umum untuk API stateless)
                .csrf(AbstractHttpConfigurer::disable)
                // 2. Atur exception handling untuk entry point (error 401)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                // 3. Atur session management menjadi STATELESS (tidak pakai HTTP session)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4. Atur aturan otorisasi untuk setiap request
                .authorizeHttpRequests(auth -> auth
                        // --- ENDPOINT PUBLIK ---
                        .requestMatchers("/api/auth/**").permitAll() // Login & Register bebas akses

                        // --- ENDPOINT USER --- (Menggunakan hasAuthority)
                        .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/profile").authenticated()

                        // --- ENDPOINT CUSTOMER --- (Menggunakan hasAnyAuthority)
                        .requestMatchers("/api/customers/**").hasAnyAuthority("ADMIN", "STAFF")

                        // --- ENDPOINT PRODUCT --- (Menggunakan hasAuthority / hasAnyAuthority)
                        .requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyAuthority("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/api/products").hasAnyAuthority("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyAuthority("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("ADMIN") // Delete hanya Admin

                        // --- ENDPOINT TAX --- (Asumsi hanya ADMIN - Gunakan hasAuthority)
                        // .requestMatchers("/api/taxes/**").hasAuthority("ADMIN") // Aktifkan jika ada TaxController

                        // --- ENDPOINT TRANSACTION --- (Menggunakan hasAuthority / hasAnyAuthority)
                        .requestMatchers(HttpMethod.POST, "/api/transactions").hasAnyAuthority("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PUT, "/api/transactions/**/status").hasAnyAuthority("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/transactions/**").authenticated() // Lihat perlu login (detail di service)

                        // --- ENDPOINT REPORT ---
                        .requestMatchers(HttpMethod.GET, "/api/reports/**").authenticated() // Lihat perlu login (detail di service)

                        // --- ATURAN DEFAULT ---
                        // Semua request lain yang tidak cocok di atas harus diautentikasi
                        .anyRequest().authenticated()
                )
                // 5. Set Authentication Provider yang kita definisikan
                .authenticationProvider(authenticationProvider())
                // 6. Tambahkan filter JWT kustom sebelum filter autentikasi standar
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // 7. Bangun konfigurasi HttpSecurity
        return http.build();
    }
}