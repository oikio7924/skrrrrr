package com.skrrrrr.harudam.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Request Header에서 토큰을 꺼냅니다.
        String token = resolveToken(request);

        // 2. 토큰이 유효한지 확인합니다.
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효하면, 토큰에서 사용자 ID를 추출하여 인증 정보를 생성합니다.
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            UserDetails userDetails = new User(userId.toString(), "", Collections.emptyList());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
            
            // SecurityContext에 인증 정보를 저장합니다.
            // 이렇게 하면 해당 요청을 처리하는 동안 사용자가 인증된 것으로 간주됩니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response);
    }

    // Request Header에서 "Bearer " 접두사를 제거하고 순수한 토큰 값만 추출합니다.
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
