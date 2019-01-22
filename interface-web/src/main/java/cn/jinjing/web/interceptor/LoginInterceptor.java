package cn.jinjing.web.interceptor;

import cn.jinjing.web.constant.Constant;
import cn.jinjing.web.model.UserInfo;
import cn.jinjing.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * author wzw
 * 登录拦截
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception {
        HttpSession session=request.getSession();
        String parentPath = request.getContextPath();
        String requestUrl = request.getRequestURI().replace(parentPath, "");

        //登录不拦截
        if(requestUrl.startsWith("/doLogin") || requestUrl.startsWith("/login")){
            return true;
        }

        //session中没有有用户信息，跳转到登录
        UserInfo testUser=(UserInfo) session.getAttribute(Constant.CURRENT_USER);
        if(null ==testUser ){
            response.sendRedirect(parentPath+"/login");
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,  Object arg2, ModelAndView arg3) throws Exception {
    }

}
