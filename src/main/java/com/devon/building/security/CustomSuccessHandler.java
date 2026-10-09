package com.devon.building.security;

import com.devon.building.constant.SystemConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        handle(request, response, authentication);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        if (isAdmin(roles)) {
            return SystemConstant.ADMIN_HOME;
        }

        if (isUser(roles)) {
            return "/";
        }

        return "/access-denied";
    }

    private boolean isAdmin(List<String> roles) {
        return roles.stream().anyMatch(role -> role.startsWith(SystemConstant.MANAGER_ROLE) || role.startsWith(SystemConstant.STAFF_ROLE));
    }

    private boolean isUser(List<String> roles) {
        return roles.contains(SystemConstant.USER_ROLE);
    }
}
