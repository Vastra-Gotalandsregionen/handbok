package se.vgregion.handbok.controller.rest.interceptor;

public class SecureRequestHandlerInterceptor extends BaseSecureRequestHandlerInterceptor {

    @Override
    protected String getRequiredRole() {
        return "admin";
    }
}