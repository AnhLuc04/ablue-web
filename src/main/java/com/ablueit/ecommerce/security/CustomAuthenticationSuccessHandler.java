package com.ablueit.ecommerce.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import java.io.IOException;
import java.util.Collection;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String redirectURL = "/home"; // Mặc định nếu không tìm thấy role phù hợp

        label:
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            switch (role) {
                case "ROLE_ADMIN":
                    redirectURL = "/admin/dashboard";
                    break label;
                case "ROLE_SELLER":
                    redirectURL = "/seller/dashboard";
                    break label;
                case "ROLE_USER":
                    redirectURL = "/user/dashboard";
                    break label;
            }
        }

        response.sendRedirect(request.getContextPath() + redirectURL);
    }
}
