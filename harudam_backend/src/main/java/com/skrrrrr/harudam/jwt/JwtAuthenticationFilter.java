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

/**
 * JWT 인증 필터
 * - Authorization 헤더의 Bearer ACCESS 토큰만 허용
 * - REFRESH 토큰은 인증용으로는 거부
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Authorization 헤더에서 토큰 추출
        String token = resolveToken(request);

        // 2. 유효한 ACCESS 토큰인지 검증
        if (token != null && jwtTokenProvider.validateAccessToken(token)) {
            Long userId = jwtTokenProvider.getUserIdFromToken(token);

            UserDetails userDetails = new User(
                    userId.toString(),
                    "",
                    Collections.emptyList()
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            "",
                            userDetails.getAuthorities()
                    );

            // SecurityContext 에 저장 → 컨트롤러에서 @AuthenticationPrincipal 등으로 접근 가능
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. 다음 필터 진행
        filterChain.doFilter(request, response);
    }

    // Authorization 헤더 → Bearer <token> 파싱
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
