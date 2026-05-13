package bus_ticket_reservation_system.ticket_reservation.security;

import bus_ticket_reservation_system.ticket_reservation.DTO.JWTAuthDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class JwtService {

    private static final Logger LOGGER = LogManager.getLogger(JwtService.class);
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration-minutes}")
    private int accessExpirationMinutes;

    @Value("${jwt.refresh-expiration-days}")
    private int refreshExpirationDays;

    public JWTAuthDTO generateAuthToken(String email) {
        JWTAuthDTO jwtAuthDTO = new JWTAuthDTO();
        jwtAuthDTO.setToken(generateJwtToken(email));
        jwtAuthDTO.setRefreshToken(generateRefreshToken(email));
        return jwtAuthDTO;
    }
    public JWTAuthDTO refreshBaseToken(String email, String refreshToken) {
        JWTAuthDTO jwtAuthDTO = new JWTAuthDTO();
        jwtAuthDTO.setToken(generateJwtToken(email));
        jwtAuthDTO.setRefreshToken(refreshToken);
        return jwtAuthDTO;
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        }
        catch (ExpiredJwtException expEx) {
            LOGGER.error("Expired JwtException", expEx);
        }
        catch (UnsupportedJwtException expEx) {
            LOGGER.error("UnsupportedJwtException", expEx);
        }
        catch (MalformedJwtException expEx) {
            LOGGER.error("MalformedJwtException", expEx);
        }
        catch (SecurityException expEx) {
            LOGGER.error("SecurityException", expEx);
        }
        catch (Exception expEx) {
            LOGGER.error("InvalidToken", expEx);
        }
        return false;
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    private String generateJwtToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(accessExpirationMinutes).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email).expiration(date).signWith(getSignInKey()).compact();
    }

    private String generateRefreshToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusDays(refreshExpirationDays).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email).expiration(date).signWith(getSignInKey()).compact();
    }

    private SecretKey getSignInKey() {
        byte[] ketBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(ketBytes);
    }
}
