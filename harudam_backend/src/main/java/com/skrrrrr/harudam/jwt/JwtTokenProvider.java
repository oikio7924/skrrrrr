package com.skrrrrr.harudam.jwt;

import com.skrrrrr.harudam.member.ChildUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenValidityInMilliseconds;

    // application.properties에 설정한 JWT 비밀키와 만료 시간을 가져옵니다.
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.expiration}") long accessTokenValidityInMilliseconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
    }

    // ChildUser 정보를 받아 Access Token을 생성합니다.
    public String createAccessToken(ChildUser childUser) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(String.valueOf(childUser.getId())) // 토큰의 주체(subject)를 자녀의 고유 ID로 설정
                .setIssuedAt(now)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(validity)
                .compact();
    }

    // 토큰에서 사용자 ID를 추출합니다.
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject());
    }

    // 토큰의 유효성과 만료일자를 확인합니다.
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 유효하지 않을 경우 (변조, 만료 등) false를 반환합니다.
            return false;
        }
    }
}
