package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DateService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DateService dateService;

    @Autowired
    private HostHolder hostHolder;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计UV（ip）
        String ip = request.getRemoteHost();
        dateService.recordUV(ip);

        //统计DAU(userId)
        User user = hostHolder.getUser();
        if(user != null){
            dateService.recordDAU(user.getId());
        }
        return true;
    }

}
