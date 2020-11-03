package com.nowcoder.community.entity;

import lombok.*;

import java.util.Date;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussPost {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int type; // 帖子的类型：0表示普通；1表示置顶
    private int status;// 帖子的状态：0表示正常；1表示精华；2表示拉黑
    private Date createTime;
    private int commentCount;
    private double score;
}
