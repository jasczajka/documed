package com.documed.backend.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String requestUri = request.getRequestURI();

    logger.info("Request coming at {}", requestUri);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      logger.debug("No JWT token found for {}", requestUri);
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String jwt = authHeader.substring(7);
      logger.debug(
          "JWT token received (truncated): {}...", jwt.substring(0, Math.min(10, jwt.length())));

      if (!jwtUtil.validateToken(jwt)) {
        logger.warn("Invalid JWT token for {}", requestUri);
        throw new RuntimeException("Invalid token");
      }

      Integer userId = jwtUtil.extractUserId(jwt);
      String role = jwtUtil.extractRole(jwt);
      logger.info("Authenticated user ID: {}, role: {}", userId, role);

      var authentication =
          new UsernamePasswordAuthenticationToken(
              userId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      logger.debug("Security context updated");

    } catch (Exception e) {
      logger.error("Authentication failed: {}", e.getMessage());
      SecurityContextHolder.clearContext();
      response.sendError(
          HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed: " + e.getMessage());
      return;
    }

    filterChain.doFilter(request, response);
  }
}
