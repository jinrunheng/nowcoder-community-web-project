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

// 过滤敏感词
@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换敏感词符号
    private static final String REPLACEMENT = "***";

    // 初始化前缀树
    // 初始化跟节点
    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init() {
        // 将敏感词文件中的字符读入:初始化前缀树
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 将keyword添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败:" + e.getMessage());
        }
    }

    // 将一个敏感词添加到前缀树(初始化前缀树)
    private void addKeyword(String keyword) {

        TrieNode tempNode = root;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            // 将指针tempNode指向子节点，进入下一轮循环
            tempNode = subNode;
            // 设置结束的标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        // 指针1 指向前缀树
        TrieNode tempNode = root;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();
        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)) {
                // 若指针1处于根节点,将此符号计入结果，让指针2向下走一步
                if (tempNode == root) {
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或者是中间，指针3都向下走一步
                position++;
                continue;
            }
            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin为开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 指针1重新指向跟节点
                tempNode = root;
            } else if (tempNode.isKeywordEnd()) {
                // 发现敏感词，将begin ~ position这一段的字符串替换掉
                sb.append(REPLACEMENT);
                // 让指针进入下一个位置
                begin = ++position;
                // 重新指向跟节点
                tempNode = root;
            } else {
                position++;
            }
        }

        // 将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF是东亚文字的范围(中文，韩文...)
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    // 定义前缀树的结构
    private class TrieNode {
        // 关键词结束标识
        private boolean isKeywordEnd = false;

        // 子节点
        // key : 下级的字符
        // value : 下级的节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
