package cn.jinjing.web.init;

import cn.jinjing.web.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 实例化自定义拦截器
     * author wzw
     * @return LoginInterceptor
     */
    @Bean
    public LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }

    /**
     * 添加拦截器
     * author wzw
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                //需要配置2：----------- 告知拦截器：/static/admin/** 与 /static/user/** 不需要拦截 （配置的是 路径）
                		.excludePathPatterns("/static/**", "/image/**");
//                .excludePathPatterns(Arrays.asList("/static/**","/image/**"));
    }

    /**
     * 无需手动编写controller就能直接跳转到页面
     *author wzw
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/login");
        registry.addViewController("/index").setViewName("/login");
        registry.addViewController("/login").setViewName("/login");
        registry.addViewController("/home").setViewName("/home");
        registry.addViewController("/userInfo").setViewName("/user/user");
        registry.addViewController("/addLabel").setViewName("/label/addLabel");
        registry.addViewController("/labelConfig").setViewName("/labelConfig");
        registry.addViewController("/labelLog").setViewName("/log/labelLog");
        registry.addViewController("/labelLogPerMon").setViewName("/log/LabelLogPerMon");
        registry.addViewController("/sourceLog").setViewName("/log/sourceLog");
        registry.addViewController("/sourceLogPerMon").setViewName("/log/sourceLogPerMon");
    }




}
