package hello.springmvc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Override
//    public void addCorsMappings(CorsRegistry registry){
//        registry.addMapping("/**") // 모든 엔드포인트에 대해
//                .allowedOrigins("http://localhost:3000") // 허용할 도메인
//                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드
//                .allowedHeaders("*") // 모든 헤더 허용
//                .allowCredentials(true); // 인증 정보 허용 (쿠키, Authorization 헤더 등)
//
//    }

}
