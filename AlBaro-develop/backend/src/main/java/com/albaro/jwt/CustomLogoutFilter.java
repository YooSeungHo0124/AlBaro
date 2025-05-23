package com.albaro.jwt;

import com.albaro.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository) {

        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    //얘는 무조건 정의해야하는 메서드라서, 그 메서드 안에 우리가 직접 정의할 메서드 수행되도록 해 놓음
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }


    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //path and method verify(로그아웃 인지 아닌지 확인)
        String requestUri = request.getRequestURI(); //URI의 파라미터값(path 값) 담기

            //로그아웃이 아니면 다음 필터로 넘어가기
            if (!requestUri.matches("^\\/logout$")) {

                filterChain.doFilter(request, response);
                return;
            }
            //POST 경로가 아니면 다음 필터로 넘어가기
            String requestMethod = request.getMethod();
            if (!requestMethod.equals("POST")) {

                filterChain.doFilter(request, response);
                return;
            }

        //get refresh token
        String refresh = null;

            //쿠키에 refresh token 있는지 확인
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {

                if (cookie.getName().equals("refresh")) {

                    refresh = cookie.getValue();
                }
            }

            //refresh null check
            if (refresh == null) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //로그아웃 진행

            //Refresh 토큰 DB에서 제거
            refreshRepository.deleteByRefresh(refresh);

            //Refresh 토큰 Cookie 값 0
            //쿠키에 저장되어 있는 refresh token을 null값으로 바꿔줌
            Cookie cookie = new Cookie("refresh", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");

            response.addCookie(cookie);
            response.setStatus(HttpServletResponse.SC_OK);

    }
}
