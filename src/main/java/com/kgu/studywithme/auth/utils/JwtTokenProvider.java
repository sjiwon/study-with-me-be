package com.kgu.studywithme.auth.utils;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JwtTokenProvider implements TokenProvider {
    private final SecretKey secretKey;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") final String secretKey,
            @Value("${jwt.access-token-validity}") final long accessTokenValidityInMilliseconds,
            @Value("${jwt.refresh-token-validity}") final long refreshTokenValidityInMilliseconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }

    @Override
    public String createAccessToken(final Long memberId) {
        return createToken(memberId, accessTokenValidityInMilliseconds);
    }

    @Override
    public String createRefreshToken(final Long memberId) {
        return createToken(memberId, refreshTokenValidityInMilliseconds);
    }

    private String createToken(final Long memberId, final long validityInMilliseconds) {
        final Claims claims = Jwts.claims();
        claims.put("id", memberId);

        // Expires At
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime tokenValidity = now.plusSeconds(validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Long getId(final String token) {
        return getClaims(token)
                .getBody()
                .get("id", Long.class);
    }

    @Override
    public void validateToken(final String token) {
        try {
            final Jws<Claims> claims = getClaims(token);
            final Date expiredDate = claims.getBody().getExpiration();
            final Date now = new Date();

            if (expiredDate.before(now)) {
                throw StudyWithMeException.type(AuthErrorCode.INVALID_TOKEN);
            }
        } catch (final ExpiredJwtException |
                       SecurityException |
                       MalformedJwtException |
                       UnsupportedJwtException |
                       IllegalArgumentException e) {
            throw StudyWithMeException.type(AuthErrorCode.INVALID_TOKEN);
        }
    }

    private Jws<Claims> getClaims(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }
}
