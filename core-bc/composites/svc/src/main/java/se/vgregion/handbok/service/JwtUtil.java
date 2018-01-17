package se.vgregion.handbok.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author Patrik Björk
 */
@Service
public class JwtUtil {

    private static String secret;
    private static int MINUTES_AGE = 5;

    @Value("${jwt.secret}")
    private String injectedSecret;

    @PostConstruct
    public void init() {
        secret = injectedSecret;
    }

    public static String createToken(Long userId, String... roles) {
        try {
            Date timeAhead = Date.from(Instant.now().plus(MINUTES_AGE, ChronoUnit.MINUTES));
            Date now = Date.from(Instant.now());
            return JWT.create()
                    .withSubject(userId != null ? String.valueOf(userId) : null)
                    .withArrayClaim("roles", roles)
                    .withIssuedAt(now)
                    .withExpiresAt(timeAhead)
                    .sign(Algorithm.HMAC256(secret));
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static DecodedJWT verify(String jwtToken) throws UnsupportedEncodingException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
        return verifier.verify(jwtToken);
    }
}
