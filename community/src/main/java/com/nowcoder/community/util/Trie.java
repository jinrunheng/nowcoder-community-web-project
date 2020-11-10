package com.nowcoder.community.util;

public class Trie {

    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public TrieNode getRoot() {
        return root;
    }

    // 向Trie中添加一个敏感词
    public void insertWord(String word) {
        if (word == null) return;

        TrieNode node = root;
        for (int i = 0; i < word.length(); i++) {
            if (!node.subNodes.containsKey(word.charAt(i))) {
                node.subNodes.put(word.charAt(i), new TrieNode());
            }
            node = node.subNodes.get(word.charAt(i));
        }
        node.isEnd = true;
    }
}
