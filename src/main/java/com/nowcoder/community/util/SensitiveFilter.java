package com.nowcoder.community.util;


import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 过滤敏感词
 * 1.定义前缀树（字典树、查找树，查找效率高，消耗内存大）
 * 2.根据敏感词，初始化前缀树
 * 3.编写过滤敏感词的方法
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根结点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        //初始化读取敏感词文件，并将敏感词添加到前缀树中
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive_words");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
            String keyword;
            while((keyword = reader.readLine()) != null){
                //添加到前缀树
                this.addKeyword(keyword);
            }
        }catch(IOException e){
            logger.error("加载敏感词文件失败："+e.getMessage());
        }

    }

    //将一个敏感词添加到前缀树
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for(int i = 0 ; i < keyword.length() ; i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                //初始化子结点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }

            //指向子结点，进入下一轮循环
            tempNode = subNode;

            //设置结束标识
            if(i == keyword.length() - 1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤文本
     * @return     过滤后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        //定义3指针
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();

        while(position < text.length()){
            char c = text.charAt(position);

            //跳过符号
            if(isSymbol(c)){
                //若指针1处于根节点,将此符号计入结果，让指针2走向下一步
                if(tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或中间，指针3都向下走一步
                position++;
                continue;
            }
            //处理正常字符,检查下级节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                //以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //重新指向根节点
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){
                //发现敏感词,将begin到position字符串替换掉
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin = ++position;
            }else{
                position++;
            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol(Character c){
        //0x2E80-0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //定义前缀树
    private class TrieNode {
        //关键词结束标识
        private boolean isKeywordEnd = false;

        //子结点(key是下级字符，value是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子结点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }

        //获取子结点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
