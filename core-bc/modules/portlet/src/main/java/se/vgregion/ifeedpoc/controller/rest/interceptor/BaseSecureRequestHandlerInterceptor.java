package se.vgregion.ifeedpoc.controller.rest.interceptor;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.liferay.portal.kernel.servlet.ProtectedServletRequest;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.CompanyThreadLocal;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import se.vgregion.ifeedpoc.service.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public abstract class BaseSecureRequestHandlerInterceptor extends HandlerInterceptorAdapter {

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

                DecodedJWT jwt = JwtUtil.verify(jwtToken);

                if (jwt == null || jwt.getExpiresAt() == null || jwt.getExpiresAt().before(new Date())) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                } else {
                    String subject = jwt.getSubject();

                    if (subject != null) {
                        setCredentials(request, request.getSession(), Long.parseLong(subject),
                                HttpServletRequest.FORM_AUTH);
                    }

                    List<String> roles = jwt.getClaim("roles").asList(String.class);

                    if (roles == null || !roles.contains(getRequiredRole())) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        return false;
                    } else {
                        return true;
                    }
                }
            } catch (JWTDecodeException exception){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        } else {
            return true;
        }
    }

    protected abstract String getRequiredRole();

    protected HttpServletRequest setCredentials(
            HttpServletRequest request, HttpSession session, Long userId,
            String authType)
            throws Exception {

        User user = UserLocalServiceUtil.getUser(userId);

        String userIdString = String.valueOf(userId);

        request = new ProtectedServletRequest(request, userIdString, authType);

        session.setAttribute(WebKeys.USER, user);
        session.setAttribute(_AUTHENTICATED_USER, userIdString);

        initThreadLocals(request);

        return request;
    }

    protected void initThreadLocals(HttpServletRequest request)
            throws Exception {

        HttpSession session = request.getSession();

        User user = (User) session.getAttribute(WebKeys.USER);

        CompanyThreadLocal.setCompanyId(user.getCompanyId());

        PrincipalThreadLocal.setName(user.getUserId());
        PrincipalThreadLocal.setPassword(PortalUtil.getUserPassword(request));

        if (!_usePermissionChecker) {
            return;
        }

        PermissionChecker permissionChecker =
                PermissionCheckerFactoryUtil.create(user);

        PermissionThreadLocal.setPermissionChecker(permissionChecker);
    }


    protected void initThreadLocals(long userId)
            throws Exception {


        User user = UserLocalServiceUtil.getUser(userId);

        CompanyThreadLocal.setCompanyId(user.getCompanyId());

        PrincipalThreadLocal.setName(user.getUserId());

        if (!_usePermissionChecker) {
            return;
        }

        PermissionChecker permissionChecker =
                PermissionCheckerFactoryUtil.create(user);

        PermissionThreadLocal.setPermissionChecker(permissionChecker);
    }

    public void setUsePermissionChecker(boolean usePermissionChecker) {
        this._usePermissionChecker = usePermissionChecker;
    }


    private static final String _AUTHENTICATED_USER =
            BaseSecureRequestHandlerInterceptor.class + "_AUTHENTICATED_USER";

    private boolean _usePermissionChecker = true;

}