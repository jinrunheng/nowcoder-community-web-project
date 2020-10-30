package com.nowcoder.community.entity;

import lombok.Builder;
import lombok.Data;

/**
 * 封装分页相关的信息
 */
public class Page {
    // 当前的页码
    private int current = 1;
    // 页面最多显示多少条数据
    private int limit = 10;
    // 数据总数(用于计算总的页数)
    private int rows;
    // 查询路径（点击第几页的链接,复用分页链接）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     *
     * @return
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * 用来获取总的页数
     *
     * @return
     */
    public int getTotal() {
        return rows % limit == 0 ? rows / limit : rows / limit + 1;
    }

    /**
     * getFrom ， getTo 从第几页到第几页
     * getFrom 获取起始页码
     *
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * getFrom ， getTo 从第几页到第几页
     * getTo 获取结束页码
     *
     * @return
     */
    public int getTo() {
        int to = current + 2;
        return to > getTotal() ? getTotal() : to;
    }
}
