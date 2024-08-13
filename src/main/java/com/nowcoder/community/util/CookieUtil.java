package com.nowcoder.community.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

// Cookie工具类
// 服务器从浏览器中保存的cookie信息中获取ticket(HostHolder类的ThreadLocal代替session实现线程隔离)，然后通过ticket从数据库中查询出对应的用户信息
// 模板引擎渲染对应页面返回给前端
public class CookieUtil {
    public static String getValue(HttpServletRequest request,String name){
        if(request == null || name == null){
            throw new IllegalArgumentException("参数为空！");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
