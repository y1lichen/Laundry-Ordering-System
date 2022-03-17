package com.jefferson.laundryorderingsystem.entities.user;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "school_id")
    private String schoolId;
    @Column(name = "user_password")
    private String password;
    @Column(name = "user_credit")
    private int credit;
    @Column(name = "user_is_login")
    private boolean isLogin;

    // constructor
    public User() {
    }
    public User(@NotBlank String schoolId, @NotBlank String password) {
        this.schoolId = schoolId;
        this.password = password;
        this.credit = 5;
        this.isLogin = false;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public boolean getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean loggedIn) {
        this.isLogin = loggedIn;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof User)) return false;
        User user = (User) object;
        return (Objects.equals(schoolId, user.getSchoolId()) &&
                Objects.equals(password, user.getPassword()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, schoolId, password, isLogin);
    }
}
