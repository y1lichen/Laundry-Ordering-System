package com.jefferson.laundryorderingsystem.entities.user;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.jefferson.laundryorderingsystem.entities.reservation.Reservation;

@Entity
@Table(name = "users")
public class User {

    @Id
    private int id;
    @Column(name = "password")
    private String password;
    @Column(name = "credit")
    private int credit;
    @Column(name = "isLogin")
    private boolean isLogin;
    @Column(name = "token")
    private String tokens;

    @OneToMany(mappedBy = "reserveUser")
    @Column(name = "reservations")
    private Set<Reservation> reservations;

    // constructor
    public User() {
    }

    public User(@NotBlank int id, @NotBlank String password) {
        this.id = id;
        this.password = password;
        this.credit = 5;
        this.isLogin = false;
        this.reservations = new HashSet<>();
        this.tokens = null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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

   public String getTokens() {
       return tokens;
   }

   public void setTokens(String tokens) {
       this.tokens = tokens;
   }

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (!(object instanceof User))
            return false;
        User user = (User) object;
        return (Objects.equals(id, user.getId()) &&
                Objects.equals(password, user.getPassword()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, password, isLogin);
    }
}
