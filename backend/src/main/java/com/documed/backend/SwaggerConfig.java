package com.documed.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SwaggerConfig {

  private final Environment env;

  public SwaggerConfig(Environment env) {
    this.env = env;
  }

  @Bean
  public SecurityFilterChain localSecurityFilterChain(HttpSecurity http) throws Exception {
    // when running with 'local' profile allow all requests to swagger
    boolean isLocal = env.acceptsProfiles("local");

    http.securityMatcher("/swagger-ui/**", "/v3/api-docs/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

    if (!isLocal) {
      http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
    }

    return http.build();
  }
}
