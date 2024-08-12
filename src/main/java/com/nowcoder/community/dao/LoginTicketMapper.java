package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {
    // 插入登录凭证
    @Insert({"insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")  // 获取插入数据的主键
    int insertLoginTicket(LoginTicket loginTicket);
    // 查询登录凭证
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);
    // 修改登陆状态
    @Update({
            "<script>",
            "update login_ticket set status = #{status} where ticket = #{ticket}",
            "<if test = \"ticket!=null\">",
            "and 1 = 1",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket,int status);
}
