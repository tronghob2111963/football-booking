package trong.com.example.football_booking.config;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import trong.com.example.football_booking.entity.User;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;
    public String generateToken(Authentication authentication) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        String username = user.getUsername();
        String role = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpiration * 1000L))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
//    public String generateToken(Authentication authentication) {
//        User user = (User) authentication.getPrincipal();
//        return Jwts.builder()
//                .setSubject(user.getUsername())
//                .claim("role", user.getRole().name())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(new Date().getTime() + jwtExpiration * 1000L))
//                .signWith(SignatureAlgorithm.HS512, jwtSecret)
//                .compact();
//    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}