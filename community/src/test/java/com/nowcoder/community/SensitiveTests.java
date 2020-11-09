package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这是一段测试文本，测试敏感词过滤。以下内容为测试文本：我们提供嫖娼，吸毒，卖淫服务。";
        String filterText = sensitiveFilter.filter(text);
        Assertions.assertEquals(filterText,"这是一段测试文本，测试敏感词过滤。以下内容为测试文本：我们提供***，***，***服务。");

        String text1 = "这是一段测试文本，测试敏感词过滤。以下内容为测试文本：我们提供嫖*娼，吸;毒，卖~淫服务!";
        String filterText1 = sensitiveFilter.filter(text1);
        Assertions.assertEquals(filterText1,"这是一段测试文本，测试敏感词过滤。以下内容为测试文本：我们提供***，***，***服务!");
    }
}
