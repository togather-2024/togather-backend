package com.togather.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;

@Slf4j
@Service
public class JwtProvider {
    private final Key secretKey;
    private final long tokenValidityInMilliSeconds;

    private final String ROLE = "role";

    public JwtProvider(@Value("${jwt.secret}") String secretKey, @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        tokenValidityInMilliSeconds = tokenValidityInSeconds * 1000;
    }

    // Generate token based on user info
    public String generateToken(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .findFirst().map(GrantedAuthority::getAuthority).orElse("ROLE_NONE");

        Date expireDateTime = new Date((new Date()).getTime() + tokenValidityInMilliSeconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(ROLE, role)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .setExpiration(expireDateTime)
                .compact();
    }

    // Get info of user by decoding token
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<GrantedAuthority> grantedAuthorities = Collections.singletonList(
                new SimpleGrantedAuthority(claims.get(ROLE).toString())
        );

        User principal = new User(claims.getSubject(), "", grantedAuthorities);

        return new UsernamePasswordAuthenticationToken(principal, token, grantedAuthorities);
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

}
