package com.skrrrrr.harudam.jwt;

import com.skrrrrr.harudam.member.ChildUser;
import com.skrrrrr.harudam.member.ParentUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * JWT 발급/검증 유틸리티
 * - Access / Refresh 토큰 모두 지원
 * - claim "tokenType" 으로 ACCESS / REFRESH 구분
 * - claim "userType" 으로 CHILD / PARENT 구분
 */
@Component
public class JwtTokenProvider {

    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String CLAIM_USER_TYPE = "userType";
    private static final String TOKEN_TYPE_ACCESS = "ACCESS";
    private static final String TOKEN_TYPE_REFRESH = "REFRESH";

    private final SecretKey key;
    private final long accessTokenValidityInMs;
    private final long refreshTokenValidityInMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String base64SecretKey,
            @Value("${jwt.expiration}") long accessTokenValidityInMs,
            @Value("${jwt.refreshExpiration}") long refreshTokenValidityInMs
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(base64SecretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInMs = accessTokenValidityInMs;
        this.refreshTokenValidityInMs = refreshTokenValidityInMs;
    }

    /* =========================================================================
       생성 (Create)
       ========================================================================= */

    // 자녀
    public String createAccessToken(ChildUser childUser) {
        return createToken(childUser.getId(), "CHILD", TOKEN_TYPE_ACCESS, accessTokenValidityInMs);
    }

    public String createRefreshToken(ChildUser childUser) {
        return createToken(childUser.getId(), "CHILD", TOKEN_TYPE_REFRESH, refreshTokenValidityInMs);
    }

    // 부모
    public String createAccessToken(ParentUser parentUser) {
        return createToken(parentUser.getId(), "PARENT", TOKEN_TYPE_ACCESS, accessTokenValidityInMs);
    }

    public String createRefreshToken(ParentUser parentUser) {
        return createToken(parentUser.getId(), "PARENT", TOKEN_TYPE_REFRESH, refreshTokenValidityInMs);
    }

    private String createToken(Long userId, String userType, String tokenType, long validityMs) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(validityMs);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .addClaims(Map.of(
                        CLAIM_TOKEN_TYPE, tokenType,
                        CLAIM_USER_TYPE, userType
                ))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* =========================================================================
       파싱/검증 (Parse / Validate)
       ========================================================================= */

    public boolean validateToken(String token) {
        try {
            parser().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateAccessToken(String token) {
        try {
            Claims claims = parser().parseClaimsJws(token).getBody();
            return TOKEN_TYPE_ACCESS.equals(claims.get(CLAIM_TOKEN_TYPE, String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = parser().parseClaimsJws(token).getBody();
            return TOKEN_TYPE_REFRESH.equals(claims.get(CLAIM_TOKEN_TYPE, String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parser().parseClaimsJws(token).getBody();
        return Long.parseLong(claims.getSubject());
    }

    public String getUserTypeFromToken(String token) {
        Claims claims = parser().parseClaimsJws(token).getBody();
        return claims.get(CLAIM_USER_TYPE, String.class);
    }

    private JwtParser parser() {
        return Jwts.parserBuilder().setSigningKey(key).build();
    }
}
