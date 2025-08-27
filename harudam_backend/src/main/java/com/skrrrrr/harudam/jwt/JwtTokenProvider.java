package com.skrrrrr.harudam.jwt;

import com.skrrrrr.harudam.member.ChildUser;
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
 *
 * application.properties 예시:
 *   jwt.secret=BASE64_ENCODED_256bit_KEY   # openssl rand -base64 32
 *   jwt.expiration=3600000                 # AccessToken 만료(ms)  -> 1시간
 *   jwt.refreshExpiration=1209600000       # RefreshToken 만료(ms) -> 14일
 */
@Component
public class JwtTokenProvider {

    private static final String CLAIM_TOKEN_TYPE = "tokenType";
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
        // secret 은 Base64 로 관리하는 것을 권장 (openssl rand -base64 32)
        byte[] keyBytes = Decoders.BASE64.decode(base64SecretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInMs = accessTokenValidityInMs;
        this.refreshTokenValidityInMs = refreshTokenValidityInMs;
    }

    /* =========================================================================
       생성 (Create)
       ========================================================================= */

    public String createAccessToken(ChildUser childUser) {
        return createAccessToken(childUser.getId());
    }

    public String createAccessToken(Long userId) {
        return createToken(userId, TOKEN_TYPE_ACCESS, accessTokenValidityInMs);
    }

    public String createRefreshToken(ChildUser childUser) {
        return createRefreshToken(childUser.getId());
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, TOKEN_TYPE_REFRESH, refreshTokenValidityInMs);
    }

    private String createToken(Long userId, String tokenType, long validityMs) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(validityMs);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))              // sub = userId
                .setIssuedAt(Date.from(now))                     // iat
                .setExpiration(Date.from(exp))                   // exp
                .addClaims(Map.of(CLAIM_TOKEN_TYPE, tokenType))  // ACCESS/REFRESH 구분
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* =========================================================================
       파싱/검증 (Parse / Validate)
       ========================================================================= */

    /**
     * 서명/만료 검증 (ACCESS/REFRESH 공통)
     */
    public boolean validateToken(String token) {
        try {
            parser().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * ACCESS 전용 검증 (tokenType 체크)
     */
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = parser().parseClaimsJws(token).getBody();
            return TOKEN_TYPE_ACCESS.equals(getTokenType(claims));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * REFRESH 전용 검증 (tokenType 체크)
     */
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = parser().parseClaimsJws(token).getBody();
            return TOKEN_TYPE_REFRESH.equals(getTokenType(claims));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parser().parseClaimsJws(token).getBody();
        return Long.parseLong(claims.getSubject());
    }

    public String getTokenType(String token) {
        Claims claims = parser().parseClaimsJws(token).getBody();
        return getTokenType(claims);
    }

    private String getTokenType(Claims claims) {
        Object typ = claims.get(CLAIM_TOKEN_TYPE);
        return typ == null ? "" : String.valueOf(typ);
    }

    private JwtParser parser() {
        return Jwts.parserBuilder().setSigningKey(key).build();
    }
}
