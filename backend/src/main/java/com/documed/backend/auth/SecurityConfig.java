package com.documed.backend.auth;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final Environment env;

  @Value("${allowed_origins}")
  private String[] allowedOrigins;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, Environment env) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.env = env;
  }

  @Bean
  public static MethodValidationPostProcessor methodValidationPostProcessor() {
    return new MethodValidationPostProcessor();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth -> {
              auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                  .permitAll()
                  .requestMatchers("/api/auth/login")
                  .permitAll()
                  .requestMatchers("/api/auth/request-registration")
                  .permitAll()
                  .requestMatchers("/api/auth/confirm-registration")
                  .permitAll()
                  .requestMatchers("/api/auth/request-password-reset")
                  .permitAll()
                  .requestMatchers("/api/auth/reset-password")
                  .permitAll()
                  .requestMatchers("/api/facilities")
                  .permitAll()
                  .requestMatchers("/actuator/health")
                  .permitAll();

              if (env.acceptsProfiles("local")) {
                auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**")
                    .permitAll();
              }

              auth.anyRequest().authenticated();
            })
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
    System.out.println("Allowed CORS origins: " + Arrays.asList(allowedOrigins));
    configuration.setAllowedMethods(Arrays.asList("*"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
