package ru.kata.spring.boot_security.demo.configs;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {

    private final Map<String, String> roleRedirectMap;

    public SuccessUserHandler() {
        roleRedirectMap = new HashMap<>();
        roleRedirectMap.put("ROLE_ADMIN", "/admin");
        roleRedirectMap.put("ROLE_USER", "/user");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        for (String role : roles) {
            if (roleRedirectMap.containsKey(role)) {
                httpServletResponse.sendRedirect(roleRedirectMap.get(role));
                return;
            }
        }
        httpServletResponse.sendRedirect("/");
    }
}