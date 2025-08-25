package com.skrrrrr.harudam.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.skrrrrr.harudam.member.ChildUser;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long accessTokenValidityInMilliseconds,
            @Value("${jwt.refresh-expiration:1209600000}") long refreshTokenValidityInMilliseconds // 기본 14일
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }

    // Access Token 생성
    public String createAccessToken(ChildUser childUser) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(String.valueOf(childUser.getId()))	// 사용자 ID
                .claim("role", childUser.getUserType().name()) 	// CHILD | PARENT
                .claim("ststus", childUser.getStatus().name())	// PENDING | ACTIVE | INACTIVE
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(ChildUser childUser) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(String.valueOf(childUser.getId()))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    // AccessToken 만료까지 남은 시간 (초 단위)
    public long getAccessTokenExpiresInSeconds() {
        return this.accessTokenValidityInMilliseconds / 1000;
    }
    
    // RefreshToken 만료 까지 남은 시간 (초 단위)
    public long getRefreshTokenExpiresInSeconds() {
        return this.refreshTokenValidityInMilliseconds / 1000;
    }
    
    // 토큰에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }
    
    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; // 토큰이 유효하지 않으면 false
        }
    }
}
