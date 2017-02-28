package se.vgregion.handbok.controller.rest.interceptor;

import org.junit.Before;
import org.junit.Test;
import se.vgregion.handbok.service.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Patrik Björk
 */
public class EditSecureRequestHandlerInterceptorTest {

    private EditSecureRequestHandlerInterceptor interceptor;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        Field minutesAgeField = JwtUtil.class.getDeclaredField("MINUTES_AGE");
        minutesAgeField.setAccessible(true);
        minutesAgeField.setInt(null, 5);

        interceptor = new EditSecureRequestHandlerInterceptor();
    }

    @Test
    public void preHandle() throws Exception {

        // Prepare
        String jwtToken = JwtUtil.createToken(12345L, "edit");

        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("PUT");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertTrue(result);
    }
}