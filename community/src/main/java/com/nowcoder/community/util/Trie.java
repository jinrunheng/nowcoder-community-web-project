package com.nowcoder.community.util;

import java.util.HashMap;
import java.util.Map;

public class Trie {

    public static class TrieNode {


        // isEnd代表，该节点是否为一个敏感词的结尾
        public boolean isEnd;

        // 子节点
        // key 存储 子节点的val
        // value 存储子节点
        public Map<Character, TrieNode> subNodes;

        public TrieNode() {
            isEnd = false;
            subNodes = new HashMap<>();
        }
    }

    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // 向Trie中添加一个敏感词
    public void insertWord(String word) {
        char[] chars = word.toCharArray();
        TrieNode node = root;
        for (int i = 0; i < chars.length; i++) {
            if (!node.subNodes.containsKey(chars[i])) {
                node.subNodes.put(chars[i], new TrieNode());
            }
            node = node.subNodes.get(chars[i]);
        }
        node.isEnd = true;
    }
}
