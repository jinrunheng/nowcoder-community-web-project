package com.nowcoder.community.util;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    public boolean isEnd;
    public Map<Character, TrieNode> subNodes;

    public TrieNode() {
        isEnd = false;
        subNodes = new HashMap<>();
    }
}
