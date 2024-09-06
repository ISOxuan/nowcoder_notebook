package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey = "test:condom";

        redisTemplate.opsForValue().set(redisKey, 2);

        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHashes(){
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"username","章唢呐");

        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));
    }

    //统计20万个重复数据的独立总数
    @Test
    public void testHyperLogLog(){
        String redisKey = "test:hll:01";

        for(int i = 1 ; i <= 100000 ; i++){
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }

        for (int i = 1 ; i <= 100000 ; i++){
            int r = (int)Math.random()*100000 + 1;
            redisTemplate.opsForHyperLogLog().add(redisKey,r);
        }

        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));
    }

    //将2组数据合并，再统计合并后的重复数据的独立总数(存在误差)
    @Test
    public void testHyperLogLogUnion(){
        String redisKey2 = "test:hll:02";
        for(int i = 1 ; i <= 10000 ; i++){
            redisTemplate.opsForHyperLogLog().add(redisKey2,i);
        }
        String redisKey3 = "test:hll:03";
        for(int i = 5001 ; i <= 15000 ; i++){
            redisTemplate.opsForHyperLogLog().add(redisKey3,i);
        }
        String redisKey4 = "test:hll:04";
        for(int i = 10001 ; i <= 20000 ; i++){
            redisTemplate.opsForHyperLogLog().add(redisKey4,i);
        }
        String unionKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey,redisKey2,redisKey3,redisKey4);

        System.out.println(redisTemplate.opsForHyperLogLog().size(unionKey));
    }
}
