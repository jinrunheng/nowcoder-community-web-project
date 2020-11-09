package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class ImprovedSensitiveFilter {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(ImprovedSensitiveFilter.class);

    // 替换敏感词符号
    private static final String REPLACEMENT = "***";

    //初始化Trie
    private Trie trie = new Trie();

    // 将敏感词文件中的字符串插入到前缀树中
    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 将keyword添加到前缀树
                trie.insertWord(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败:" + e.getMessage());
        }
    }

    /**
     * 对敏感词做处理
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        return null;
    }

}
