package net.zdsoft.cache.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author shenke
 * @since 17-9-10下午12:56
 */
@Entity(name = "base_user")
public class User extends BaseEntity<String> {

    @Column(name = "user_name")
    private String userName;
    private String password;

    @Override
    public String getCacheEntityName() {
        return "user";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
