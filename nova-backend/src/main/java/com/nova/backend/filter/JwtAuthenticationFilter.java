package com.nova.backend.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.backend.common.Result;
import com.nova.backend.config.JwtProperties;
import com.nova.backend.entity.User;
import com.nova.backend.service.UserService;
import com.nova.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * JWT认证过滤器
 *
 * @author Nova
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String actualPath = contextPath.isEmpty() ? requestUri : requestUri.substring(contextPath.length());
        
        log.debug("JWT过滤器 - 请求URI: {}, ContextPath: {}, 实际路径: {}", requestUri, contextPath, actualPath);

        if (isWhiteListed(actualPath)) {
            log.debug("JWT过滤器 - 白名单放行: {}", actualPath);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(jwtProperties.getHeader());

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(jwtProperties.getPrefix())) {
            log.debug("JWT过滤器 - 未提供认证Token: {}", actualPath);
            writeUnauthorizedResponse(response, "未提供认证Token");
            return;
        }

        String token = authHeader.substring(jwtProperties.getPrefix().length() + 1);

        try {
            if (!jwtUtil.validateToken(token)) {
                log.debug("JWT过滤器 - Token无效或已过期: {}", actualPath);
                writeUnauthorizedResponse(response, "Token无效或已过期");
                return;
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            User user = userService.getById(userId);

            if (user == null) {
                writeUnauthorizedResponse(response, "用户不存在");
                return;
            }

            if (user.getStatus() != 1) {
                writeUnauthorizedResponse(response, "账号已被禁用");
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.warn("JWT认证失败: {}", e.getMessage());
            writeUnauthorizedResponse(response, "Token验证失败: " + e.getMessage());
        }
    }

    private boolean isWhiteListed(String actualPath) {
        if (jwtProperties.getWhiteList() == null || jwtProperties.getWhiteList().isEmpty()) {
            return false;
        }
        for (String pattern : jwtProperties.getWhiteList()) {
            if (pathMatcher.match(pattern, actualPath)) {
                return true;
            }
        }
        return false;
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        
        Result<Void> result = Result.unauthorized(message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
