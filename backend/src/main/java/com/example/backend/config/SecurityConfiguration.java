package com.example.backend.config;

import com.example.backend.entity.RestBean;
import com.example.backend.entity.vo.response.AuthorizeVO;
import com.example.backend.filter.JwtAuthorizeFilter;
import com.example.backend.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {
    @Resource
    JwtUtils jwt;

    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf->conf
                        .requestMatchers(("/api/auth/**")).permitAll()
                        .anyRequest().authenticated())
                .formLogin(conf->conf
                        .loginProcessingUrl("/api/auth/login")
                        .failureHandler(this::onAuthenticationFailure)
                        .successHandler(this::onAuthenticationSuccess))
                .logout(conf->conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess))
                .exceptionHandling(conf->conf.authenticationEntryPoint(this::onUnauthorized)
                        .accessDeniedHandler(this::onAccessDeniedhandler))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf->conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private void onAccessDeniedhandler(HttpServletRequest request, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write(RestBean.failure(403,"拒绝访问").asJsonString());
        System.out.println(RestBean.failure(403,"拒绝访问").asJsonString());
    }

    private void onUnauthorized(HttpServletRequest request, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write(RestBean.failure(401,"未通过认证").asJsonString());
        System.out.println(RestBean.failure(401,"未通过认证").asJsonString());
    }

    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String authorization=request.getHeader("Authorization");
        if (jwt.invalidateJwt(authorization)){
            writer.write(RestBean.success().asJsonString());
        }
        else {
            writer.write(RestBean.failure(400,"退出登陆失败").asJsonString());
        }

        System.out.println("logout");
    }




public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        User user=(User) authentication.getPrincipal();
        String token = jwt.createJwt(user,233,"user");
        AuthorizeVO vo=new AuthorizeVO();
        vo.setExpire(jwt.GetExpireTime());
        vo.setRole("user");
        vo.setUsername("xiaoming");
        vo.setToken(token);
        System.out.println(RestBean.success(vo).asJsonString());
        httpServletResponse.getWriter().write(RestBean.success(vo).asJsonString());

    }

    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write("fail");
    }
}