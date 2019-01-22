package cn.jinjing.web.controller;

import cn.jinjing.web.constant.Constant;
import cn.jinjing.web.model.UserInfo;
import cn.jinjing.web.service.UserService;
import cn.jinjing.web.util.MD5;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping("/doLogin")
    public JSONObject login(HttpServletRequest request,HttpServletResponse response,HttpSession session,String username,String userpwd){
        response.setHeader("Access-Control-Allow-Origin", "*");
        JSONObject jSONObject = new JSONObject();
        String message = "";
        String flag = "error";

        UserInfo user = new UserInfo();
        user.setUserCode(username);
        user.setPasswd(MD5.cell32(userpwd));

        UserInfo userInfo = userService.login(user);

        if(userInfo != null) {
            session.setAttribute(Constant.CURRENT_USER, userInfo);
            flag = "success";
        }else {
            flag = "error";
            message = "用户名或密码错误";
        }
        jSONObject.put("head",flag);
        jSONObject.put("message",message);
        return jSONObject;
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request,HttpServletResponse response,HttpSession session) {

        //废止session
        session.invalidate();

        //清除cookie
        Cookie ckUserName = new Cookie("userName","");// user是代表用户的bean
        ckUserName.setMaxAge(60 * 60 * 24 * 99999999);//设置Cookie有效期为7天
        response.addCookie(ckUserName);
        Cookie ckPswd = new Cookie("pswd","");
        ckPswd.setMaxAge(60 * 60 * 24 * 99999999);
        response.addCookie(ckPswd);

        return "/login";

    }

}
