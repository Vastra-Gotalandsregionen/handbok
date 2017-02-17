package se.vgregion.ifeedpoc.controller.rest.interceptor;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import se.vgregion.ifeedpoc.service.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class BaseSecureRequestHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseSecureRequestHandlerInterceptor.class);

    public final static List<String> METHODS_TO_CHECK = Collections.unmodifiableList(Arrays.asList("POST", "PUT", "DELETE"));

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

        if (METHODS_TO_CHECK.contains(request.getMethod().toUpperCase())) {

            try {
                String authorizationHeader = request.getHeader("Authorization");
                if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }

                String jwtToken = authorizationHeader.substring("Bearer".length()).trim();

                DecodedJWT jwt;
                try {
                    jwt = JwtUtil.verify(jwtToken);
                } catch (JWTVerificationException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }

                List<String> roles = jwt.getClaim("roles").asList(String.class);

                if (roles == null || !roles.contains(getRequiredRole())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                } else {
                    return true;
                }
            } catch (JWTDecodeException exception) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        } else {
            return true;
        }
    }

    protected abstract String getRequiredRole();

}