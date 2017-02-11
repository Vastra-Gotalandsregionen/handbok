package se.vgregion.ifeedpoc.controller.rest.interceptor;

import org.junit.Test;
import se.vgregion.ifeedpoc.service.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Patrik Björk
 */
public class SecureRequestHandlerInterceptorTest {

    @Test
    public void preHandle() throws Exception {

        // Prepare
        boolean isAdmin = true;
        String jwtToken = JwtUtil.createToken(isAdmin, 12345L);

        // Given
        SecureRequestHandlerInterceptor interceptor = new SecureRequestHandlerInterceptor() {
            @Override
            protected HttpServletRequest setCredentials(HttpServletRequest request, HttpSession session, Long userId,
                                                        String authType) throws Exception {
                return null;
            }
        };

        String cookieNameSecondPart = "aslokdfj";

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("PUT");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(request.getRequestURI()).thenReturn("/" + cookieNameSecondPart + "/askjdf");

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertTrue(result);
    }

    @Test
    public void preHandleGetMethod() throws Exception {

        // Given
        SecureRequestHandlerInterceptor interceptor = new SecureRequestHandlerInterceptor();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("GET");

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertTrue(result);
    }

}