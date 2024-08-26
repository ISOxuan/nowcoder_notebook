package com.nowcoder.community.controller;

import com.google.protobuf.ByteString;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userservice;

    @Autowired
    private HostHolder hostHolder;
    @LoginRequired
    @RequestMapping(path="/setting",method= RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    /**
     * 上传用户头像
     * 请求：必须是POST请求
     * 表单：enctype="multipart/form-data"
     * Spring MVC：通过MultipartFile对象封装上传的文件
     * 开发步骤：访问账号设置页面->上传头像->获取头像
     * @return
     */
    @LoginRequired
    @RequestMapping(path="/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error","您还没有选择图片！！");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix =fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确！！");
            return "/site/setting";
        }

        //生成随机文件名
        fileName = CommunityUtil.generateUUID()+suffix;

        //确定文件存放的路径
        //--/home/ztx/images/fileName
        File dest = new File(uploadPath+"/"+fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        }catch (IOException e){
            logger.error("上传文件失败："+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！",e);
        }

        //更新当前用户的头像路径（web访问路径）
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain+contextPath+"/user/header/"+fileName;
        userservice.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    /**
     * TODO
     * @param oldPassword
     * @param newPassword
     * @param model
     * @return
     */
    //path = "http://localhost:8080/community/user/setting?oldpassword=asdqwe123&newpassword=asd"
    @RequestMapping(path = "/setting", method = RequestMethod.POST)
    public String updatePassword(@RequestParam("oldpassword") String oldPassword,@RequestParam("newpassword") String newPassword,@RequestParam("confirmpassword")String confirmPassword , Model model,@CookieValue("ticket") String ticket) {
        if (oldPassword == null) {
            model.addAttribute("oldPasswordMsg", "请输入原始密码!");
            return "site/setting";
        }
        if (newPassword == null) {
            model.addAttribute("newPasswordMsg", "请输入新密码!");
            return "site/setting";
        }
        if (confirmPassword == null) {
            model.addAttribute("confirmPasswordMsg", "请确认新密码!");
            return "site/setting";
        }

        User user = hostHolder.getUser();
        if (!CommunityUtil.md5(oldPassword + user.getSalt()).equals(user.getPassword())) {
            model.addAttribute("oldPasswordMsg", "密码错误!");
            return "/site/setting";
        }
        if (!confirmPassword.equals(newPassword)) {
            model.addAttribute("confirmPasswordMsg", "两次输入的密码不一致!");
            return "site/setting";
        }
        userservice.updatePassword(user.getId(), CommunityUtil.md5(newPassword + user.getSalt()));
        userservice.logout(ticket);
        return "redirect:/login";
    }
}