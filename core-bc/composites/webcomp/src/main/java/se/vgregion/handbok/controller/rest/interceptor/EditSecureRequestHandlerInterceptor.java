package se.vgregion.handbok.controller.rest.interceptor;

public class EditSecureRequestHandlerInterceptor extends BaseSecureRequestHandlerInterceptor {

    @Override
    protected String getRequiredRole() {
        return "edit";
    }
}