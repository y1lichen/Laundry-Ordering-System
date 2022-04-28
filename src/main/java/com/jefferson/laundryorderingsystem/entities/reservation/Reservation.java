package com.jefferson.laundryorderingsystem.entities.reservation;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.jefferson.laundryorderingsystem.entities.user.User;

@Entity(name = "reservations")
@Table(name = "reservations")
public class Reservation {

    // total amount of laundry machine
    public static final int totalMachine = 18;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "machine")
    private int machine;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User reserveUser;

    // constructor
    public Reservation() {
    }

    public Reservation(@NotBlank LocalDateTime time, @NotBlank User user, @NotBlank int machine) {
        this.time = time;
        this.reserveUser = user;
        this.machine = machine;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getMachine() {
        return machine;
    }

    public void setMachine(int machine) {
        this.machine = machine;
    }

    public User getReserveUser() {
        return reserveUser;
    }

    public void setReserveUser(User reserveUser) {
        this.reserveUser = reserveUser;
    }

}
