package com.example.security.filter;

import com.example.dto.RequestRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;


public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    public JsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
        super("/members/login");
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {

        // 1. JSON -> DTO 변환
        RequestRecord.LoginRequestRecord loginRequestRecord =
                objectMapper.readValue(request.getInputStream(), RequestRecord.LoginRequestRecord.class);

        // 2. Token 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequestRecord.id(),
                loginRequestRecord.passwd()
        );

        // 3. AuthenticationManager에게 인증 위임
        return this.getAuthenticationManager().authenticate(authToken);
    }
}
