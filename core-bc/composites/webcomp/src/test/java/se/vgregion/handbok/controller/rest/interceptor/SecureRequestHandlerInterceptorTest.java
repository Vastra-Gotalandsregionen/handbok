package se.vgregion.handbok.controller.rest.interceptor;

import org.junit.Before;
import org.junit.Test;
import se.vgregion.handbok.service.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.lang.reflect.Field;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Patrik Björk
 */
public class SecureRequestHandlerInterceptorTest {

    private SecureRequestHandlerInterceptor interceptor;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        Field minutesAgeField = JwtUtil.class.getDeclaredField("MINUTES_AGE");
        minutesAgeField.setAccessible(true);
        minutesAgeField.setInt(null, 5);

        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(null, "secret");

        interceptor = new SecureRequestHandlerInterceptor();
    }

    @Test
    public void preHandle() throws Exception {

        // Prepare
        String jwtToken = JwtUtil.createToken(12345L, "admin");

        // Given
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
    public void preHandleMissingAuthorizationHeader() throws Exception {

        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("PUT");
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        verify(response).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED));
        assertFalse(result);
    }

    @Test
    public void preHandleExpiredJwt() throws Exception {

        // Prepare
        Field minutesAgeField = JwtUtil.class.getDeclaredField("MINUTES_AGE");
        minutesAgeField.setAccessible(true);
        minutesAgeField.setInt(null, 0);

        String jwtToken = JwtUtil.createToken(12345L, "admin");

        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("PUT");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        // When
        // Wait one second to be sure we get to the next second to make it expired.
        Thread.sleep(1000);
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        verify(response).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED));
        assertFalse(result);
    }

    @Test
    public void preHandleNotAdmin() throws Exception {

        // Prepare
        String jwtToken = JwtUtil.createToken(12345L, "guestOrWhatever");

        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("PUT");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN));
        assertFalse(result);
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
