package com.documed.backend.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
  private static final String AUTH_COOKIE_NAME = "JwtToken";

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

    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      filterChain.doFilter(request, response);
      return;
    }

    final String requestUri = request.getRequestURI();
    logger.info("Incoming request for URI: {}", requestUri);

    if (requestUri.startsWith("/api/auth")) {
      logger.debug("Skipping authentication for auth endpoint: {}", requestUri);
      filterChain.doFilter(request, response);
      return;
    }

    String jwt = extractJwtFromCookie(request);

    if (jwt == null) {
      logger.debug("No JWT cookie found for {}", requestUri);
      filterChain.doFilter(request, response);
      return;
    }

    try {
      logger.debug(
          "JWT token found (truncated): {}...", jwt.substring(0, Math.min(10, jwt.length())));

      if (!jwtUtil.validateToken(jwt)) {
        logger.warn("Invalid JWT token for {}", requestUri);
        throw new RuntimeException("Invalid token");
      }

      Integer userId = jwtUtil.extractUserId(jwt);
      String role = jwtUtil.extractRole(jwt);
      logger.info("Authenticated user - ID: {}, Role: {}", userId, role);

      var authentication =
          new UsernamePasswordAuthenticationToken(
              userId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      logger.debug("Security context updated for user {}", userId);

    } catch (Exception e) {
      logger.error("Authentication failed for {}: {}", requestUri, e.getMessage());
      clearAuthCookie(response);
      SecurityContextHolder.clearContext();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String extractJwtFromCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (AUTH_COOKIE_NAME.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  private void clearAuthCookie(HttpServletResponse response) {
    logger.debug("Clearing authentication cookie");
    Cookie cookie = new Cookie(AUTH_COOKIE_NAME, "");
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setAttribute("SameSite", "None");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }
}
