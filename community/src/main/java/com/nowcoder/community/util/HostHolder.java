package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户的信息 是一块内存 用于代替Session对象
 */
@Component
public class HostHolder {

    // ThreadLocal保证了线程隔离
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    // 请求结束的时候，清理掉ThreadLocal
    public void clear() {
        users.remove();
    }

}
