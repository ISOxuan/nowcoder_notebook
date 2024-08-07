package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //分页查询(offset表示每页起始行号，limit表示每页显示多少条数据)
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //@Param注解用于给参数取别名
    //如果只有一个参数，并且在<if>里使用，则必须加别名(动态sql)
    int selectDiscussPostRows(@Param("userId") int userId);
}
