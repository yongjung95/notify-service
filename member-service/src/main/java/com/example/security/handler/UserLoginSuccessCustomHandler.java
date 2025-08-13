package com.example.security.handler;

import com.example.dto.CommonResultRecord;
import com.example.dto.ResponseRecord;
import com.example.security.CustomUserDetails;
import com.example.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLoginSuccessCustomHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails customUserDetails  = ((CustomUserDetails) authentication.getPrincipal());

        Map<String, Object> map = Map.of("nickname", customUserDetails.getNickname());

        String token = jwtUtil.generateToken(customUserDetails.getMemberUUID(), map);

        String res = objectMapper.writeValueAsString(
                CommonResultRecord.successResult(HttpStatus.OK.value(), "로그인을 성공하였습니다.",
                        ResponseRecord.LoginResponseRecord.of(customUserDetails.getMemberUUID(), token)));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(res);
    }
}
