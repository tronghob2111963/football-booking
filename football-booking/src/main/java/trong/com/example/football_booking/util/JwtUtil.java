package trong.com.example.football_booking.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    private String secret = "trong11"; // Khóa bí mật để ký token
    private long expiration = 86400000; // Thời gian hết hạn token: 1 ngày (86400000 ms)

    // Tạo token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    // Lấy username từ token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Trích xuất claims từ token
    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // Kiểm tra token có hợp lệ không
    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Kiểm tra token đã hết hạn chưa
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
