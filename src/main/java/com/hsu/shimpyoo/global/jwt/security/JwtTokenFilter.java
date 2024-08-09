package com.hsu.shimpyoo.global.jwt.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = jwtTokenProvider.resolveToken(request);
        System.out.println("token: " + token);

        if (token != null) {
            if(jwtTokenProvider.validateToken(token)) {
                jwtTokenProvider.setSecurityContext(token);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

}
