package com.jefferson.laundryorderingsystem.entities.reservation;

import java.sql.Date;

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

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int num;

    @Column(name = "time")
    private Date time;

    @Column(name = "machine")
    private int machine;

    @Column(name = "user")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User reserveUser;


    // constructor
    public Reservation() {}

    public Reservation(@NotBlank Date time, @NotBlank User user) {
        this.time = time;
        this.reserveUser = user;
    }
}
