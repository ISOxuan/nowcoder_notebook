package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //分页查询(offset表示每页起始行号，limit表示每页显示多少条数据,orderMode为1，帖子列表按热度排序)
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    //@Param注解用于给参数取别名
    //如果只有一个参数，并且在<if>里使用，则必须加别名(动态sql)
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    //更新帖子评论数量
    int updateCommentCount(int id,int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}
