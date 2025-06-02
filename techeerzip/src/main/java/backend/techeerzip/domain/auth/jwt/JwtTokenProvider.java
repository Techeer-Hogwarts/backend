package backend.techeerzip.domain.auth.jwt;

import java.security.Key;
import java.util.Date;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import backend.techeerzip.domain.auth.dto.token.TokenPair;
import backend.techeerzip.domain.auth.exception.InvalidJwtTokenException;
import backend.techeerzip.global.logger.CustomLogger;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60; // 1시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7; // 7일

    private final CustomLogger logger;

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    // secretKey 인코딩
    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenPair generateTokenPair(Authentication authentication) {
        // 각 유저당 권한 하나씩
        String authority = authentication.getAuthorities().iterator().next().getAuthority();

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        long now = new Date().getTime();

        String accessToken =
                Jwts.builder()
                        .setSubject(authentication.getName())
                        .claim("userId", userId)
                        .claim(AUTHORITIES_KEY, authority)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME))
                        .signWith(key, SignatureAlgorithm.HS512)
                        .compact();

        String refreshToken =
                Jwts.builder()
                        .setSubject(authentication.getName())
                        .claim("userId", userId)
                        .claim(AUTHORITIES_KEY, authority)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                        .signWith(key, SignatureAlgorithm.HS512)
                        .compact();

        logger.info(String.format("토큰 생성 완료 - email: %s", authentication.getName()));
        return new TokenPair(accessToken, refreshToken);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        String email = claims.getSubject();
        Long userId = claims.get("userId", Long.class);
        String role = claims.get(AUTHORITIES_KEY, String.class);

        GrantedAuthority authority = new SimpleGrantedAuthority(role);
        List<GrantedAuthority> authorities = List.of(authority);

        CustomUserPrincipal principal = new CustomUserPrincipal(userId, email, "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("만료된 JWT 토큰입니다.");
            // 리프레시 토큰 검증
            return false;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.warn("잘못된 JWT 서명입니다.");
        } catch (UnsupportedJwtException e) {
            logger.warn("지원되지 않는 JWT 토큰입니다.");
        }
        throw new InvalidJwtTokenException();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
