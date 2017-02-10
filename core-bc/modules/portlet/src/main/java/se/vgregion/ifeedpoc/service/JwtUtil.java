package se.vgregion.ifeedpoc.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * @author Patrik Björk
 */
public class JwtUtil {

    private static final String secret = UUID.randomUUID().toString();

    public static String createToken(boolean isAdmin, long userId) {
        try {
            Date timeAhead = Date.from(Instant.now().plus(2, ChronoUnit.HOURS));
            Date now = Date.from(Instant.now());
            return JWT.create()
                    .withSubject(String.valueOf(userId))
                    .withClaim("isAdmin", isAdmin)
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
