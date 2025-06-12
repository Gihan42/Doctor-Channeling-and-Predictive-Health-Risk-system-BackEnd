package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.filter;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.util.JWTUtil;
import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;
        Map<String, Long> map = null;
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                jwt = authorizationHeader.substring(7);
                try {
                    email = jwtUtil.extractUser(jwt);
                    Claims claims = jwtUtil.extractAllClaims(jwt);
                    request.setAttribute("user",email);
                    request.setAttribute("type",claims.get("type"));
                } catch (Exception e) {
                    request.setAttribute("code", 401);
                    request.setAttribute("message", "Expired JWT token");
                    request.setAttribute("error",e.getMessage());
                    SecurityContextHolder.clearContext();
                }
            } else {
                request.setAttribute("code", 404);
                request.setAttribute("message", "Token is Missing");
                request.setAttribute("error", "Token is null");
                SecurityContextHolder.clearContext();
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
                if (jwtUtil.validateToken(jwt, userDetails)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(map);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    request.setAttribute("code", 401);
                    request.setAttribute("message", "Token Exception");
                    request.setAttribute("error", "Token is not valid");
                    SecurityContextHolder.clearContext();
                    throw new AuthenticationCredentialsNotFoundException("Token is not valid");
                }
            }
        } catch (Exception e) {
            request.setAttribute("code", 400);
            request.setAttribute("message", "Filter Exception");
            request.setAttribute("error",e.getMessage());
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}
